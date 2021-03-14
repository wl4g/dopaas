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
package com.wl4g.dopaas.udc.tools.devel.stats;

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.SystemUtils.IS_OS_UNIX;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;

import java.io.File;

import com.wl4g.dopaas.udc.tools.devel.stats.SourceCodeCounterTool;

/**
 * {@link SourceCodeCounterToolTests}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月3日
 * @since
 */
public class SourceCodeCounterToolTests {

	public static void main(String[] args) throws Exception {
		statisThatRootCodesTests();
	}

	public static void statisThatRootCodesTests() throws Exception {
		// String rootDir = USER_DIR;
		String[] parts = split(USER_DIR, File.separator);
		String rootDir = null;
		if (parts.length > 4) {
			rootDir = join(parts, File.separator, 0, parts.length - 3);
		} else {
			rootDir = join(parts, File.separator);
		}
		if (IS_OS_UNIX) {
			rootDir = "/".concat(rootDir);
		}
		String[] _args = new String[] { "-V", "true", "-r", rootDir };
		SourceCodeCounterTool.main(_args);
	}

}