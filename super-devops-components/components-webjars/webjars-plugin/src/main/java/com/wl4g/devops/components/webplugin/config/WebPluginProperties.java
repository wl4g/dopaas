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
package com.wl4g.devops.components.webplugin.config;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;

import com.wl4g.devops.common.config.DefaultEmbeddedWebappsAutoConfiguration.GenericEmbeddedWebappsProperties;

/**
 * Web plugin properties
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月31日
 * @since
 */
public class WebPluginProperties extends GenericEmbeddedWebappsProperties {

	final public static String KEY_PLUGIN_PREFIX = "spring.cloud.devops.components.webjars.plugin";
	final public static String URI_PLUGIN_BASE = "/plugin";
	final public static String PATH_PLUGIN_WEBAPPS = "classpath*:/plugin-webapps";

	/**
	 * Web plugin modules definitions.
	 */
	private List<String> plugins = new ArrayList<String>() { 
		private static final long serialVersionUID = -5568542141702550250L;
		{
			add("example-plugin.json");
		}
	};

	public WebPluginProperties() {
		setBaseUri(URI_PLUGIN_BASE);
		setWebappLocation(PATH_PLUGIN_WEBAPPS);
	}

	public List<String> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<String> plugins) {
		if (!isEmpty(plugins)) {
			this.plugins.addAll(plugins);
		}
	}

}