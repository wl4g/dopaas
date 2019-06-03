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
package com.wl4g.devops.scm.client.config;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseInstance;

/**
 * Instance config.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月1日
 * @since
 */
public class InstanceInfo implements InitializingBean, Serializable {
	private static final long serialVersionUID = -3652519510077023579L;

	final private Environment environment;

	private String appName;
	private String profilesActive;
	private ReleaseInstance bindInstance;

	public InstanceInfo(Environment environment) {
		this.environment = environment;
	}

	public String getAppName() {
		return appName;
	}

	public String getProfilesActive() {
		return profilesActive;
	}

	public ReleaseInstance getBindInstance() {
		return bindInstance;
	}

	/**
	 * Environment['server.port' 'spring.application.name'
	 * 'spring.profiles.active'] will be used for DevOps configuration to
	 * zookeeper instance registration.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// Get required information.
		this.appName = this.environment.getProperty("spring.application.name");
		this.profilesActive = this.environment.getProperty("spring.profiles.active");
		String serverPort = this.environment.getProperty("server.port");

		// Check null.
		boolean r = StringUtils.isAnyBlank(this.appName, this.profilesActive, serverPort);
		Assert.isTrue(!r,
				"Environment['server.port' 'spring.application.name' 'spring.profiles.active'] config is null, Because spring cloud loads bootstrap.yml preferentially, which means that other config files are not loaded at initialization, so configurations other than bootstrap.yml cannot be used at initialization, Therefore, these 3 items must be allocated to bootstrap.yml.");

		// Bind current instance.
		try {
			this.bindInstance = ReleaseInstance.of(InetAddress.getLocalHost().getHostName() + ":" + serverPort);
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}

	}

}