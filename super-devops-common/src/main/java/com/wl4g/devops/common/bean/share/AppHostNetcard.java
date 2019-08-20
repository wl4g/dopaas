package com.wl4g.devops.common.bean.share;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class AppHostNetcard extends BaseBean implements Serializable {

	private static final long serialVersionUID = -7546448616357790576L;

	private Integer hostId;

	private String name;

	private String status;

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