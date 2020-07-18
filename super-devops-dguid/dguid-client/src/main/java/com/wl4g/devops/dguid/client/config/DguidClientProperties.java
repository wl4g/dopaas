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
package com.wl4g.devops.dguid.client.config;

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * {@link DguidClientProperties}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月15日
 * @since
 */
public class DguidClientProperties implements InitializingBean {

	/** Dguid server endpoint */
	private String serverEndpoint = "http://localhost:14071/dguid-server";

	/** Dguid client application name. */
	@Value("${spring.application.name:defaultDguidClientApp}")
	private String appName;

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	public String getServerEndpoint() {
		return serverEndpoint;
	}

	public void setServerEndpoint(String serverEndpoint) {
		this.serverEndpoint = serverEndpoint;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
	}

}