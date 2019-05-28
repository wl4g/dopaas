/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.shell.processor;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.apache.commons.lang3.exception.ExceptionUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.Assert;

import com.wl4g.devops.shell.bean.MetaMessage;
import com.wl4g.devops.shell.bean.ExceptionMessage;
import com.wl4g.devops.shell.bean.InterruptMessage;
import com.wl4g.devops.shell.bean.LineMessage;
import com.wl4g.devops.shell.bean.ResultMessage;
import com.wl4g.devops.shell.config.ShellProperties;
import com.wl4g.devops.shell.exception.TooManyConnectionsException;
import com.wl4g.devops.shell.handler.ChannelMessageHandler;
import com.wl4g.devops.shell.processor.ShellContext.EventListener;
import com.wl4g.devops.shell.registry.ShellBeanRegistry;
import com.wl4g.devops.shell.registry.TargetMethodWrapper;
import com.wl4g.devops.shell.utils.ShellContextHolder;

/**
 * Socket server shell processor
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月1日
 * @since
 */
public class EmbeddedServerProcessor extends AbstractProcessor implements ApplicationRunner, Runnable {

	/**
	 * Current server running status.
	 */
	final private AtomicBoolean running = new AtomicBoolean(false);

	/** Execution workers */
	final private ConcurrentMap<ShellHandler, Thread> workers = new ConcurrentHashMap<>();

	/**
	 * Server sockets
	 */
	private ServerSocket ss;

	/**
	 * Boss boot threads
	 */
	private Thread boss;

	public EmbeddedServerProcessor(ShellProperties config, String appName, ShellBeanRegistry registry) {
		super(config, appName, registry);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (running.compareAndSet(false, true)) {
			Assert.state(ss == null, "server socket already listen ?");

			int bindPort = ensureDetermineServPort(getAppName());

			ss = new ServerSocket(bindPort, getConfig().getBacklog(), getConfig().getInetBindAddr());
			ss.setSoTimeout(0); // Infinite timeout
			if (log.isInfoEnabled()) {
				log.info("Shell Console started on port(s): {}", bindPort);
			}

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
		if (running.compareAndSet(true, false)) {
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
				Iterator<ShellHandler> it = workers.keySet().iterator();
				while (it.hasNext()) {
					ShellHandler h = it.next();
					Thread t = workers.get(h);
					t.interrupt();
					t = null;
					it.remove();
				}
			} catch (Exception e) {
				log.error("Closing worker failure", e);
			}
		}
	}

	@Override
	public void run() {
		while (!boss.isInterrupted() && running.get()) {
			try {
				// Receiving client socket(blocking)
				Socket s = ss.accept();
				if (log.isDebugEnabled()) {
					log.debug("On accept socket: {}, maximum: {}, actual: {}", s, getConfig().getMaxClients(), workers.size());
				}

				// Check many connections.
				if (workers.size() >= getConfig().getMaxClients()) {
					String errmsg = String.format("There are too many parallel shell connections. maximum: %s, actual: %s",
							getConfig().getMaxClients(), workers.size());
					log.warn(errmsg);
					// Print error message.
					ChannelMessageHandler.writeAndFlush(s.getOutputStream(), new TooManyConnectionsException(errmsg));
					s.close();
					continue;
				}

				// Processing
				ShellHandler handler = bind(new ShellHandler(registry, s, line -> process(line)));

				// The worker thread may not be the parent thread of Runnable,
				// so you need to display bind to the thread in the afternoon
				// again.
				Thread channel = new Thread(() -> bind(handler).run(), getClass().getSimpleName() + "-channel-" + workers.size());
				channel.setDaemon(true);
				workers.put(handler, channel);
				channel.start();

			} catch (Throwable e) {
				log.warn("Shell boss thread shutdown. cause: {}", getStackTrace(e));
			}
		}
	}

	@Override
	protected void preProcessParameters(TargetMethodWrapper tm, List<Object> args) {
		// Get shellContext
		ShellContext context = getClient().getContext();

		// Default initialize
		ShellContextHolder.bind(context);

		// Find ShellContext parameter index
		int index = findParameterTypeIndex(tm, ShellContext.class);
		if (index >= 0) {
			// Overwrite parameters
			if (index < args.size()) {
				args.add(index, context);
			} else {
				args.add(context);
			}
		}

	}

	/**
	 * Server shell handler
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月2日
	 * @since
	 */
	class ShellHandler extends ChannelMessageHandler {

		final protected Logger log = LoggerFactory.getLogger(getClass());

		/** Shell context */
		final ShellContext context;

		/** Execution worker */
		final private ExecutorService worker;

		public ShellHandler(ShellBeanRegistry registry, Socket client, Function<String, Object> function) {
			super(registry, client, function);
			this.context = new ShellContext(this);

			this.worker = new ThreadPoolExecutor(1, getConfig().getConcurrently(), 0, TimeUnit.SECONDS,
					new LinkedBlockingDeque<>(64), new ThreadFactory() {
						final private AtomicInteger counter = new AtomicInteger(0);

						@Override
						public Thread newThread(Runnable r) {
							String prefix = ShellHandler.class.getSimpleName() + "-channel-worker-" + counter.incrementAndGet();
							Thread t = new Thread(r, prefix);
							t.setDaemon(true);
							return t;
						}
					});
		}

		public ShellContext getContext() {
			return context;
		}

		@Override
		public void run() {
			while (running.get() && isActive()) {
				try {
					// To a string command line
					Object input = new ObjectInputStream(_in).readObject();
					if (log.isInfoEnabled()) {
						log.info("<= {}", input);
					}

					Object result = null;
					// Register message
					if (input instanceof MetaMessage) {
						// Target methods
						result = new MetaMessage(registry.getTargetMethods());
					}
					// Interrupt message
					else if (input instanceof InterruptMessage) {
						// Execution event.
						EventListener listener = context.getEventListener();
						if (listener != null) {
							listener.onInterrupt();
						}
					}
					// Commands message
					else if (input instanceof LineMessage) {
						LineMessage line = (LineMessage) input;
						// Resolve that client input cannot be received during
						// blocking execution.
						worker.execute(() -> {
							try {
								Object ret = function.apply(line.getLine());
								if (ret != null) {
									if (log.isInfoEnabled()) {
										log.info("=> {}", ret);
									}
									writeAndFlush(new ResultMessage(context.getState(), ret.toString()));
								}
							} catch (Throwable e) {
								handleThorws(e);
							}
						});
					}

					if (result != null) { // Echo
						if (log.isInfoEnabled()) {
							log.info("=> {}", result);
						}
						writeAndFlush(result);
					}

				} catch (Throwable th) {
					handleThorws(th);
				} finally {
					try {
						Thread.sleep(100L);
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
			Thread t = workers.remove(this);
			if (t != null) {
				t.interrupt();
				t = null;
			}
			if (log.isDebugEnabled()) {
				log.debug("Remove shellHandler: {}, actual: {}", this, workers.size());
			}
		}

		/**
		 * Handling throws
		 * 
		 * @param th
		 */
		private void handleThorws(Throwable th) {
			if ((th instanceof SocketException) || (th instanceof EOFException) || !isActive()) {
				if (log.isWarnEnabled()) {
					log.warn("Disconnect for client: {}", client);
				}
				try {
					close();
				} catch (IOException e) {
					log.error("Close failure.", e);
				}
			} else {
				try {
					String errmsg = getRootCauseMessage(th);
					errmsg = isBlank(errmsg) ? getMessage(th) : errmsg;
					if (log.isWarnEnabled()) {
						log.warn("{}", errmsg);
					}
					writeAndFlush(new ExceptionMessage(th));
				} catch (IOException e) {
					log.warn("Echo failure", e);
				}
			}
		}

	}

}
