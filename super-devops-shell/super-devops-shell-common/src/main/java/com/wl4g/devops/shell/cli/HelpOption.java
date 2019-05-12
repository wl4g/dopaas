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
package com.wl4g.devops.shell.cli;

import static org.apache.commons.lang3.StringUtils.*;
import org.apache.commons.cli.Option;

/**
 * Help option.</br>
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月12日
 * @since
 */
public class HelpOption extends Option {
	private static final long serialVersionUID = 1950613325131445963L;

	public HelpOption(String opt, String longOpt, boolean hasArg, String description) throws IllegalArgumentException {
		super(opt, longOpt, hasArg, description);
	}

	public HelpOption(String opt, String longOpt, String defaultValue, String description) throws IllegalArgumentException {
		super(opt, longOpt, true, description);
		setRequired(isBlank(defaultValue));
		if (!isRequired()) {
			setArgName("default:" + defaultValue);
		} else {
			setArgName("required");
		}
	}

}
