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
package com.wl4g.devops.iam.configure;

/**
 * Default securer configure adapter
 * 
 * @author wangl.sir
 * @version v1.0 2019年3月19日
 * @since
 */
public class DefaultSecureConfigureAdapter implements SecureConfigureAdapter {

	@Override
	public SecureConfig configure() {
		return new SecureConfig(new String[] { "MD5", "SHA-256", "SHA-384", "SHA-512" }, "IAM", 5, 2 * 60 * 60 * 1000L,
				3 * 60 * 1000L);
	}

}