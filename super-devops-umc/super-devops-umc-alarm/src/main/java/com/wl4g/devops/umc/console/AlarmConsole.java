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
package com.wl4g.devops.umc.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.shell.annotation.ShellComponent;
import com.wl4g.devops.shell.annotation.ShellMethod;
import com.wl4g.devops.shell.annotation.ShellOption;
import com.wl4g.devops.umc.rule.RuleConfigManager;

/**
 * Receiver configuration console.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月24日
 * @since
 */
@ShellComponent
public class AlarmConsole {
	final public static String SHELL_GROUP = "UMC alarms";

	protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private RuleConfigManager ruleManager;

	/**
	 * Cleanup all of alarm rules cache.
	 */
	@ShellMethod(keys = "rule-clear", group = SHELL_GROUP, help = "Cleanup all of alarm rules cache.")
	public void cleanRuleAll(ClearArgument arg) {
		if (log.isInfoEnabled()) {
			log.info("Cleaning all of rules cache...");
		}
		ruleManager.clearAll(arg.getClearBatch());
	}

	/**
	 * Clear argument.
	 * 
	 * @author Wangl.sir
	 * @version v1.0 2019年8月1日
	 * @since
	 */
	public static class ClearArgument {

		@ShellOption(opt = "b", lopt = "clearBatch", required = false, defaultValue = "200", help = "Batch size for cleaning alert template cache.")
		private int clearBatch;

		public int getClearBatch() {
			return clearBatch;
		}

		public void setClearBatch(int clearBatch) {
			this.clearBatch = clearBatch;
		}

	}

}