package com.wl4g.devops.iam.web.model;

import javax.validation.constraints.NotBlank;

/**
 * General PreCheck response.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月20日
 * @since
 */
public class GeneralCheckModel extends CaptchaModel {
	private static final long serialVersionUID = -5279195217830694101L;

	final public static String KEY_GENERAL_CHECKER = "checkGeneral";

	/**
	 * Encrypted public key requested before login returns key name
	 */
	@NotBlank(message = "'secret' is required")
	private String secret;

	public GeneralCheckModel() {
		super();
	}

	public GeneralCheckModel(String captchaToken, String secret) {
		this.secret = secret;
		setCaptchaToken(captchaToken);
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}