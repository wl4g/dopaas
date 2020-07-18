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

import static java.util.Collections.emptyMap;

import java.util.Map;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * {@link JNAClassPathNativeLibraryLoader}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月22日
 * @since
 */
public class JNAClassPathNativeLibraryLoader extends ClassPathNativeLibraryLoader {

	/**
	 * Load gets JNA interfaceClass instance.
	 * 
	 * @param interfaceClass
	 * @return
	 */
	public <T extends Library> T loadInstance(Class<T> interfaceClass) {
		return loadInstance(interfaceClass, emptyMap());
	}

	/**
	 * Load gets JNA interfaceClass instance.
	 * 
	 * @param interfaceClass
	 * @param options
	 * @return
	 */
	public <T extends Library> T loadInstance(Class<T> interfaceClass, Map<String, ?> options) {
		return Native.load(interfaceClass, options);
	}

}