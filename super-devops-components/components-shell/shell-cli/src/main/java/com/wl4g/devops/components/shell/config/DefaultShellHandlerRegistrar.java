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

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static java.lang.String.format;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wl4g.devops.components.shell.cli.HelpOptions;
import com.wl4g.devops.components.shell.registry.ShellHandlerRegistrar;
import com.wl4g.devops.components.shell.registry.TargetMethodWrapper;

/**
 * Default bean registry
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月3日
 * @since
 */
public class DefaultShellHandlerRegistrar extends ShellHandlerRegistrar {
	final private static long serialVersionUID = -6852880158146389409L;

	private static class Holder {
		final private static DefaultShellHandlerRegistrar INSTANCE = new DefaultShellHandlerRegistrar();
	}

	/**
	 * Local and remote registed shell target methods for help.
	 */
	final private Map<String, HelpOptions> helpOptions = new ConcurrentHashMap<>(16);

	public final static DefaultShellHandlerRegistrar getSingle() {
		return Holder.INSTANCE;
	}

	/**
	 * Merge remote and local targetMethodWrapper
	 * 
	 * @param registed
	 * @return
	 */
	public DefaultShellHandlerRegistrar merge(Map<String, TargetMethodWrapper> registed) {
		state(helpOptions.isEmpty(), "Remote server registed target methods is null");

		// Registion from local.
		getTargetMethods().forEach((argname, tm) -> {
			state(helpOptions.putIfAbsent(argname, tm.getOptions()) == null,
					format("Already local registed commands: '%s'", argname));
		});

		// Registion from remote registed.
		registed.forEach((argname, tm) -> {
			state(helpOptions.putIfAbsent(argname, tm.getOptions()) == null,
					format("Already remote registed commands: '%s', It is recommended to replace the shell definition @ShellMethod(name=xx)",
							argname));
		});

		return this;
	}

	public Map<String, HelpOptions> getHelpOptions() {
		return helpOptions;
	}

}