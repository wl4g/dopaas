package com.wl4g.devops.common.bean.share;

import com.wl4g.devops.common.bean.scm.BaseBean;

import java.io.Serializable;

public class Dict extends BaseBean implements Serializable {

	private static final long serialVersionUID = 381411777614066880L;

	private String value;

	private String label;

	private String type;

	private String description;

	private Integer sort;

	private Integer status;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}