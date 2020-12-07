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
package com.wl4g.devops.dts.kit.license;

import static java.util.Objects.nonNull;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * Lisences content registry factory.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-19
 * @since
 */
public class LisenceRegistryFactory {

	/**
	 * Copyright contents.
	 */
	final private static Map<String, String> COPYRIGHTS = new TreeMap<String, String>((o1, o2) -> {
		return o1.compareTo(o2);
	});

	/**
	 * LISENCE contents.
	 */
	final private static Map<String, String> LISENCES = new TreeMap<String, String>((o1, o2) -> {
		return o1.compareTo(o2);
	});

	static {
		try {
			registerLisences(COPYRIGHTS, "copyrights/");
			registerLisences(LISENCES, "lisences/");
		} catch (Exception e) {
			throw new IllegalStateException("Failed to register lisences information.", e);
		}
	}

	/**
	 * Register LISENCES content information.
	 * 
	 * @param registry
	 * @param rootPath
	 * @throws Exception
	 */
	private static void registerLisences(Map<String, String> registry, String rootPath) throws Exception {
		URL rootUrl = LisenceRegistryFactory.class.getClassLoader().getResource(rootPath);
		if (nonNull(rootUrl)) {
			String[] files = new File(rootUrl.toURI()).list();
			if (nonNull(files)) {
				for (String fname : files) {
					registry.put(fname, Resources.toString(new URL(rootUrl.toString() + "/" + fname), Charsets.UTF_8));
				}
			}
		}
	}

	public static Map<String, String> getCopyrights() {
		return COPYRIGHTS;
	}

	public static Map<String, String> getLisences() {
		return LISENCES;
	}

}