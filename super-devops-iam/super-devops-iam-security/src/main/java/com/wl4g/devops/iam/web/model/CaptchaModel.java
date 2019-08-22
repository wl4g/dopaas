package com.wl4g.devops.iam.web.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

/**
 * CAPTCHA model.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月20日
 * @since
 */
public class CaptchaModel implements Serializable {
	private static final long serialVersionUID = -1335591707696587073L;

	@NotBlank(message = "'captchaToken' is required")
	private String captchaToken;

	public CaptchaModel() {
		super();
	}

	public String getCaptchaToken() {
		return captchaToken;
	}

	public void setCaptchaToken(String captchaToken) {
		this.captchaToken = captchaToken;
	}

}
