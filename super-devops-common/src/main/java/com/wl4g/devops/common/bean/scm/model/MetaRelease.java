package com.wl4g.devops.common.bean.scm.model;

import javax.validation.constraints.NotBlank;

/**
 * @author vjay
 * @date 2019-06-06 11:57:00
 */
public class MetaRelease extends PreRelease {

	private static final long serialVersionUID = 3673632779071727079L;

	@NotBlank
	private String token;

	private String secretKey;

	private String algName;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getAlgName() {
		return algName;
	}

	public void setAlgName(String algName) {
		this.algName = algName;
	}
}
