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

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.components.tools.common.cli.ProcessUtils.buildCrossSingleCommands;
import static com.wl4g.devops.components.tools.common.cli.ProcessUtils.execMulti;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.wl4g.devops.components.tools.common.cli.ProcessUtils;

public class ProcessUtilsTests {

	public static void main(String[] args) throws Exception {
		// buildCrossSingleCommandsTest1();
		// execMultiTest2();
		// execProgressTest3();
		execInteractiveCommandTest4();
	}

	public static void buildCrossSingleCommandsTest1() throws Exception {
		String[] cmdarray = buildCrossSingleCommands("mvn -version", new File("c:\\out"), new File("c:\\err"), false, false);
		Runtime.getRuntime().exec(cmdarray).waitFor();
	}

	public static void execMultiWithWindowsTest2() throws Exception {
		if (IS_OS_WINDOWS) {
			execMulti("echo \"start...\"\njps \necho \"end\"", new File("d:\\"), new File("c:\\out"), new File("c:\\err"), true,
					false);
		}
	}

	public static void execProgressTest3() throws Exception {
		int whole = 120;
		for (int i = 0; i < whole; i++) {
			ProcessUtils.printProgress("正在分析...", i, whole, '#');
		}
	}

	public static void execInteractiveCommandTest4() throws Exception {
		// Generate sample data.
		File file = new File("/tmp/test_vim_file.txt");
		FileUtils.write(file, "abcdefghijklmnopqrstuvwxyz", UTF_8);
		// Testing
		String res = ProcessUtils.execSimpleString(new String[] { "vim", file.getAbsolutePath() }, 1_000);
		System.out.println(res);
	}

}