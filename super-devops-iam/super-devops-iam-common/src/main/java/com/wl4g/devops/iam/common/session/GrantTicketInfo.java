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
package com.wl4g.devops.iam.common.session;

import static com.wl4g.devops.tool.common.utils.serialize.JacksonUtils.toJSONString;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notEmpty;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * IAM authentication grant ticket information.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月12日
 * @since
 */
public class GrantTicketInfo implements Serializable {
	private static final long serialVersionUID = -3499216861786196071L;

	/**
	 * application => grantTicket
	 */
	private Map<String, String> applications = new HashMap<>();

	public GrantTicketInfo() {
		super();
	}

	public GrantTicketInfo(Map<String, String> applications) {
		super();
		this.setApplications(applications);
	}

	/**
	 * application => grantTicket
	 * 
	 * @return
	 */
	public Map<String, String> getApplications() {
		return applications;
	}

	public GrantTicketInfo setApplications(Map<String, String> applications) {
		notEmpty(applications, "'applications' must not be empty");
		this.applications.putAll(applications);
		return this;
	}

	public GrantTicketInfo addApplications(String grantApp, String grantTicket) {
		hasText(grantApp, "'applications' must not be empty");
		hasText(grantTicket, "'grantTicket' must not be empty");
		this.applications.put(grantApp, grantTicket);
		return this;
	}

	public boolean hasApplications() {
		return getApplications() != null && !getApplications().isEmpty();
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

}