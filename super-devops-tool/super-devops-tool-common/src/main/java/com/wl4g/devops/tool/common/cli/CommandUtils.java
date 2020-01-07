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

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;

import com.wl4g.devops.tool.common.lang.Assert2;

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
			Assert2.notNull(options, "Options did not initialize creation");
			Option option = new Option(opt, longOpt, true, description);
			option.setRequired(required);
			options.addOption(option);
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
					log.info("Parsed arguments: {}", printArgs);
				}
			} catch (Exception e) {
				new HelpFormatter().printHelp("\n", options);
				throw new ParseException(e.getMessage());
			}
			return line;
		}

	}

}