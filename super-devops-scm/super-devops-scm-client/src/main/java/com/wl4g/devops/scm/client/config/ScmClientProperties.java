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

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SCM client properties.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月3日
 * @since
 */
@ConfigurationProperties(ScmClientProperties.PREFIX)
public class ScmClientProperties {

	/**
	 * Prefix for SCM configuration properties.
	 */
	final public static String PREFIX = "spring.cloud.devops.scm.client";
	final public static String AUTHORIZATION = "authorization";

	final public static String EXP_MAXATTEMPTS = "${spring.cloud.devops.scm.client.retry.max-attempts:5}";
	final public static String EXP_DELAY = "${spring.cloud.devops.scm.client.retry.delay:1000}";
	final public static String EXP_MAXDELAY = "${spring.cloud.devops.scm.client.retry.max-delay:5000}";
	final public static String EXP_MULT = "${spring.cloud.devops.scm.client.retry.multiplier:1.1}";
	final public static String EXP_FASTFAIL = "${spring.cloud.devops.scm.client.retry.threshold-fastfail:true}";

	/** SCM server based URI. */
	private String baseUri = "http://localhost:14043/scm";

	/**
	 * timeout on waiting to read data from the SCM Server.
	 */
	private int requestReadTimeout = 30 * 1000 + 5000;

	/**
	 * Additional headers used to create the client request.
	 */
	private Map<String, String> headers = new HashMap<>();

	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public int getRequestReadTimeout() {
		return requestReadTimeout;
	}

	public void setRequestReadTimeout(int requestReadTimeout) {
		this.requestReadTimeout = requestReadTimeout;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

}