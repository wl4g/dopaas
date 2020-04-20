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
package com.wl4g.devops.iam.sns.support;

/**
 * OAuth2 response_type definition
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月3日
 * @see https://www.cnblogs.com/giserliu/p/4372455.html
 * @since
 */
public enum OAuth2ResponseType {

	CODE(true), TOKEN;

	private boolean isDefault = false;

	private OAuth2ResponseType() {
	}

	private OAuth2ResponseType(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public static OAuth2ResponseType getDefault() {
		OAuth2ResponseType defaultResponseType = null;
		for (OAuth2ResponseType rt : values()) {
			if (rt.isDefault()) {
				if (defaultResponseType != null) {
					throw new IllegalStateException("There can only be one default value");
				}
				defaultResponseType = rt;
			}
		}
		return defaultResponseType;
	}

}