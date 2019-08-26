package com.wl4g.devops.iam.web.model;

import java.io.Serializable;

import com.wl4g.devops.common.annotation.Unused;

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
	 * CAPTCHA simple(graph2d/JPEG) type.
	 */
	final public static String CAPTCHA_SIMPLE_TPYE = "simple";

	/**
	 * CAPTCHA gap slider type.
	 */
	@Unused
	final public static String CAPTCHA_GAP_SLIDER_TPYE = "gapslider";

	/**
	 * Enable login CAPTCHA token for session.
	 */
	private boolean enabled;

	/**
	 * CAPTCHA type.
	 */
	private String type;

	/**
	 * Apply CAPTCHA URL.
	 */
	private String applyUrl;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getApplyUrl() {
		return applyUrl;
	}

	public void setApplyUrl(String applyUrl) {
		this.applyUrl = applyUrl;
	}

}
