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
package com.wl4g.devops.tool.common.cli;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wl4g.devops.tool.common.lang.Assert;
import static com.wl4g.devops.tool.common.lang.Assert.*;

/**
 * Command line utility.
 * 
 * @author Wangl.sir
 * @version v1.0.0 2019-09-08
 * @since
 */
@SuppressWarnings("deprecation")
public abstract class CommandLineUtils {

	/**
	 * Execution command-line.
	 * 
	 * @param commands
	 *            commands string.
	 * @param pwdDir
	 *            execute context directory.
	 * @return
	 * @throws IOException
	 */
	public final static Process doExec(final String[] commands, final File pwdDir) throws IOException {
		Process ps = null;
		if (nonNull(pwdDir)) {
			state(pwdDir.exists(), String.format("No such directory for pwdDir:[%s]", pwdDir));
			ps = Runtime.getRuntime().exec(commands, null, pwdDir);
		} else {
			ps = Runtime.getRuntime().exec(commands);
		}
		return ps;
	}

	/**
	 * Build cross platform wide fully qualified command line.
	 * 
	 * @param cmd
	 *            Execution command string.
	 * @return
	 */
	public final static String[] buildCrossCommands(final String cmd) {
		return buildCrossCommands(cmd, null, null);
	}

	/**
	 * Build cross platform wide fully qualified command line.
	 * 
	 * @param cmd
	 *            Execution command string.
	 * @param stdout
	 *            Standard output file.
	 * @param stderr
	 *            Standard error output file.
	 * @return
	 */
	public final static String[] buildCrossCommands(final String cmd, final File stdout, final File stderr) {
		hasText(cmd, "Execute command can't empty.");

		String command = cmd;
		List<String> commands = new ArrayList<>(8);
		if (IS_OS_WINDOWS) {
			commands.add("C:\\Windows\\System32\\cmd.exe");
			commands.add("/c");

			// Stdout/Stderr
			boolean output = false;
			if (nonNull(stdout)) {
				command = command + " 1> " + stdout.getAbsolutePath();
				output = true;
			}
			if (nonNull(stderr)) {
				command = command + " 2> " + stderr.getAbsolutePath();
				output = true;
			}
			if (!output) {
				command = command + " > C:\\nul"; // To use in poweshell: $null
			}
		} else {
			commands.add("/bin/bash");
			commands.add("-c");

			// Stdout/Stderr
			boolean output = false;
			if (nonNull(stdout)) {
				command = command + " 1> " + stdout.getAbsolutePath();
				output = true;
			}
			if (nonNull(stderr)) {
				command = command + " 2> " + stderr.getAbsolutePath();
				output = true;
			}
			if (!output) {
				command = command + " > /dev/null";
			}
		}
		commands.add(command);
		return commands.toArray(new String[] {});
	}

	/**
	 * Command line builder tool.
	 * 
	 * @author Wangl.sir
	 * @version v1.0.0 2019-09-08
	 * @since
	 */
	public static class Builder {
		protected Log log = LogFactory.getLog(getClass());

		private Options options;

		public Builder() {
			this.options = new Options();
		}

		/**
		 * Add option to options.
		 * 
		 * @param opt
		 * @param longOpt
		 * @param required
		 * @param description
		 * @return
		 */
		public Builder option(String opt, String longOpt, boolean required, String description) {
			Assert.notNull(options, "Options did not initialize creation");
			Option option = new Option(opt, longOpt, true, description);
			option.setRequired(required);
			options.addOption(option);
			return this;
		}

		public CommandLine build(String args[]) throws ParseException {
			CommandLine line = null;
			try {
				line = new BasicParser().parse(options, args);
				if (log.isInfoEnabled()) {
					// Print input argument list.
					List<String> printArgs = Arrays.asList(line.getOptions()).stream()
							.map(o -> o.getOpt() + "|" + o.getLongOpt() + "=" + o.getValue()).collect(toList());
					log.info(String.format("Parsed arguments: %s", printArgs));
				}
			} catch (Exception e) {
				new HelpFormatter().printHelp("\t", options);
				throw new ParseException(e.getMessage());
			}
			return line;
		}

	}

}