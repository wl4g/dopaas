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
package com.wl4g.devops.ci.utils;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.state;
import static org.springframework.util.StringUtils.getFilename;

import java.io.File;

/**
 * Pipeline utility tools.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-24
 * @since
 */
public abstract class PipelineUtils {

	/**
	 * Get filename with-out suffix from path.
	 * 
	 * @param path
	 * @return
	 */
	public static String getUnExtensionFilename(String path) {
		String filename = getFilename(path);
		return filename.substring(0, filename.lastIndexOf("."));
	}

	/**
	 * Make sure that the directory of the path exists. If it does not exist,
	 * create it. Throw an exception when the creation fails.
	 * 
	 * @param path
	 */
	public static void ensureDirectory(String path) {
		hasText(path, "Directory path must not be emtpy.");
		File file = new File(path);
		if (!file.exists()) {
			state(file.mkdirs(), String.format("Failed to creating directory for %s", path));
		}
	}

}