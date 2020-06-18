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
package com.wl4g.devops.components.shell.handler;

import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;

import org.slf4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;

import com.wl4g.devops.components.shell.config.ShellProperties;
import com.wl4g.devops.components.shell.handler.AbstractShellHandler;
import com.wl4g.devops.components.shell.handler.EmbeddedServerShellHandler.ServerShellMessageChannel;
import com.wl4g.devops.components.shell.registry.ShellHandlerRegistrar;

/**
 * Abstract shell component processor
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public abstract class AbstractServerShellHandler extends AbstractShellHandler implements DisposableBean {
	final protected Logger log = getLogger(getClass());

	/**
	 * Accept socket client handlers.
	 */
	final private ThreadLocal<ServerShellMessageChannel> clientContext = new InheritableThreadLocal<>();

	/**
	 * Shell properties configuration
	 */
	final private ShellProperties config;

	/**
	 * Spring application name.
	 */
	final private String appName;

	public AbstractServerShellHandler(ShellProperties config, String appName, ShellHandlerRegistrar registry) {
		super(config, registry);
		Assert.notNull(config, "config must not be null");
		Assert.hasText(appName, "appName must not be null");
		this.config = config;
		this.appName = appName;
	}

	protected ShellProperties getConfig() {
		return config;
	}

	protected String getAppName() {
		return appName;
	}

	/**
	 * Register current client handler.
	 * 
	 * @param client
	 * @return
	 */
	protected ServerShellMessageChannel bind(ServerShellMessageChannel client) {
		clientContext.set(client);
		return client;
	}

	/**
	 * Get current client handler
	 * 
	 * @return
	 */
	protected ServerShellMessageChannel getClient() {
		return clientContext.get();
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