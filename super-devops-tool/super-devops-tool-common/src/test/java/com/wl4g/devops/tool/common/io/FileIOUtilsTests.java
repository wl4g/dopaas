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
package com.wl4g.devops.tool.common.io;

import static com.wl4g.devops.tool.common.io.FileIOUtils.ensureFile;
import static com.wl4g.devops.tool.common.io.FileIOUtils.readLines;
import static com.wl4g.devops.tool.common.io.FileIOUtils.seekReadLines;
import static com.wl4g.devops.tool.common.io.FileIOUtils.seekReadString;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

public class FileIOUtilsTests {

	public static void main(String[] args) {
		// seekReadTest1();
		ensureFileTest2();
	}

	public static void seekReadTest1() {
		System.out.println(SystemUtils.LINE_SEPARATOR);
		System.out.println(readLines("C:\\Users\\Administrator\\Desktop\\aaa.txt", 2, 12));
		System.out.println("--------------------");
		System.out.println(seekReadString("C:\\Users\\Administrator\\Desktop\\aaa.txt", 3L, 12));
		System.out.println("--------------------");
		System.out.println(seekReadLines("C:\\Users\\Administrator\\Desktop\\aaa.txt", 13L, 6, line -> {
			return line.equalsIgnoreCase("EOF"); // End if 'EOF'
		}));
	}

	public static void ensureFileTest2() {
		ensureFile(new File("c:\\mydir1\\a.txt"));
	}

}
