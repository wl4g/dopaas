package com.wl4g.devops.common.bean.umc.model;

import java.io.Serializable;

/**
 * Physical information model
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class PhysicalInfo implements Serializable {

	private static final long serialVersionUID = 381411777614066880L;

	private String id;

	private String type;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
