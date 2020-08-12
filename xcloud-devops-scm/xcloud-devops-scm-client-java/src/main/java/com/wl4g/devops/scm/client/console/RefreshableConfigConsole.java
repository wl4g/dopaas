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
package com.wl4g.devops.scm.client.console;

import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;

import org.slf4j.Logger;

import com.wl4g.shell.annotation.ShellComponent;
import com.wl4g.shell.annotation.ShellMethod;
import com.wl4g.shell.annotation.ShellOption;

/**
 * See:<a href=
 * "https://blog.csdn.net/cml_blog/article/details/78411312">https://blog.csdn.net/cml_blog/article/details/78411312</a>
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年10月9日
 * @since
 */
@ShellComponent
public class RefreshableConfigConsole {

	protected final Logger log = getLogger(getClass());

	/**
	 * 
	 * @param force
	 */
	@ShellMethod(keys = { "refresh" }, group = DEFAULT_SCM_CONSOLE_GROUP, help = "Execute refresh configuration immediately")
	public void refresh(@ShellOption(opt = "f", lopt = "force", help = "Force refresh configuration") boolean force) {
		log.info("Refresh configuration for ... force={}", force);

		// TODO

	}

	/** SCM console group */
	public static final String DEFAULT_SCM_CONSOLE_GROUP = "SCM configurer console";

}