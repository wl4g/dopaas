package com.wl4g.devops.common.bean.share;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class Dict extends BaseBean implements Serializable {
	private static final long serialVersionUID = -7546448616357790576L;

	private String key;

	private String value;

	private String label;

	private String labelEn;

	private String type;

	private String themes;

	private String icon;

	private Long sort;

	private Integer status;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key == null ? null : key.trim();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value == null ? null : value.trim();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label == null ? null : label.trim();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type == null ? null : type.trim();
	}

	public String getThemes() {
		return themes;
	}

	public void setThemes(String themes) {
		this.themes = themes == null ? null : themes.trim();
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon == null ? null : icon.trim();
	}

	public Long getSort() {
		return sort;
	}

	public void setSort(Long sort) {
		this.sort = sort;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getLabelEn() {
		return labelEn;
	}

	public void setLabelEn(String labelEn) {
		this.labelEn = labelEn;
	}

	@Override
	public String toString() {
		return "Dict [key=" + key + ", value=" + value + ", label=" + label + ", type=" + type + ", themes=" + themes + ", icon="
				+ icon + ", sort=" + sort + ", status=" + status + "]";
	}

}