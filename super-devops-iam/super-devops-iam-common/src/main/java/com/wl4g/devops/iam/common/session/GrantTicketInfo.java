package com.wl4g.devops.iam.common.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.util.Assert;
import org.apache.shiro.util.StringUtils;

import com.wl4g.devops.common.utils.serialize.JacksonUtils;

/**
 * Grant ticket information.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月12日
 * @since
 */
public class GrantTicketInfo implements Serializable {
	private static final long serialVersionUID = -3499216861786196071L;

	/**
	 * application => grantTicket
	 */
	private Map<String, String> applications = new HashMap<>();

	public GrantTicketInfo() {
		super();
	}

	public GrantTicketInfo(Map<String, String> applications) {
		super();
		this.setApplications(applications);
	}

	/**
	 * application => grantTicket
	 * 
	 * @return
	 */
	public Map<String, String> getApplications() {
		return applications;
	}

	public GrantTicketInfo setApplications(Map<String, String> applications) {
		Assert.notEmpty(applications, "'applications' must not be empty");
		this.applications.putAll(applications);
		return this;
	}

	public GrantTicketInfo addApplications(String grantApp, String grantTicket) {
		Assert.isTrue(StringUtils.hasText(grantApp), "'applications' must not be empty");
		Assert.isTrue(StringUtils.hasText(grantTicket), "'grantTicket' must not be empty");
		this.applications.put(grantApp, grantTicket);
		return this;
	}

	public boolean hasApplications() {
		return getApplications() != null && !getApplications().isEmpty();
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

}