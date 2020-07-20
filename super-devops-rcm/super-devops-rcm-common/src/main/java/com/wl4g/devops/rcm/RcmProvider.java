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
package com.wl4g.devops.rcm;

import static com.wl4g.devops.tool.common.lang.Assert2.notNull;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * RCM provider type definitions.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月5日
 * @since
 * @throws RcmException
 * @throws ServerRcmException
 */
public enum RcmProvider {

	/**
	 * RCM provider for aliyun saf.
	 */
	AliyunSafEngine("aliyunSafEngine"),

	/**
	 * RCM provider for native tensorflow+groovy engine.
	 */
	NativeEngine("nativeEngine");

	final private String value;

	private RcmProvider(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	/**
	 * Safe converter string to {@link RcmProvider}
	 * 
	 * @param cossProvider
	 * @return
	 */
	final public static RcmProvider safeOf(String cossProvider) {
		if (isBlank(cossProvider))
			return null;

		for (RcmProvider t : values())
			if (t.getValue().equalsIgnoreCase(cossProvider) || t.name().equalsIgnoreCase(cossProvider))
				return t;

		return null;
	}

	/**
	 * Converter string to {@link RcmProvider}
	 * 
	 * @param cossProvider
	 * @return
	 */
	final public static RcmProvider of(String cossProvider) {
		RcmProvider type = safeOf(cossProvider);
		notNull(type, format("Unsupported RCM provider for %s", cossProvider));
		return type;
	}

}