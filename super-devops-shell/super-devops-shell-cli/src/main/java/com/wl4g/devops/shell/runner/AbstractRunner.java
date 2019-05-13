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
package com.wl4g.devops.shell.runner;

import static java.lang.System.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import org.jline.reader.LineReader;
import static org.jline.reader.LineReader.*;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;

import static org.apache.commons.lang3.exception.ExceptionUtils.*;
import static org.apache.commons.lang3.SystemUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

import static com.wl4g.devops.shell.config.DefaultBeanRegistry.getSingle;
import static com.wl4g.devops.shell.cli.InternalCommand.*;
import static com.wl4g.devops.shell.utils.LineUtils.*;
import static com.wl4g.devops.shell.annotation.ShellOption.*;
import com.wl4g.devops.shell.command.DefaultInternalCommand;
import com.wl4g.devops.shell.AbstractActuator;
import com.wl4g.devops.shell.bean.MetaMessage;
import com.wl4g.devops.shell.bean.ExceptionMessage;
import com.wl4g.devops.shell.bean.LineMessage;
import com.wl4g.devops.shell.bean.ResultMessage;
import com.wl4g.devops.shell.config.Configuration;
import com.wl4g.devops.shell.config.DynamicCompleter;
import com.wl4g.devops.shell.handler.ChannelMessageHandler;
import com.wl4g.devops.shell.registry.ShellBeanRegistry;
import com.wl4g.devops.shell.utils.Assert;
import com.wl4g.devops.shell.utils.LineUtils;

/**
 * Abstract shell component runner
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public abstract class AbstractRunner extends AbstractActuator implements Runner {

	/**
	 * The PID used to get the set target service, that is, the server that the
	 * current shell client will connect to (because the same computer may start
	 * many different shell services)
	 */
	final public static String ARG_SERV_PIDS = "servpids";

	/**
	 * IBid, note that this priority is higher than ARG_SERV_PIDS
	 */
	final public static String ARG_SERV_POINT = "servpoint";

	/**
	 * Commands prompt string.
	 */
	final public static String ARG_PROMPT = "prompt";

	/**
	 * Enable debugging
	 */
	final public static boolean DEBUG = System.getProperty("debug") != null;

	/**
	 * Attributed string
	 */
	final public static AttributedString DEFAULT_ATTRIBUTED = new AttributedString("console> ");

	/**
	 * Shell configuration
	 */
	final protected Configuration config;

	/**
	 * Line reader
	 */
	final protected LineReader lineReader;

	/**
	 * Shell client handler
	 */
	private ClientHandler client;

	/**
	 * Current process exception statcktrace as strings.
	 */
	private String stacktraceAsString;

	public AbstractRunner(Configuration config) {
		super(getSingle());
		Assert.notNull(config, "configuration is null, please check configure");
		this.config = config;

		// Build lineReader
		try {
			this.lineReader = LineReaderBuilder.builder().appName("DevOps Shell Cli").completer(new DynamicCompleter(getSingle()))
					.terminal(TerminalBuilder.terminal()).build();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		// Initialization
		try {
			initialize();
		} catch (Throwable t) {
			printErr(EMPTY, t);
			shutdown(EMPTY);
		}
	}

	public ShellBeanRegistry getRegistry() {
		return registry;
	}

	public LineReader getLineReader() {
		return lineReader;
	}

	@Override
	public void shutdown(String line) {
		try {
			out.println("Shutting down, bye...");

			// Close client
			closeQuietly();

			// Gracefully halt
			exit(0);

		} catch (Throwable e) {
			printErr("Shutdown failure.", e);
		}
	}

	/**
	 * Print exceptions
	 * 
	 * @param th
	 * @param details
	 */
	public void printErr(String abnormal, Throwable th) {
		this.stacktraceAsString = getStackTrace(th);
		err.println(String.format("%s %s", abnormal, getRootCauseMessage(th)));
	}

	/**
	 * Get last abnormal stacktrace string
	 * 
	 * @return
	 */
	public String getLastStacktrace() {
		return this.stacktraceAsString;
	}

	/**
	 * Submission task to remote
	 * 
	 * @param line
	 * @throws IOException
	 */
	protected void submit(Object message) throws IOException {
		// Ensure client
		ensureClient();

		if (message instanceof String) {
			// Exec internal commands
			String line = (String) message;
			List<String> cmds = parse(line);
			if (!cmds.isEmpty()) {
				// $> [help|clear|history...]
				if (registry.contains(cmds.get(0))) {
					DefaultInternalCommand.senseLine(line);
					// Processing
					process(line);
					return;
				}
				// [MARK0] $> add --help
				else if (cmds.size() > 1
						&& equalsAny(cmds.get(1), (GNU_CMD_LONG + INTERNAL_HELP), (GNU_CMD_LONG + INTERNAL_HE))) {

					// Equivalent to: '$> help add'
					line = clean(INTERNAL_HELP) + " " + cmds.get(0);
					// Set current line
					DefaultInternalCommand.senseLine(line);
					// Processing
					process(line);
					return;
				}
			}

			// Submission remote commands line
			client.writeAndFlush(new LineMessage(line));
		} else
			client.writeAndFlush(message);
	}

	@Override
	protected void postProcessResult(Object result) {
		if (result != null && isNotBlank(result.toString())) {
			out.println(result);
		}
	}

	/**
	 * Get line attributed.
	 * 
	 * @return
	 */
	protected AttributedString getAttributed() {
		String prompt = getProperty(ARG_PROMPT);
		return isBlank(prompt) ? DEFAULT_ATTRIBUTED : new AttributedString(String.format("%s> ", prompt));
	}

	/**
	 * Initialization runner
	 */
	private void initialize() {
		// Register commands
		this.registry.register(new DefaultInternalCommand(this));

		// Initialize remote register commands
		try {
			submit(new MetaMessage());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		// Set history persist file
		File file = new File(USER_HOME + "/.devops/shell/history");
		if (!file.getParentFile().exists()) {
			Assert.state(file.getParentFile().mkdirs(),
					String.format("Failed to create, for directory: '%s'", file.getParentFile().getAbsolutePath()));
		}
		if (!file.exists()) {
			String errmsg = String.format("Failed to create, for file: '%s'", file.getAbsolutePath());
			try {
				Assert.state(file.createNewFile(), errmsg);
			} catch (IOException e) {
				throw new IllegalStateException(errmsg);
			}
		}
		this.lineReader.setVariable(HISTORY_FILE, file.getAbsolutePath());

		// Print banner
		this.banner();
	}

	/**
	 * Print banner
	 */
	private void banner() {
		out.println(String.format("%s", config.getBanner()));
		String v = this.getClass().getPackage().getImplementationVersion();
		out.println(String.format("version: %s", isBlank(v) ? "unknown" : v));
		out.println(String.format("time: %s", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
	}

	/**
	 * Ensure client handler
	 * 
	 * @throws IOException
	 */
	private void ensureClient() throws IOException {
		synchronized (this) {
			boolean create = false;
			if (client == null) {
				create = true;
			} else if (!client.isActive()) {
				create = true;
				closeQuietly();
			}

			if (create) {
				Object[] point = determineServPoint();
				if (DEBUG) {
					out.print(String.format("Connecting to %s:%s ... \n", point[0], point[1]));
				}

				Socket s = new Socket((String) point[0], (int) point[1]);
				client = new ClientHandler(this, s, result -> {
					out.println(result);
					return null;
				}).starting();
			}
		}

	}

	/**
	 * Determine the corresponding server port (identified by PID) of the
	 * current client
	 * 
	 * @return
	 */
	private Object[] determineServPoint() {
		String servPids = getProperty(ARG_SERV_PIDS);
		String servPoint = getProperty(ARG_SERV_POINT);

		if (isBlank(servPids) && isBlank(servPoint)) {
			throw new IllegalArgumentException(String.format(
					"JVM startup argument -D%s(e.g. -D%s=8080) and -D%s(e.g. -D%s=19701,19702) must be one of the two, and only -D%s are adopted when both exist",
					ARG_SERV_POINT, ARG_SERV_POINT, ARG_SERV_PIDS, ARG_SERV_PIDS, ARG_SERV_POINT));
		}

		//
		// Direct use of specified point.</br>
		// Can be used to connect to remote service console.
		//
		if (isNotBlank(servPoint)) {
			Assert.isTrue(contains(servPoint, ":") && servPoint.length() > 8,
					String.format("Invalid server point. e.g. -D%s=10.0.0.11", ARG_SERV_POINT));
			String[] parts = servPoint.split(":");
			Assert.isTrue(isNumeric(parts[1]), String.format("Invalid server port is %s", servPoint));
			int port = Integer.parseInt(parts[1]);
			Assert.isTrue((port > 1024 && port < 65535),
					String.format("Server port must be between 1024 and 65535, actual is %s", servPoint));
			return new Object[] { parts[0], port };
		}

		//
		// Obtain port according to PIDS.</br>
		// Can only be used to connect to the local service console.
		//

		Assert.isTrue(IS_OS_LINUX || IS_OS_UNIX, "Not support operation system!");
		int _servPort = -1;
		StringBuffer debug = new StringBuffer("Scanning local serv listen port ...");
		try {
			ok: for (String pid : trimToEmpty(servPids).replaceAll(" ", ",").split(",")) {
				/**
				 * <pre>
				 * sl  local_address rem_address   st tx_queue rx_queue tr tm->when retrnsmt   uid  timeout inode                                                     
				 *  0: 00000000:1F68 00000000:0000 0A 00000000:00000000 00:00000000 00000000     0        0 3098188 1 ffff92b4f1ee8f80 100 0 0 10 0                   
				 *  1: 00000000:1F6A 00000000:0000 0A 00000000:00000000 00:00000000 00000000     0        0 3094334 1 ffff92b508321740 100 0 0 10 0
				 * </pre>
				 */
				String catline = String.format("cat /proc/%s/net/tcp", pid);
				if (DEBUG) {
					debug.append(String.format("\nPID: <%s> %s \n", pid, catline));
				}

				String result = LineUtils.execAsString(catline);
				if (isBlank(result)) {
					err.println(String.format("Unable to follow up on PIDS (%s) to get information about bound ports", pid));
				}

				// Find legal local port by PID
				List<String> infos = Arrays.asList(result.split("\n"));
				for (int i = 1; i < infos.size(); i++) {
					String info = infos.get(i).trim();
					String[] parts = info.split(" ");
					String localAddr = parts[1];
					String localPortHex = localAddr.split(":")[1];
					int localPort = Integer.parseInt(localPortHex, 16);
					if (DEBUG) {
						debug.append(String.format("\t%s    => :%s\n", info, localPort));
					}
					// Check whether it is within the range of port listen by
					// the server.
					if (config.getBeginPort() <= localPort && localPort <= config.getEndPort()) {
						if (DEBUG) {
							debug.append(String.format("Successful extracted for : %s\n", localPort));
						}
						_servPort = localPort;
						break ok;
					}
				}
			}

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		if (DEBUG) {
			out.println(debug.toString());
		}
		if (_servPort < 0) {
			throw new IllegalStateException(String.format(
					"Unable to connect, failed to find remote service port. target PIDS: '%s'\n More See: https://github.com/wl4g/super-devops/blob/master/super-devops-shell/super-devops-shell-cli/README_EN.md",
					servPids));
		}

		return new Object[] { config.getServer(), _servPort };
	}

	/**
	 * Quietly client close
	 */
	private void closeQuietly() {
		if (client != null) {
			client.close();
		}
	}

	/**
	 * Shell client handler
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月2日
	 * @since
	 */
	class ClientHandler extends ChannelMessageHandler {

		/**
		 * Line process runner.
		 */
		final private AbstractRunner runner;

		/**
		 * Boot boss thread
		 */
		private Thread boss;

		public ClientHandler(AbstractRunner runner, Socket client, Function<String, Object> function) {
			super(runner.getRegistry(), client, function);
			this.runner = runner;
		}

		@Override
		public ClientHandler starting() {
			this.boss = new Thread(this);
			this.boss.start();
			return this;
		}

		@Override
		public void run() {
			while (!boss.isInterrupted()) {
				try {
					// Read a string command process result
					Object input = new ObjectInputStream(_in).readObject();

					if (input instanceof ExceptionMessage) { // Exception-callback
						ExceptionMessage ex = (ExceptionMessage) input;
						runner.printErr(EMPTY, ex.getThrowable());
					}
					if (input instanceof ResultMessage) { // Result callback
						ResultMessage result = (ResultMessage) input;
						function.apply(result.getContent());
					}
					// Merge remote target methods commands
					else if (input instanceof MetaMessage) {
						MetaMessage meta = (MetaMessage) input;
						getSingle().merge(meta.getRegistedMethods());
					}

				} catch (SocketException e) {
					boss.interrupt();
					close();
				} catch (Throwable e) {
					runner.printErr(EMPTY, e);
				} finally {
					try {
						Thread.sleep(200L);
					} catch (InterruptedException e) {
					}
				}
			}
		}

	}

}
