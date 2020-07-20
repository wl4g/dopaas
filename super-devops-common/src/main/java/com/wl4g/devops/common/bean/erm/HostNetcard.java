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

public class HostNetcard extends BaseBean {

	private static final long serialVersionUID = 4324569366421220002L;

	private Integer hostId;

	private String name;

	private String status;

	private Integer vpnTunnelType;

	private Integer openvpnTunnelId;

	private Integer pptpTunnelId;

	private String ipv4;

	private String ipv6;

	private String hwaddr;

	private String netmask;

	private String broadcast;

	private String getway;

	public Integer getHostId() {
		return hostId;
	}

	public void setHostId(Integer hostId) {
		this.hostId = hostId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status == null ? null : status.trim();
	}

	public Integer getVpnTunnelType() {
		return vpnTunnelType;
	}

	public void setVpnTunnelType(Integer vpnTunnelType) {
		this.vpnTunnelType = vpnTunnelType;
	}

	public Integer getOpenvpnTunnelId() {
		return openvpnTunnelId;
	}

	public void setOpenvpnTunnelId(Integer openvpnTunnelId) {
		this.openvpnTunnelId = openvpnTunnelId;
	}

	public Integer getPptpTunnelId() {
		return pptpTunnelId;
	}

	public void setPptpTunnelId(Integer pptpTunnelId) {
		this.pptpTunnelId = pptpTunnelId;
	}

	public String getIpv4() {
		return ipv4;
	}

	public void setIpv4(String ipv4) {
		this.ipv4 = ipv4 == null ? null : ipv4.trim();
	}

	public String getIpv6() {
		return ipv6;
	}

	public void setIpv6(String ipv6) {
		this.ipv6 = ipv6 == null ? null : ipv6.trim();
	}

	public String getHwaddr() {
		return hwaddr;
	}

	public void setHwaddr(String hwaddr) {
		this.hwaddr = hwaddr == null ? null : hwaddr.trim();
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask == null ? null : netmask.trim();
	}

	public String getBroadcast() {
		return broadcast;
	}

	public void setBroadcast(String broadcast) {
		this.broadcast = broadcast == null ? null : broadcast.trim();
	}

	public String getGetway() {
		return getway;
	}

	public void setGetway(String getway) {
		this.getway = getway == null ? null : getway.trim();
	}

}