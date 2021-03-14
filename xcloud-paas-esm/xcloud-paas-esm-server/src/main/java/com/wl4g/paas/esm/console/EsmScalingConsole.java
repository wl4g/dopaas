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
package com.wl4g.paas.esm.console;

import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.paas.esm.console.args.ScalingArgument;
import com.wl4g.paas.esm.service.EsmScalingService;
import com.wl4g.paas.shell.annotation.ShellComponent;
import com.wl4g.paas.shell.annotation.ShellMethod;

/**
 * ESM console controller.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月2日
 * @since
 */
@ShellComponent
public class EsmScalingConsole {

	final public static String GROUP = "Esm operation commands.";

	@Autowired
	protected EsmScalingService scalingService;

	@ShellMethod(keys = "scaler", group = GROUP, help = "Manual execution of scaling scheduling control.")
	public String deploy(ScalingArgument arg) {
		// TODO
		//

		return null;
	}

}