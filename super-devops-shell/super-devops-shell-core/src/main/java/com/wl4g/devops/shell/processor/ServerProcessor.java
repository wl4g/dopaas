package com.wl4g.devops.shell.processor;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
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

import com.wl4g.devops.shell.bean.CommandMessage;
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
public class ServerProcessor extends AbstractProcessor implements ApplicationRunner, Runnable {

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

	public ServerProcessor(ShellProperties config, ShellBeanRegistry registry) {
		super(config, registry);
		this.worker = Executors.newFixedThreadPool(config.getConcurrently(), new ThreadFactory() {
			final private AtomicInteger counter = new AtomicInteger(0);

			@Override
			public Thread newThread(Runnable r) {
				String prefix = ServerProcessor.class.getSimpleName() + "-" + counter.incrementAndGet();
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

			ss = new ServerSocket(config.getPort(), config.getBacklog(), config.getInetBindAddr());
			ss.setSoTimeout(0); // Infinite timeout

			if (log.isInfoEnabled()) {
				log.info("Sheller started on port(s): {}", config.getPort());
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

					if (log.isDebugEnabled()) {
						log.debug("Receving input: {}", input);
					}

					// Submit line
					if (input instanceof LineMessage) {
						LineMessage line = (LineMessage) input;
						// Processing
						result = new ResultMessage(function.apply(line.getLine()).toString());
					}
					// Request registed commands
					else if (input instanceof CommandMessage) {
						// Write registed target methods commands
						result = new CommandMessage(registry.getTargetMethods());
					}

					// Echo
					if (result != null) {
						if (log.isDebugEnabled()) {
							log.debug("Processing result: {}", result);
						}
						writeAndFlush(result);
					}
				} catch (Throwable th) {
					handleThorws(th);
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
						log.warn("Processing failure, cause: {}", errmsg);
					}
					writeAndFlush(new ResultMessage(getStackTrace(th)));
				} catch (IOException e) {
					log.warn("Echo client failure", e);
				}
			}
		}

	}

}
