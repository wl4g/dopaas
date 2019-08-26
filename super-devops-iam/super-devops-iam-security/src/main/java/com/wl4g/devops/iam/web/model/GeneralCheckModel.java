package com.wl4g.devops.iam.web.model;

import java.io.Serializable;

/**
 * General check model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-24
 * @since
 */
public class GeneralCheckModel implements Serializable {
	private static final long serialVersionUID = 2636165327046053795L;

	/**
	 * General PreCheck response key-name.
	 */
	final public static String KEY_GENERAL_CHECK = "checkGeneral";

	/**
	 * Encrypted secret public key requested before login returns.
	 */
	private String secret;

	public GeneralCheckModel() {
		super();
	}

	public GeneralCheckModel(String secret) {
		super();
		this.secret = secret;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
