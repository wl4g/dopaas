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
package com.wl4g.devops.components.shell.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.impl.completer.StringsCompleter;

import static com.wl4g.devops.components.shell.annotation.ShellOption.*;
import static com.wl4g.devops.components.tools.common.lang.Assert2.*;

import com.wl4g.devops.components.shell.cli.HelpOptions;
import com.wl4g.devops.components.shell.utils.LineUtils;

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
	final private DefaultShellHandlerRegistrar registry;

	public DynamicCompleter(DefaultShellHandlerRegistrar registry) {
		notNull(registry, "registry is null, please check configure");
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