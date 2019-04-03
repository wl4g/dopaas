package com.wl4g.devops.common.bean.iam.model;

import org.hibernate.validator.constraints.NotBlank;

import com.wl4g.devops.common.utils.serialize.JacksonUtils;

/**
 * Application grant ticket wrap
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月22日
 * @since ServiceTicket
 */
public final class LoggedModel {

	/**
	 * Temporary authorization code(Used for fast-CAS login successfully
	 * returned to application), only single use of work is effective.
	 */
	@NotBlank
	private String grantTicket;

	public LoggedModel() {
		super();
	}

	public LoggedModel(String grantTicket) {
		this.setGrantTicket(grantTicket);
	}

	public final String getGrantTicket() {
		return grantTicket;
	}

	public final void setGrantTicket(String serviceTicket) {
		this.grantTicket = serviceTicket;
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

}
