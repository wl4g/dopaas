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
package com.wl4g.devops.coss.aws.config;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

import com.amazonaws.regions.Regions;

@Validated
public class S3CossProperties {

	@NotBlank
	private String accessKeyId;

	@NotBlank
	private String accessKeySecret;

	@NotBlank
	private String regionName = Regions.DEFAULT_REGION.getName();

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

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

}