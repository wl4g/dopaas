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
package com.wl4g.devops.common.kit.jvm;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * JVM runtime kit utility
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月12日
 * @since
 */
public class JVMRuntimeKit {

	/**
	 * Whether current JVM runtime debuging mode
	 * 
	 * @return
	 */
	public static boolean isJVMDebuging() {
		List<String> arguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
		boolean debuging = false;
		for (String str : arguments) {
			if (str.startsWith("-agentlib")) {
				debuging = true;
			}
		}
		return debuging;
	}

}