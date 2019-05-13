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
package com.wl4g.devops.shell.command;

import static java.lang.System.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.cli.Options;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;

import static org.apache.commons.lang3.SystemUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

import com.wl4g.devops.shell.annotation.ShellMethod;
import com.wl4g.devops.shell.cli.HelpOptions;
import com.wl4g.devops.shell.cli.InternalCommand;
import com.wl4g.devops.shell.config.DefaultBeanRegistry;
import com.wl4g.devops.shell.runner.AbstractRunner;
import com.wl4g.devops.shell.utils.Assert;
import com.wl4g.devops.shell.utils.LineUtils;
import static com.wl4g.devops.shell.utils.StandardFormatter.getHelpFormat;

/**
 * Default internal command.
 * 
 * @author wangl.sir
 * @version v1.0 2019年5月8日
 * @since
 */
public class DefaultInternalCommand extends InternalCommand {

	/**
	 * Default internal commands.
	 */
	final public static String DEFAULT_GROUP = "Default Internal Commands";

	/**
	 * Current read line strings.
	 */
	final private static ThreadLocal<String> lineCache = new InheritableThreadLocal<>();

	/**
	 * Shell handler bean registry
	 */
	final protected DefaultBeanRegistry registry;

	/**
	 * Line process runner.
	 */
	final protected AbstractRunner runner;

	public DefaultInternalCommand(AbstractRunner runner) {
		Assert.notNull(runner, "runner is null, please check configure");
		this.runner = runner;
		this.registry = (DefaultBeanRegistry) runner.getRegistry();
		Assert.notNull(registry, "Registry must not be null");
	}

	@ShellMethod(keys = { INTERNAL_STACKTRACE, INTERNAL_ST }, group = DEFAULT_GROUP, help = "Exit current process")
	public void stacktrace() {
		err.println(runner.getLastStacktrace());
	}

	@ShellMethod(keys = { INTERNAL_QUIT, INTERNAL_QU, INTERNAL_EXIT,
			INTERNAL_EX }, group = DEFAULT_GROUP, help = "Exit current process")
	public void exit() {
		runner.shutdown(EMPTY);
	}

	/**
	 * See:<a href=
	 * "https://github.com/jline/jline3/issues/183">https://github.com/jline/jline3/issues/183</a>
	 */
	@ShellMethod(keys = { INTERNAL_CLEAR, INTERNAL_CLS }, group = DEFAULT_GROUP, help = "Clean up console history")
	public void clear() {
		Terminal terminal = runner.getLineReader().getTerminal();
		terminal.puts(Capability.clear_screen);
		terminal.flush();
	}

	@ShellMethod(keys = { INTERNAL_HISTORY, INTERNAL_HIS }, group = DEFAULT_GROUP, help = "View commands execution history")
	public String history() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

		StringBuffer history = new StringBuffer();
		runner.getLineReader().getHistory().forEach(h -> {
			history.append(formatter.format(h.time()) + " - " + h.line());
			history.append("\n");
		});

		return history.toString();
	}

	/**
	 * See:[{@link AbstractRunner#submit}.MARK0]
	 * 
	 * @return
	 */
	@ShellMethod(keys = { INTERNAL_HELP, INTERNAL_HE }, group = DEFAULT_GROUP, help = "View supported commands help information")
	public String help() {
		try {
			StringBuffer helpString = new StringBuffer();
			// Input line string
			String line = lineCache.get();
			List<String> commands = LineUtils.parse(line);
			if (!commands.isEmpty()) {
				// Processing for e.g. help add
				if (commands.size() > 1) {
					String argname = commands.get(1);
					Options options = registry.getHelpOptions().get(argname);
					helpString.append(getHelpFormat(argname, options));
				}
				// Processing for e.g. add --help
				else {
					// Help options(group name dict sort)
					Map<String, HelpGroupWrapper> helpGroup = new TreeMap<>(new Comparator<String>() {
						@Override
						public int compare(String o1, String o2) {
							return o1.compareTo(o2);
						}
					});

					// Transform to group options
					for (Entry<String, HelpOptions> ent : registry.getHelpOptions().entrySet()) { // [MARK0]
						String argname = ent.getKey();
						HelpOptions hopts = ent.getValue();
						ShellMethod sm = hopts.getShellMethod();

						HelpGroupWrapper wrap = helpGroup.get(sm.group());
						if (wrap == null) {
							wrap = new HelpGroupWrapper(sm.group());
						}
						if (!wrap.getHelpMethods().contains(argname)) {
							wrap.getHelpMethods().add(new HelpMethod(argname, hopts, sm.help()));
						}
						helpGroup.put(sm.group(), wrap);
					}

					// Move default commands to first place
					HelpGroupWrapper defaultWrap = helpGroup.remove(DEFAULT_GROUP);
					appendHelp(DEFAULT_GROUP, defaultWrap, helpString);

					// Group options print
					helpGroup.forEach((group, wrap) -> appendHelp(group, wrap, helpString));
				}

			}

			return helpString.toString();
		} finally {
			lineCache.remove();
		}
	}

	/**
	 * Append help as strings.
	 * 
	 * @param group
	 * @param wrap
	 * @param helpString
	 */
	private void appendHelp(String group, HelpGroupWrapper wrap, StringBuffer helpString) {
		helpString.append("\n----- " + group + " -----\n\n");
		for (HelpMethod hm : wrap.getHelpMethods()) {
			helpString.append(getHelpFormat(hm.getArgname(), hm.getOptions(), hm.getHelp()));
			// Optimize: Printing default commands does not require line feeds.
			if (equalsIgnoreCase(group, DEFAULT_GROUP)) {
				helpString.delete(helpString.length() - LINE_SEPARATOR.length(), helpString.length());
			}
			if (!hm.getOptions().getOptions().isEmpty()) {
				helpString.append("\n");
			}
		}
	}

	/**
	 * Set current line
	 * 
	 * @param line
	 */
	public static void senseLine(String line) {
		lineCache.set(line);
	}

	/**
	 * Help group wrapper
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月4日
	 * @since
	 */
	class HelpGroupWrapper {

		final private String group;

		final private LinkedList<HelpMethod> helpMethods = new LinkedList<>();

		public HelpGroupWrapper(String group) {
			Assert.hasText(group, "Group must not be empty");
			this.group = group;
		}

		public String getGroup() {
			return group;
		}

		public LinkedList<HelpMethod> getHelpMethods() {
			return helpMethods;
		}

	}

	/**
	 * Help method
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月4日
	 * @since
	 */
	class HelpMethod {

		final private String argname;

		final private Options options;

		final private String help;

		public HelpMethod(String argname, Options options, String help) {
			Assert.hasText(argname, "Argname must not be empty");
			Assert.notNull(options, "options must not be null");
			Assert.hasText(argname, "Arg help must not be empty");
			this.argname = argname;
			this.options = options;
			this.help = help;
		}

		public String getArgname() {
			return argname;
		}

		public Options getOptions() {
			return options;
		}

		public String getHelp() {
			return help;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((argname == null) ? 0 : argname.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			HelpMethod other = (HelpMethod) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (argname == null) {
				if (other.argname != null)
					return false;
			} else if (!argname.equals(other.argname))
				return false;
			return true;
		}

		private DefaultInternalCommand getOuterType() {
			return DefaultInternalCommand.this;
		}

	}

}
