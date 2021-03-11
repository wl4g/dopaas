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
package com.wl4g.devops.erm.handler.standard;

/**
 * DNS zone resolving stardand definitions.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @date 2020-07-02 15:19:00
 * @see
 */
public enum DNSResolveType {

	A, AAAA, CNAME, TXT, NS, MX, SRV, SOA;

	/**
	 * Converter string to {@link DNSResolveType}
	 *
	 * @param resolveType
	 * @return
	 */
	public static DNSResolveType of(String resolveType) {
		DNSResolveType wh = safeOf(resolveType);
		if (wh == null) {
			throw new IllegalArgumentException(String.format("Illegal resolveType '%s'", resolveType));
		}
		return wh;
	}

	/**
	 * Safe converter string to {@link Action}
	 *
	 * @param resolveType
	 * @return
	 */
	public static DNSResolveType safeOf(String resolveType) {
		for (DNSResolveType t : values()) {
			if (String.valueOf(resolveType).equalsIgnoreCase(t.name())) {
				return t;
			}
		}
		return null;
	}

}