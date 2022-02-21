/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.scm.client.config;

import static com.wl4g.component.common.lang.Assert2.notNull;
import static com.wl4g.component.common.lang.StringUtils2.eqIgnCase;

/**
 * {@link CommType}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-19
 * @since
 */
public enum CommType {

	/**
	 * Use HTTP long-polling comm protocol
	 */
	HLP,

	/**
	 * Use TCP comm protocol
	 */
	RPC;

	/**
	 * Convertion of type name.
	 * 
	 * @param type
	 * @return
	 */
	public static CommType safeOf(String type) {
		for (CommType t : values()) {
			if (eqIgnCase(type, t.name())) {
				return t;
			}
		}
		return null;
	}

	/**
	 * Safe convertion of type name.
	 * 
	 * @param type
	 * @return
	 */
	public static CommType of(String type) {
		CommType t = safeOf(type);
		notNull(t, "Invalid internal comm type: '%s'", type);
		return t;
	}

}