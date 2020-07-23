/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.components.shell.handler;

import com.wl4g.devops.components.shell.config.ShellProperties;
import com.wl4g.devops.components.shell.handler.ShellMessageChannel;
import com.wl4g.devops.components.shell.registry.ShellHandlerRegistrar;
import com.wl4g.devops.components.shell.registry.TargetMethodWrapper;
import com.wl4g.devops.components.shell.signal.*;

import org.slf4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.Assert;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.wl4g.devops.components.shell.signal.ChannelState.*;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.lang.Thread.sleep;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.exception.ExceptionUtils.*;
import static org.springframework.util.Assert.state;

/**
 * Socket server shell processor
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月1日
 * @since
 */
public class EmbeddedServerShellHandler extends AbstractServerShellHandler implements ApplicationRunner, Runnable {

	/**
	 * Current server shellRunning status.
	 */
	final private AtomicBoolean shellRunning = new AtomicBoolean(false);

	/** Command channel workers. */
	final private Map<ServerShellMessageChannel, Thread> channels = new ConcurrentHashMap<>();

	/**
	 * Server sockets
	 */
	private ServerSocket ss;

	/**
	 * Boss thread
	 */
	private Thread boss;

	public EmbeddedServerShellHandler(ShellProperties config, String appName, ShellHandlerRegistrar registry) {
		super(config, appName, registry);
	}

	// Start server
	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (shellRunning.compareAndSet(false, true)) {
			Assert.state(ss == null, "server socket already listen ?");

			// Determine server port.
			int bindPort = ensureDetermineServPort(getAppName());

			ss = new ServerSocket(bindPort, getConfig().getBacklog(), getConfig().getInetBindAddr());
			ss.setSoTimeout(0); // Infinite timeout
			log.info("Shell Console started on port(s): {}", bindPort);

			this.boss = new Thread(this, getClass().getSimpleName() + "-boss");
			this.boss.setDaemon(true);
			this.boss.start();
		}
	}

	@Override
	public void destroy() throws Exception {
		close();
	}

	protected void close() {
		if (shellRunning.compareAndSet(true, false)) {
			try {
				boss.interrupt();
			} catch (Exception e) {
				log.error("Interrupting boss failure", e);
			}

			if (ss != null && !ss.isClosed()) {
				try {
					ss.close();
				} catch (IOException e) {
					log.error("Closing server failure", e);
				}
			}

			try {
				Iterator<ServerShellMessageChannel> it = channels.keySet().iterator();
				while (it.hasNext()) {
					ServerShellMessageChannel h = it.next();
					Thread t = channels.get(h);
					t.interrupt();
					t = null;
					it.remove();
				}
			} catch (Exception e) {
				log.error("Closing worker failure", e);
			}
		}
	}

	// Accepting channel connect
	@Override
	public void run() {
		while (!boss.isInterrupted() && shellRunning.get()) {
			try {
				// Receiving client socket(blocking)
				Socket s = ss.accept();
				log.debug("On accept socket: {}, maximum: {}, actual: {}", s, getConfig().getMaxClients(), channels.size());

				// Check many connections.
				if (channels.size() >= getConfig().getMaxClients()) {
					log.warn(String.format("There are too many parallel shell connections. maximum: %s, actual: %s",
							getConfig().getMaxClients(), channels.size()));
					s.close();
					continue;
				}

				// Create shell channel
				ServerShellMessageChannel channel = new ServerShellMessageChannel(registrar, s, line -> process(line));

				// MARK1:
				// The worker thread may not be the parent thread of Runnable,
				// so you need to display bind to the thread in the afternoon
				// again.
				Thread channelTask = new Thread(() -> bind(channel).run(),
						getClass().getSimpleName() + "-channel-" + channels.size());
				channelTask.setDaemon(true);
				channels.put(channel, channelTask);
				channelTask.start();

			} catch (Throwable e) {
				log.warn("Shell boss thread shutdown. cause: {}", getStackTrace(e));
			}
		}
	}

	@Override
	protected void preHandleInput(TargetMethodWrapper tm, List<Object> args) {
		// Get current context
		AbstractShellContext context = getClient().getContext();

		// Bind target method
		context.setTarget(tm);

		// Resolving args with {@link AbstractShellContext}
		AbstractShellContext updateContext = resolveInjectArgsForShellContextIfNecceary(context, tm, args);
		// Inject update actual context
		getClient().setContext(updateContext);
	}

	/**
	 * If necessary, resolving whether the shell method parameters have
	 * {@link AbstractShellContext} instances and inject.
	 * 
	 * @param context
	 * @param tm
	 * @param args
	 */
	private AbstractShellContext resolveInjectArgsForShellContextIfNecceary(AbstractShellContext context, TargetMethodWrapper tm,
			List<Object> args) {

		// Find parameter: ShellContext index and class
		Object[] ret = findParameterForShellContext(tm);
		int index = (int) ret[0];
		Class<?> contextClass = (Class<?>) ret[1];

		if (index >= 0) { // have ShellContext?
			// Convert to specific shellContext
			if (SimpleShellContext.class.isAssignableFrom(contextClass)) {
				context = new SimpleShellContext(context);
			} else if (ProgressShellContext.class.isAssignableFrom(contextClass)) {
				context = new ProgressShellContext(context);
			}
			if (index < args.size()) { // Correct parameter index
				args.add(index, context);
			} else {
				args.add(context);
			}

			/**
			 * When injection {@link ShellContext} is used, the auto open
			 * channel status is wait.
			 */
			context.begin(); // MARK2
		}

		return context;
	}

	/**
	 * Get {@link ShellContext} index by parameters classes.
	 * 
	 * @param tm
	 * @param clazz
	 * @return
	 */
	private Object[] findParameterForShellContext(TargetMethodWrapper tm) {
		int index = -1, i = 0;
		Class<?> contextCls = null;
		for (Class<?> cls : tm.getMethod().getParameterTypes()) {
			if (ShellContext.class.isAssignableFrom(cls)) {
				state(index < 0, format("Multiple shellcontext type parameters are unsupported. %s", tm.getMethod()));
				index = i;
				contextCls = cls;
			}
			++i;
		}
		return new Object[] { index, contextCls };
	}

	/**
	 * Server shell message channel handler
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月2日
	 * @since
	 */
	class ServerShellMessageChannel extends ShellMessageChannel {
		final protected Logger log = getLogger(getClass());

		/** Current single command worker */
		final private ExecutorService currentWorker;

		/** Current shell context */
		AbstractShellContext currentContext;

		public ServerShellMessageChannel(ShellHandlerRegistrar registrar, Socket client, Function<String, Object> func) {
			super(registrar, client, func);
			this.currentContext = new AbstractShellContext(this) {
			};
			this.currentWorker = new ThreadPoolExecutor(1, 1, 0, SECONDS, new LinkedBlockingDeque<>(1), new ThreadFactory() {
				final private AtomicInteger counter = new AtomicInteger(0);

				@Override
				public Thread newThread(Runnable r) {
					String prefix = getClass().getSimpleName() + "-worker-" + counter.incrementAndGet();
					Thread t = new Thread(r, prefix);
					t.setDaemon(true);
					return t;
				}
			});
		}

		AbstractShellContext getContext() {
			return currentContext;
		}

		void setContext(AbstractShellContext context) {
			notNullOf(context, "ShellContext");
			this.currentContext = context;
		}

		@Override
		public void run() {
			while (running.get() && isActive()) {
				try {
					Object stdin = new ObjectInputStream(_in).readObject();
					log.info("<= {}", stdin);

					Object output = null;
					// Register shell methods
					if (stdin instanceof MetaSignal) {
						output = new MetaSignal(registrar.getTargetMethods());
					}
					// Ask interruption
					else if (stdin instanceof PreInterruptSignal) {
						// Call pre-interrupt events.
						currentContext.getUnmodifiableEventListeners().forEach(l -> l.onPreInterrupt(currentContext));
						// Ask if the client is interrupt.
						output = new AskInterruptSignal("Are you sure you want to cancel execution? (y|n)");
					}
					// Confirm interruption
					else if (stdin instanceof AckInterruptSignal) {
						AckInterruptSignal ack = (AckInterruptSignal) stdin;
						// Call interrupt events.
						currentContext.getUnmodifiableEventListeners()
								.forEach(l -> l.onInterrupt(currentContext, ack.getConfirm()));
					}
					// Stdin of commands
					else if (stdin instanceof StdinSignal) {
						StdinSignal cmd = (StdinSignal) stdin;
						// Call command events.
						currentContext.getUnmodifiableEventListeners().forEach(l -> l.onCommand(currentContext, cmd.getLine()));

						// Resolve that client input cannot be received during
						// blocking execution.
						currentWorker.execute(() -> {
							try {
								/**
								 * Only {@link ShellContext} printouts are
								 * supported, and return value is no longer
								 * supported (otherwise it will be ignored)
								 */
								function.apply(cmd.getLine());

								/**
								 * see:{@link EmbeddedServerShellHandler#preHandleInput()}#MARK2
								 */
								if (currentContext.getState() != RUNNING) {
									currentContext.completed();
								}
							} catch (Throwable e) {
								log.error(format("Failed to handle shell command: [%s]", cmd.getLine()), e);
								handleError(e);
							}
						});
					}

					if (nonNull(output)) { // Write to console.
						currentContext.printf0(output);
					}
				} catch (Throwable th) {
					handleError(th);
				} finally {
					try {
						sleep(100L);
					} catch (InterruptedException e) {
					}
				}
			}
		}

		@Override
		public void close() throws IOException {
			// Prevent threadContext memory leakage.
			cleanup();

			// Close the current socket
			super.close();

			// Clear the current channel
			Thread t = channels.remove(this);
			if (t != null) {
				t.interrupt();
				t = null;
			}
			log.debug("Remove shellHandler: {}, actual: {}", this, channels.size());
		}

		/**
		 * Error handling
		 * 
		 * @param th
		 */
		private void handleError(Throwable th) {
			if ((th instanceof SocketException) || (th instanceof EOFException) || !isActive()) {
				log.warn("Disconnect for client : {}", socket);
				try {
					close();
				} catch (IOException e) {
					log.error("Close failure.", e);
				}
			} else {
				currentContext.printf0(th);
			}
		}

	}

}