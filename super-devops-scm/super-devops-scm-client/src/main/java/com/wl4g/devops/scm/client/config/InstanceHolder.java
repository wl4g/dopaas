/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.scm.client.config;

import static org.apache.commons.lang3.StringUtils.isAnyBlank;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseInstance;

/**
 * Instance information.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月1日
 * @since
 */
public class InstanceHolder {

	final private Environment environment;
	final private String appName;
	final private ReleaseInstance instance;

	public InstanceHolder(Environment environment) {
		this.environment = environment;
		// Application name
		this.appName = this.environment.getProperty("spring.application.name");
		String servPort = this.environment.getProperty("server.port");

		boolean check = isAnyBlank(appName, servPort);
		Assert.isTrue(!check,
				"Environment['server.port','spring.application.name'] config is null, Because spring cloud loads bootstrap.yml preferentially, which means that other config files are not loaded at initialization, so configurations other than bootstrap.yml cannot be used at initialization, Therefore, these 3 items must be allocated to bootstrap.yml.");
		try {
			// Local instance.
			this.instance = ReleaseInstance.of(InetAddress.getLocalHost().getHostName() + ":" + servPort);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}

	public String getAppName() {
		return appName;
	}

	public ReleaseInstance getInstance() {
		return instance;
	}

}