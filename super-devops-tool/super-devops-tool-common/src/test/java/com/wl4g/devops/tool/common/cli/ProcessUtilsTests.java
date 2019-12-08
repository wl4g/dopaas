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

import static com.wl4g.devops.tool.common.cli.ProcessUtils.buildCrossSingleCommands;
import static com.wl4g.devops.tool.common.cli.ProcessUtils.execMulti;

import java.io.File;

public class ProcessUtilsTests {

	public static void main(String[] args) throws Exception {
		// buildCrossSingleCommandsTest1();
		execMultiTest2();
	}

	public static void buildCrossSingleCommandsTest1() throws Exception {
		String[] cmdarray = buildCrossSingleCommands("mvn -version", new File("c:\\out"), new File("c:\\err"), false, false);
		Runtime.getRuntime().exec(cmdarray).waitFor();
	}

	public static void execMultiTest2() throws Exception {
		execMulti("echo \"start...\"\njps \necho \"end\"", new File("d:\\"), new File("c:\\out"), new File("c:\\err"),
				true, false);
	}

}
