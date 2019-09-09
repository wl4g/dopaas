package com.wl4g.devops.iam.web.model;

import java.io.Serializable;

/**
 * CAPTCHA check model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-24
 * @since
 */
public class CaptchaCheckModel implements Serializable {
	private static final long serialVersionUID = 2636165327046053795L;

	/**
	 * CAPTCHA check response key-name.
	 */
	final public static String KEY_CAPTCHA_CHECK = "checkCaptcha";

	/**
	 * Enable login CAPTCHA token for session.
	 */
	private boolean enabled;

	/**
	 * CAPTCHA verify type support.
	 */
	private String support;

	/**
	 * Apply CAPTCHA URL.
	 */
	private String applyUri;

	public CaptchaCheckModel() {
		super();
	}

	public CaptchaCheckModel(boolean enabled) {
		super();
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}

	public String getApplyUri() {
		return applyUri;
	}

	public void setApplyUri(String applyUrl) {
		this.applyUri = applyUrl;
	}

}
