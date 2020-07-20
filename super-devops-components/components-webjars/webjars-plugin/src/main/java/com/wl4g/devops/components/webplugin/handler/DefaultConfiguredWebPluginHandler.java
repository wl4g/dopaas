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
package com.wl4g.devops.components.webplugin.handler;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNull;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.parseJSON;
import static java.util.Locale.US;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import com.wl4g.devops.common.exception.compoents.ParsePluginDependenciesException;
import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.components.webplugin.config.WebPluginProperties;
import com.wl4g.devops.components.webplugin.model.PluginInfo;

/**
 * {@link DefaultConfiguredWebPluginHandler}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年4月22日
 * @since
 */
public class DefaultConfiguredWebPluginHandler implements WebPluginHandler, InitializingBean {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Plugin dependencies caching
	 */
	final private Map<String, PluginInfo> pluginCaching = new ConcurrentHashMap<>(8);

	@Autowired
	protected WebPluginProperties config;

	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, PluginInfo> plugins = config.getPlugins().stream().map(p -> parsePluginDependencies(p))
				.collect(toMap(p -> p.getPluginName().toUpperCase(US), p -> p));
		pluginCaching.putAll(plugins);

		log.info("Loaded web-plugins : {}", config.getPlugins());
	}

	@Override
	public PluginInfo getPlugin(String pluginName) {
		PluginInfo plugin = pluginCaching.get(pluginName.toUpperCase(US));
		notNull(plugin, "Not found plugin '%s'", pluginName);
		return plugin;
	}

	/**
	 * Parsing {@link PluginInfo} from plugin resource.
	 * 
	 * @param path
	 * @return
	 */
	protected PluginInfo parsePluginDependencies(String plugin) throws ParsePluginDependenciesException {
		try {
			return parseJSON(new ClassPathResource(plugin).getInputStream(), PluginInfo.class);
		} catch (IOException e) {
			throw new ParsePluginDependenciesException(e);
		}
	}

}