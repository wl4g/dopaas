package com.wl4g.devops.shell.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;

import static com.wl4g.devops.shell.annotation.ShellOption.*;
import com.wl4g.devops.shell.cli.HelpOptions;
import com.wl4g.devops.shell.utils.Assert;
import com.wl4g.devops.shell.utils.LineUtils;

/**
 * Dynamic completer
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public class DynamicCompleter implements Completer {

	/**
	 * Shell bean registry.
	 */
	final private DefaultBeanRegistry registry;

	public DynamicCompleter(DefaultBeanRegistry registry) {
		Assert.notNull(registry, "registry is null, please check configure");
		this.registry = registry;
	}

	@Override
	public void complete(LineReader reader, ParsedLine parsedLine, List<Candidate> candidates) {
		List<String> commands = LineUtils.parse(parsedLine.line());

		// Primary level frist arguments
		if (commands.isEmpty()) {
			new StringsCompleter(registry.getHelpOptions().keySet()).complete(reader, parsedLine, candidates);
		}
		// Secondary primary arguments
		else {
			HelpOptions options = registry.getHelpOptions().get(commands.get(0));
			// Continue before completion
			if (completingCompleted(commands, options)) {
				List<String> candes = new ArrayList<>();
				for (Option opt : options.getOptions()) {
					candes.add(GNU_CMD_SHORT + opt.getOpt());
					candes.add(GNU_CMD_LONG + opt.getLongOpt());
				}
				new StringsCompleter(candes).complete(reader, parsedLine, candidates);
			}
		}

	}

	/**
	 * Check if candidate parameters have been completing completed</br>
	 * 
	 * e.g. $> add -a 11 -b 22
	 * 
	 * @param commands
	 * @param options
	 * @return
	 */
	private boolean completingCompleted(List<String> commands, HelpOptions options) {
		return options != null && ((commands.size() - 1) / 2) != options.getOptions().size();
	}

}
