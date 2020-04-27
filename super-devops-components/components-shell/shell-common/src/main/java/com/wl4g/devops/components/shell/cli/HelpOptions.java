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
package com.wl4g.devops.components.shell.cli;

import org.apache.commons.cli.Options;

import com.wl4g.devops.components.shell.annotation.ShellMethod;

/**
 * Help option.</br>
 * See:{@link com.wl4g.devops.shell.command.DefaultInternalCommand#help()}[MARK0]
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public class HelpOptions extends Options {
	private static final long serialVersionUID = 2206030510132539771L;

	final private ShellMethod shellMethod;

	public HelpOptions(ShellMethod shellMethod) throws IllegalArgumentException {
		this.shellMethod = shellMethod;
	}

	public ShellMethod getShellMethod() {
		return shellMethod;
	}

}