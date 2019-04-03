package com.wl4g.devops.iam.authc;

import org.hibernate.validator.constraints.NotBlank;

import com.wl4g.devops.iam.common.authc.AbstractIamAuthenticationToken;

/**
 * SMS authentication token
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月19日
 * @since
 */
public class SmsAuthenticationToken extends AbstractIamAuthenticationToken {
	private static final long serialVersionUID = 8587329689973009598L;

	@NotBlank
	private String code;

	public SmsAuthenticationToken() {
		super();
	}

	public SmsAuthenticationToken(String code) {
		super();
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public String getHost() {
		return null;
	}

}
