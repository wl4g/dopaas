/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.shell.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;

import com.wl4g.devops.shell.AbstractActuator;
import com.wl4g.devops.shell.config.ShellProperties;
import com.wl4g.devops.shell.handler.ChannelMessageHandler;
import com.wl4g.devops.shell.processor.EmbeddedServerProcessor.ShellHandler;
import com.wl4g.devops.shell.registry.ShellBeanRegistry;
import com.wl4g.devops.shell.registry.TargetMethodWrapper;

/**
 * Abstract shell component processor
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public abstract class AbstractProcessor extends AbstractActuator implements DisposableBean {

	/**
	 * Accept socket client handlers.
	 */
	final private ThreadLocal<ChannelMessageHandler> clientContext = new InheritableThreadLocal<>();

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Shell properties configuration
	 */
	final private ShellProperties config;

	/**
	 * Spring application name.
	 */
	final private String appName;

	public AbstractProcessor(ShellProperties config, String appName, ShellBeanRegistry registry) {
		super(config, registry);
		Assert.notNull(config, "config must not be null");
		Assert.hasText(appName, "appName must not be null");
		this.config = config;
		this.appName = appName;
	}

	public ShellProperties getConfig() {
		return config;
	}

	public String getAppName() {
		return appName;
	}

	/**
	 * Find parameter type index.
	 * 
	 * @param tm
	 * @param clazz
	 * @return
	 */
	protected int findParameterTypeIndex(TargetMethodWrapper tm, Class<?> clazz) {
		int index = -1, i = 0;

		for (Class<?> cls : tm.getMethod().getParameterTypes()) {
			if (cls == clazz) {
				String errmsg = String.format(
						"Find more than one parameter of the same type. make sure that the same type parameter is unique. Method: %s, parameter: %s",
						tm.getMethod(), clazz);
				Assert.state(index < 0, errmsg);
				index = i;
			}
			++i;
		}

		return index;
	}

	/**
	 * Register current client handler.
	 * 
	 * @param client
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T extends ShellHandler> T bind(ChannelMessageHandler client) {
		clientContext.set(client);
		return (T) client;
	}

	/**
	 * Get current client handler
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T extends ShellHandler> T getClient() {
		return (T) clientContext.get();
	}

	/**
	 * Cleanup current client handler.
	 * 
	 * @param client
	 * @return
	 */
	protected void cleanup() {
		clientContext.remove();
	}

}
