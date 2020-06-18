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
package com.wl4g.devops.components.tools.common.cli;

import static com.wl4g.devops.components.tools.common.lang.Assert2.isTrue;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNull;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.components.tools.common.reflect.ReflectionUtils2.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;

import com.wl4g.devops.components.tools.common.lang.Assert2;

/**
 * Command utility.
 * 
 * @author wanglsir@gmail.com, 983708408@qq.com
 * @version 2019年12月29日 v1.0.0
 * @see
 */
@SuppressWarnings("deprecation")
public class CommandUtils {

	/**
	 * New create builder. {@link Builder}
	 * 
	 * @return
	 */
	public final static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Command line builder tool.
	 * 
	 * @author Wangl.sir
	 * @version v1.0.0 2019-09-08
	 * @since
	 */
	public final static class Builder {
		final protected Logger log = getLogger(getClass());

		private RemovableOptions options;

		public Builder() {
			this.options = new RemovableOptions();
		}

		/**
		 * Adds option to options.
		 * 
		 * @param opt
		 *            Short option.
		 * @param longOpt
		 *            Long option.
		 * @param defaultValue
		 *            Null means there is no default value, that is, the
		 *            parameter is required
		 * @param description
		 * @return Argument description
		 */
		public Builder option(String opt, String longOpt, String defaultValue, String description) {
			notNull(options, "Options did not initialize creation");
			boolean required = isNull(defaultValue);
			HelpOption option = new HelpOption(opt, longOpt, defaultValue, required, description);
			option.setRequired(required);
			options.addOption(option);
			return this;
		}

		/**
		 * Remove option to options.
		 * 
		 * @param opt
		 * @param longOpt
		 * @param required
		 * @param description
		 * @return
		 */
		public Builder removeOption(String opt, String longOpt, boolean required, String description) {
			Assert2.notNull(options, "Options did not initialize creation");
			Option option = new Option(opt, longOpt, true, description);
			options.removeOption(option);
			return this;
		}

		/**
		 * Build parsing required options.
		 * 
		 * @param args
		 * @return
		 * @throws ParseException
		 */
		public CommandLine build(String args[]) throws ParseException {
			CommandLine line = null;
			try {
				line = new BasicParser().parse(options, args);
				if (log.isInfoEnabled()) {
					// Print input argument list.
					List<String> printArgs = asList(line.getOptions()).stream()
							.map(o -> o.getOpt() + "|" + o.getLongOpt() + "=" + o.getValue()).collect(toList());
					log.info("Parsed commond line args: {}", printArgs);
				}
			} catch (Exception e) {
				new HelpFormatter().printHelp(120, "\n", "", options, "");
				throw new ParseException(e.getMessage());
			}
			return line;
		}

	}

	/**
	 * Help option.</br>
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月12日
	 * @since
	 */
	public static class HelpOption extends Option {
		private static final long serialVersionUID = 1950613325131445963L;

		/**
		 * Shell option default value.
		 */
		final private String defaultValue;

		public HelpOption(String opt, String longOpt, String defaultValue, boolean required, String description)
				throws IllegalArgumentException {
			super(opt, longOpt, true, null);
			isTrue(opt.length() == 1,
					format("Short option: '%s' (%s), non GNU specification, name length must be 1", opt, description));
			this.defaultValue = defaultValue;
			setRequired(required);
			if (!isRequired()) {
				setArgName("default=" + defaultValue);
			} else {
				setArgName("required");
			}
		}

		public String getDefaultValue() {
			return defaultValue;
		}

	}

	/**
	 * {@link RemovableOptions}
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年5月17日 v1.0.0
	 * @see
	 */
	public static class RemovableOptions extends Options {
		private static final long serialVersionUID = -3292319664089354481L;

		/**
		 * Remove an option instance
		 *
		 * @param opt
		 *            the option that is to be added
		 * @return the resulting Options instance
		 */
		public RemovableOptions removeOption(Option opt) {
			if (!isNull(opt)) {
				getShortOpts().remove(opt.getOpt());
				getLongOpts().remove(opt.getLongOpt());
				getRequiredOpts().remove(opt.getOpt());
			}
			return this;
		}

		@SuppressWarnings("unchecked")
		final private Map<String, Option> getShortOpts() {
			Field field = findField(Options.class, "shortOpts");
			return (Map<String, Option>) getField(field, this);
		}

		@SuppressWarnings("unchecked")
		final private Map<String, Option> getLongOpts() {
			Field field = findField(Options.class, "longOpts");
			return (Map<String, Option>) getField(field, this);
		}

		@SuppressWarnings("unchecked")
		final private Map<String, Option> getRequiredOpts() {
			Field field = findField(Options.class, "requiredOpts");
			return (Map<String, Option>) getField(field, this);
		}

	}

}