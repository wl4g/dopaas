/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.udc.codegen.console;

import com.wl4g.component.common.io.FileDeletionUtils;
import com.wl4g.devops.udc.codegen.config.CodegenProperties;
import com.wl4g.devops.udc.codegen.engine.template.GenTemplateLocator;
import com.wl4g.shell.common.annotation.ShellMethod;
import com.wl4g.shell.core.handler.SimpleShellContext;
import com.wl4g.shell.springboot.annotation.ShellComponent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * {@link CodegenConsole}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-19
 * @sine v1.0.0
 * @see
 */
@ShellComponent
public class CodegenConsole {

	/**
	 * {@link CodegenProperties}
	 */
	@Autowired
	protected CodegenProperties config;

	/**
	 * {@link GenTemplateLocator}
	 */
	@Autowired
	protected GenTemplateLocator locator;

	@ShellMethod(keys = { "refreshTplCache" }, group = DEF_CONSOLE_GROUP, help = "Refresh generators templates cache.")
	public void refreshTemplateCache(SimpleShellContext context) {
		context.printf("Cleaning generate templates cache...");
		locator.cleanAll();

		context.printf("Cleaned generate templates cache.");
		context.completed();
	}

	@ShellMethod(keys = { "cleanGeneratedJobs" }, group = DEF_CONSOLE_GROUP, help = "Clean Generated Job temp Files.")
	public void cleanGeneratedJobs(SimpleShellContext context) {
		context.printf("Cleaning generate templates cache...");
		FileDeletionUtils.delete(config.getWorkspace().concat("/**"));
		context.printf("Cleaned generate templates cache.");
	}

	/** Codegen shell console group default name. */
	public static final String DEF_CONSOLE_GROUP = "Codegen command-line console";

}