package com.wl4g.devops.iam.common.web.model;

import com.google.common.annotations.Beta;

import java.io.Serializable;

import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;

/**
 * Sessions destroy model.
 *
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-31
 * @since
 */
@Beta
public class SessionDestroyModel implements Serializable {
	private static final long serialVersionUID = 2579844578836104918L;

	/**
	 * Destroy target principal.
	 */
	private String principal;

	/**
	 * Destroy target sessionIds.
	 */
	private String sessionId;

	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

}
