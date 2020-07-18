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
package com.wl4g.devops.coss.common.utils;

import java.io.InputStream;
import java.util.Properties;

import static com.wl4g.devops.coss.common.utils.LogUtils.logException;

/**
 * {@link VersionInfoUtils}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年6月29日 v1.0.0
 * @see
 */
public class VersionInfoUtils {

	private static final String VERSION_INFO_FILE = "version-info.properties";
	private static final String USER_AGENT_PREFIX = "wl4g-coss-sdk-java";

	private static String version = null;

	private static String defaultUserAgent = null;

	public static String getVersion() {
		if (version == null) {
			initializeVersion();
		}
		return version;
	}

	public static String getDefaultUserAgent() {
		if (defaultUserAgent == null) {
			defaultUserAgent = USER_AGENT_PREFIX + "/" + getVersion() + "(" + System.getProperty("os.name") + "/"
					+ System.getProperty("os.version") + "/" + System.getProperty("os.arch") + ";"
					+ System.getProperty("java.version") + ")";
		}
		return defaultUserAgent;
	}

	private static void initializeVersion() {
		InputStream inputStream = VersionInfoUtils.class.getClassLoader().getResourceAsStream(VERSION_INFO_FILE);
		Properties versionInfoProperties = new Properties();
		try {
			if (inputStream == null) {
				throw new IllegalArgumentException(VERSION_INFO_FILE + " not found on classpath");
			}
			versionInfoProperties.load(inputStream);
			version = versionInfoProperties.getProperty("version");
		} catch (Exception e) {
			logException("Unable to load version information for the running SDK: ", e);
			version = "unknown-version";
		}
	}
}