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
package com.wl4g.devops.iam.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Social networking services configuration
 *
 * @author wangl.sir
 * @version v1.0 2019年1月8日
 * @since
 */
@ConfigurationProperties(prefix = "spring.cloud.devops.iam.crypto")
public class CryptoProperties {

	private int keyPairPools = 64;
	private long keyPairExpireMs = 600_000;

	public int getKeyPairPools() {
		return keyPairPools;
	}

	public void setKeyPairPools(int keyPairPools) {
		this.keyPairPools = keyPairPools;
	}

	public long getKeyPairExpireMs() {
		return keyPairExpireMs;
	}

	public void setKeyPairExpireMs(long keyPairExpireMs) {
		this.keyPairExpireMs = keyPairExpireMs;
	}

}