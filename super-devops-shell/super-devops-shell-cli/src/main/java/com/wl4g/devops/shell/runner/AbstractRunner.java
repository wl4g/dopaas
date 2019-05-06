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

import static java.lang.System.out;
import static java.lang.System.err;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
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
import com.wl4g.devops.shell.bean.CommandMessage;
import com.wl4g.devops.shell.bean.LineMessage;
import com.wl4g.devops.shell.bean.ResultMessage;
import com.wl4g.devops.shell.config.Configuration;
import com.wl4g.devops.shell.config.DynamicCompleter;
import com.wl4g.devops.shell.handler.ChannelMessageHandler;
import com.wl4g.devops.shell.registry.ShellBeanRegistry;
import com.wl4g.devops.shell.utils.Assert;

/**
 * Abstract shell component runner
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public abstract class AbstractRunner extends AbstractActuator implements Runner {

	/**
	 * Shell configuration
	 */
	final protected Configuration config;

	/**
	 * Attributed string
	 */
	final protected AttributedString attributed;

	/**
	 * Line reader
	 */
	final protected LineReader lineReader;

	/**
	 * Shell client handler
	 */
	private ClientHandler client;

	public AbstractRunner(Configuration config, AttributedString attributed) {
		super(getSingle());
		Assert.notNull(config, "configuration is null, please check configure");
		Assert.notNull(attributed, "attributedString is null, please check configure");
		this.config = config;
		this.attributed = attributed;

		// Build lineReader
		try {
			this.lineReader = LineReaderBuilder.builder().appName("DevOps Shell").completer(new DynamicCompleter(getSingle()))
					.terminal(TerminalBuilder.terminal()).build();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		// Initialization
		initialize();
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
			System.exit(0);

		} catch (Throwable e) {
			err.println(String.format("Shutdown failure. %s", getStackTrace(e)));
		}
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
	 * Initialization runner
	 */
	private void initialize() {
		// Register commands
		this.registry.register(new DefaultInternalCommand(this));

		// Initialize remote register commands
		try {
			submit(new CommandMessage());
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
				Socket s = new Socket(config.getServer(), config.getPort());
				client = new ClientHandler(registry, s, result -> {
					out.println(result);
					return null;
				}).starting();
			}
		}
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
		 * Boot boss thread
		 */
		private Thread boss;

		public ClientHandler(ShellBeanRegistry registry, Socket client, Function<String, Object> function) {
			super(registry, client, function);
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

					if (input instanceof ResultMessage) { // Result callback
						ResultMessage result = (ResultMessage) input;
						function.apply(result.getContent());
					}
					// Merge remote target methods commands
					else if (input instanceof CommandMessage) {
						CommandMessage cmd = (CommandMessage) input;
						getSingle().merge(cmd.getRegisted());
					}

				} catch (SocketException e) {
					close();
				} catch (Throwable e) {
					err.println(String.format("%s", getStackTrace(e)));
				}
			}
		}

	}

}
