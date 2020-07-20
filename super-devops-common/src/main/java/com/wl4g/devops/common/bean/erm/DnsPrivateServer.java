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
package com.wl4g.devops.common.bean.erm;

import com.wl4g.devops.common.bean.BaseBean;

public class DnsPrivateServer extends BaseBean {
	private static final long serialVersionUID = -3298424126317938674L;

	private String name;

	private String kind;

	private String dnsServer1;

	private String dnsServer2;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind == null ? null : kind.trim();
	}

	public String getDnsServer1() {
		return dnsServer1;
	}

	public void setDnsServer1(String dnsServer1) {
		this.dnsServer1 = dnsServer1 == null ? null : dnsServer1.trim();
	}

	public String getDnsServer2() {
		return dnsServer2;
	}

	public void setDnsServer2(String dnsServer2) {
		this.dnsServer2 = dnsServer2 == null ? null : dnsServer2.trim();
	}
}