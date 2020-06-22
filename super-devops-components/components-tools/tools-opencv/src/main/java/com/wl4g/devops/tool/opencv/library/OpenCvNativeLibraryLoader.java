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
package com.wl4g.devops.tool.opencv.library;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.wl4g.devops.components.tools.common.natives.LoadNativeLibraryError;
import com.wl4g.devops.components.tools.common.natives.ClassPathNativeLibraryLoader;

/**
 * OpenCv dynamic link library loader.</br>
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-12-01
 * @see <a href=
 *      "https://docs.opencv.org/2.4/doc/tutorials/introduction/desktop_java/java_dev_intro.html">opencv-java-install</a>
 * @since
 */
public final class OpenCvNativeLibraryLoader {

	/**
	 * Opencv native librarys location patterns.
	 */
	final private static List<String> DEFAULT_OPENCV_LIBS_PATTERN = new ArrayList<String>() {
		private static final long serialVersionUID = 5852139544044059530L;
		{
			// It's also a non classpath external path
			// add("file:/home/user1/natives/**/*.so");
			add("opencv/natives/**/*.dll"); // Windows
			add("opencv/natives/**/*.so"); // Linux/AIX/FreeBSD
			add("opencv/natives/**/*.jnilib"); // Linux/Mac
			add("opencv/natives/**/*.dylib"); // Mac
		}
	};

	/**
	 * Opencv native librarys loader.
	 */
	private final static ClassPathNativeLibraryLoader loader = new ClassPathNativeLibraryLoader();

	/**
	 * Loading OpenCv native librarys.
	 * 
	 * @param libLocationPatterns
	 */
	public final static void loadLibrarys(String... libLocationPatterns) {
		if (!loader.isLoaded()) {
			try {
				List<String> patterns = new ArrayList<>(DEFAULT_OPENCV_LIBS_PATTERN);
				if (nonNull(libLocationPatterns)) {
					patterns.addAll(asList(libLocationPatterns));
				}

				// Loading native library.
				loader.loadLibrarys(patterns.toArray(new String[] {}));
			} catch (IOException e) {
				throw new IllegalStateException(e);
			} catch (LoadNativeLibraryError e) {
				throw new LoadNativeLibraryError(
						"Failed to load opencv dylib. missing native lib file to run? you can download and "
								+ "install it see: https://github.com/wl4g/super-devops-tool-opencv-native"
								+ " or https://gitee.com/wl4g/super-devops-tool-opencv-native",
						e);
			}
		}

	}

}