package com.wl4g.devops.common.web.model;

import java.io.Serializable;

/**
 * A general pull-down box data transmission model for Web
 * 
 * @author &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020年1月7日 v1.0.0
 * @see
 */
public class SelectionModel implements Serializable {
	private static final long serialVersionUID = -3412929918195969714L;

	/**
	 * Drop down box display value
	 */
	private String label;

	/**
	 * Drop down box background value.
	 */
	private String value;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return label + ":" + value;
	}

}
