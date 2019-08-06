/*
 * Copyright 2017 ~ 2025 the original author or authors.
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

import java.util.Collection;
import java.util.Map;

import org.apache.commons.cli.Option;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * Help option.</br>
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月12日
 * @since
 */
public class HelpOption extends Option {
	private static final long serialVersionUID = 1950613325131445963L;

	/**
	 * Shell option default value.
	 */
	final private String defaultValue;

	public HelpOption(String opt, String longOpt, boolean hasArg, String description) throws IllegalArgumentException {
		super(opt, longOpt, hasArg, description);
		this.defaultValue = EMPTY;
	}

	public HelpOption(Class<?> paramType, String opt, String longOpt, String defaultValue, boolean required, String description)
			throws IllegalArgumentException {
		super(opt, longOpt, true, null);
		this.defaultValue = defaultValue;
		setRequired(required);
		if (!isRequired()) {
			setArgName("default=" + defaultValue);
		} else {
			setArgName("required");
		}

		// [MARK0]: Special example hints are required for list/set/array and
		// map types. See:com.wl4g.devops.shell.utils.Types#simpleSetConvert
		if (Collection.class.isAssignableFrom(paramType) || paramType.isArray()) {
			setDescription(String.format("%s\t(e.g. arg1 --list x1,x2... )", description));
		} else if (Map.class.isAssignableFrom(paramType)) {
			setDescription(String.format("%s\t(e.g. arg1 --map x1=y1,x2=y2... )", description));
		} else {
			setDescription(description);
		}
	}

	public String getDefaultValue() {
		return defaultValue;
	}

}