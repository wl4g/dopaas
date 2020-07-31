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
package com.wl4g.devops.components.tools.common.natives;

import static java.lang.System.out;
import static org.apache.commons.lang3.SystemUtils.OS_ARCH;
import static org.apache.commons.lang3.SystemUtils.OS_NAME;

import com.wl4g.devops.components.tools.common.natives.JNAClassPathNativeLibraryLoader;

/**
 * {@link JNAClassPathNativeLibraryLoaderTests}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-07-31
 * @since
 * @see https://www.cnblogs.com/gwd1154978352/p/6831927.html
 */
public class JNAClassPathNativeLibraryLoaderTests {

	public static void main(String[] args) throws Exception {
		loadSampleLibForDeleteOnExitTest1();
	}

	public static void loadSampleLibForDeleteOnExitTest1() throws Exception {
		out.println(OS_NAME);
		out.println(OS_ARCH);

		JNAClassPathNativeLibraryLoader loader = new JNAClassPathNativeLibraryLoader()
				.loadLibrarys("com/wl4g/devops/components/tools/common/natives/library/**/*.*");
		LibSample1 sample1 = loader.loadInstance(LibSample1.class);

		System.out.println("Load instance: " + sample1);
		System.out.println("Call Sum() Result: " + sample1.Sum(111, 222));
	}

}