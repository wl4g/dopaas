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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wl4g.devops.common.bean.BaseBean;

import java.util.Date;
import java.util.List;

public class DnsPrivateZone extends BaseBean {

	private static final long serialVersionUID = -3298424126317938674L;

	private String zone;

	private Integer dnsServerId;

	private String status;

	private Date registerDate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date dueDate;

	private List<DnsPrivateResolution> dnsPrivateResolutions;

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone == null ? null : zone.trim();
	}

	public Integer getDnsServerId() {
		return dnsServerId;
	}

	public void setDnsServerId(Integer dnsServerId) {
		this.dnsServerId = dnsServerId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status == null ? null : status.trim();
	}

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public List<DnsPrivateResolution> getDnsPrivateResolutions() {
		return dnsPrivateResolutions;
	}

	public void setDnsPrivateResolutions(List<DnsPrivateResolution> dnsPrivateResolutions) {
		this.dnsPrivateResolutions = dnsPrivateResolutions;
	}
}