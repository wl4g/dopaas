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
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.apache.commons.lang3.exception.ExceptionUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.Assert;

import com.wl4g.devops.shell.bean.MetaMessage;
import com.wl4g.devops.shell.bean.ExceptionMessage;
import com.wl4g.devops.shell.bean.LineMessage;
import com.wl4g.devops.shell.bean.ResultMessage;
import com.wl4g.devops.shell.config.ShellProperties;
import com.wl4g.devops.shell.handler.ChannelMessageHandler;
import com.wl4g.devops.shell.registry.ShellBeanRegistry;

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

	/**
	 * Executor service
	 */
	final private ExecutorService worker;

	/**
	 * Server sockets
	 */
	private ServerSocket ss;

	/**
	 * Boss boot threads
	 */
	private Thread boss;

	public EmbeddedServerProcessor(ShellProperties config, ShellBeanRegistry registry) {
		super(config, registry);
		this.worker = Executors.newFixedThreadPool(config.getConcurrently(), new ThreadFactory() {
			final private AtomicInteger counter = new AtomicInteger(0);

			@Override
			public Thread newThread(Runnable r) {
				String prefix = EmbeddedServerProcessor.class.getSimpleName() + "-" + counter.incrementAndGet();
				Thread t = new Thread(r, prefix);
				t.setDaemon(true);
				return t;
			}
		});
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (running.compareAndSet(false, true)) {
			Assert.state(ss == null, "server socket already listen ?");

			int bindPort = -1;
			for (int retryPort = config.getBeginPort(); retryPort <= config.getEndPort(); retryPort++) {
				try {
					ss = new ServerSocket(retryPort, config.getBacklog(), config.getInetBindAddr());
					bindPort = retryPort;
					break;
				} catch (BindException e) {
				}
			}
			ss.setSoTimeout(0); // Infinite timeout

			if (log.isInfoEnabled()) {
				log.info("Shell Console started on port(s): {}", bindPort);
			}
			this.boss = new Thread(this);
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
				worker.shutdownNow();
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
					log.debug("On accept socket: {}", s);
				}

				worker.submit(new ShellHandler(registry, s, line -> {
					return process(line); // Processing
				}).starting());

			} catch (Throwable e) {
				log.warn("Shell boss thread shutdown. cause: {}", getStackTrace(e));
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

		public ShellHandler(ShellBeanRegistry registry, Socket client, Function<String, Object> function) {
			super(registry, client, function);
		}

		@Override
		public ChannelMessageHandler starting() {
			run(); // Running
			return this;
		}

		@Override
		public void run() {
			while (running.get() && isActive()) {
				try {
					Object result = null;

					// To a string command line
					Object input = new ObjectInputStream(_in).readObject();

					if (log.isInfoEnabled()) {
						log.info("CLI receive: {}", input);
					}

					// Submit line
					if (input instanceof LineMessage) {
						LineMessage line = (LineMessage) input;
						// Processing
						result = new ResultMessage(line.getProcessId(), function.apply(line.getLine()).toString());
					}
					// Request registed commands
					else if (input instanceof MetaMessage) {
						// Write registed target methods commands
						result = new MetaMessage(registry.getTargetMethods());
					}

					// Echo
					if (result != null) {
						if (log.isInfoEnabled()) {
							log.info("CLI processed: {}", result);
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
				close();
			} else {
				try {
					String errmsg = getRootCauseMessage(th);
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
