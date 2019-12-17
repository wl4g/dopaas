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
package com.wl4g.devops.iam.common.authc.model;

import com.wl4g.devops.tool.common.lang.StringUtils2;

public final class TicketValidateModel extends BaseAssertModel {
	private static final long serialVersionUID = 1383145313778896117L;

	/**
	 * Ticket may be empty when the first access is not logged-in<br/>
	 * {@link com.wl4g.devops.iam.web.IamServerController#validate}
	 */
	private String ticket;

	public TicketValidateModel() {
		super();
	}

	public TicketValidateModel(String ticket, String application) {
		super(application);
		this.ticket = ticket;
	}

	public final String getTicket() {
		return ticket;
	}

	public final void setTicket(String ticket) {
		if (!StringUtils2.isEmpty(ticket) && !"NULL".equalsIgnoreCase(ticket)) {
			this.ticket = ticket;
		}
	}

}