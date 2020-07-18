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
package com.wl4g.devops.coss.aliyun.config;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

@Validated
public class AliyunOssProperties {

	/**
	 * e.g1: oss-cn-shenzhen.aliyuncs.com </br>
	 * e.g2: oss-cn-hangzhou.aliyuncs.com </br>
	 * e.g3: oss-cn-shenzhen-internal.aliyuncs.com </br>
	 * e.g4: oss-cn-hangzhou-internal.aliyuncs.com </br>
	 */
	@NotBlank
	private String endpoint;

	@NotBlank
	private String accessKeyId;

	@NotBlank
	private String accessKeySecret;

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		notNullOf(endpoint, "aliyunOssEndpoint");
		this.endpoint = endpoint;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyId) {
		notNullOf(accessKeyId, "cossAccessKeyId");
		this.accessKeyId = accessKeyId;
	}

	public String getAccessKeySecret() {
		return accessKeySecret;
	}

	public void setAccessKeySecret(String accessKeySecret) {
		notNullOf(accessKeySecret, "cossAccessKeySecret");
		this.accessKeySecret = accessKeySecret;
	}

}