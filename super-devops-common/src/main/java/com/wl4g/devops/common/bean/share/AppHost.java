package com.wl4g.devops.common.bean.share;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class AppHost extends BaseBean implements Serializable {

	private static final long serialVersionUID = -7546448616357790576L;

	private String name;

	private String hostname;

	private Integer idcId;

	private Integer status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname == null ? null : hostname.trim();
	}

	public Integer getIdcId() {
		return idcId;
	}

	public void setIdcId(Integer idcId) {
		this.idcId = idcId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}