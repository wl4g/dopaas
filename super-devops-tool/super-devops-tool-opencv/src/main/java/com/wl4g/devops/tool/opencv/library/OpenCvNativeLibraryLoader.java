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
package com.wl4g.devops.tool.opencv.library;

import java.io.IOException;

import com.wl4g.devops.tool.common.natives.LoadNativeLibraryError;
import com.wl4g.devops.tool.common.natives.PathPatternNativeLibraryLoader;

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
	final private static String[] OPENCV_LIBS_PATTERN = { //
			"/com/wl4g/devops/tool/opencv/library/natives/**/*.dll", // Windows
			"/com/wl4g/devops/tool/opencv/library/natives/**/*.so", // Linux/AIX/FreeBSD
			"/com/wl4g/devops/tool/opencv/library/natives/**/*.dylib", // Mac
			"/com/wl4g/devops/tool/opencv/library/natives/**/*.jnilib" // Mac
	};

	/**
	 * Opencv native librarys loader.
	 */
	private final static PathPatternNativeLibraryLoader loader = new PathPatternNativeLibraryLoader(OPENCV_LIBS_PATTERN);

	/**
	 * Loading OpenCv native librarys.
	 */
	public final static void loadLibrarys() {
		try {
			loader.loadLibrarys();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} catch (LoadNativeLibraryError e) {
			throw new LoadNativeLibraryError(
					"Failed to load opencv dylib. Missing native library file to run? you can download and "
							+ "install it see: https://github.com/wl4g/super-devops-tool-opencv-native"
							+ " or https://gitee.com/wl4g/super-devops-tool-opencv-native",
					e);
		}
	}

}