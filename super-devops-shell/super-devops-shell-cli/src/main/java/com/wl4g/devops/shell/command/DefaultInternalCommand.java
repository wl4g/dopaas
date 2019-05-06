package com.wl4g.devops.shell.command;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.cli.Options;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp.Capability;

import static org.apache.commons.lang3.StringUtils.*;

import com.wl4g.devops.shell.annotation.ShellMethod;
import com.wl4g.devops.shell.cli.HelpOptions;
import com.wl4g.devops.shell.cli.InternalCommand;
import com.wl4g.devops.shell.config.DefaultBeanRegistry;
import com.wl4g.devops.shell.runner.AbstractRunner;
import com.wl4g.devops.shell.utils.Assert;
import com.wl4g.devops.shell.utils.LineUtils;
import static com.wl4g.devops.shell.utils.ResultFormatter.getUsageFormat;

public class DefaultInternalCommand extends InternalCommand {

	/**
	 * Current read line strings.
	 */
	final private static ThreadLocal<String> lineCache = new InheritableThreadLocal<>();

	/**
	 * Shell handler bean registry
	 */
	final protected DefaultBeanRegistry registry;

	/**
	 * Abstract runner
	 */
	final protected AbstractRunner runner;

	public DefaultInternalCommand(AbstractRunner runner) {
		Assert.notNull(runner, "runner is null, please check configure");
		this.runner = runner;
		this.registry = (DefaultBeanRegistry) runner.getRegistry();
	}

	@ShellMethod(keys = { INTERNAL_QUIT, INTERNAL_QU, INTERNAL_EXIT,
			INTERNAL_EX }, group = "Default internal group", help = "Exit current process")
	public void exit() {
		runner.shutdown(EMPTY);
	}

	/**
	 * See:<a href=
	 * "https://github.com/jline/jline3/issues/183">https://github.com/jline/jline3/issues/183</a>
	 */
	@ShellMethod(keys = { INTERNAL_CLEAR, INTERNAL_CLS }, group = "Default internal group", help = "Clean up console history")
	public void clear() {
		Terminal terminal = runner.getLineReader().getTerminal();
		terminal.puts(Capability.clear_screen);
		terminal.flush();
	}

	@ShellMethod(keys = { INTERNAL_HISTORY,
			INTERNAL_HIS }, group = "Default internal group", help = "View commands execution history")
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
	@ShellMethod(keys = { INTERNAL_HELP,
			INTERNAL_HE }, group = "Default internal group", help = "View supported commands help information")
	public String help() {
		try {
			StringBuffer helpString = new StringBuffer();
			// Input line string
			String line = lineCache.get();
			List<String> commands = LineUtils.parse(line);
			if (!commands.isEmpty()) {
				if (commands.size() > 1) {
					String subArgname = commands.get(1);
					Options options = registry.getHelpOptions().get(subArgname);
					helpString.append(getUsageFormat(subArgname, options));
				} else {
					// Help options
					Map<String, HelpGroupWrapper> helpGroup = new HashMap<>();

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
							wrap.getHelpMethods().add(new HelpMethod(argname, hopts));
						}
						helpGroup.put(sm.group(), wrap);
					}

					// Group options print
					helpGroup.forEach((group, wrap) -> {
						helpString.append("----- " + group + ":\n\n");
						wrap.getHelpMethods().forEach(hm -> {
							helpString.append(getUsageFormat(hm.getArgname(), hm.getOptions()));
							if (!hm.getOptions().getOptions().isEmpty()) {
								helpString.append("\n");
							}
						});
					});
				}
			}
			return helpString.toString();
		} finally {
			lineCache.remove();
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

		public HelpMethod(String argname, Options options) {
			Assert.hasText(argname, "Argname must not be empty");
			Assert.notNull(options, "options must not be null");
			this.argname = argname;
			this.options = options;
		}

		public String getArgname() {
			return argname;
		}

		public Options getOptions() {
			return options;
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
