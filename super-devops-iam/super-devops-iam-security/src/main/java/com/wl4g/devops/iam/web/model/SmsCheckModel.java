package com.wl4g.devops.iam.web.model;

/**
 * SMS verify check model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-24
 * @since
 */
public class SmsCheckModel extends AuthenticationCodeModel {
	private static final long serialVersionUID = 2636165327046053795L;

	/**
	 * SMS PreCheck response key-name.
	 */
	final public static String KEY_SMS_CHECK = "checkSms";

	/**
	 * Enable SMS login apply for session.
	 */
	private boolean enabled;

	/**
	 * Mobile number.
	 */
	private Long mobileNum;

	/**
	 * The remaining milliseconds to wait to re-apply for SMS dynamic password.
	 */
	private Long remainDelayMs;

	public SmsCheckModel() {
		super();
	}

	public SmsCheckModel(Long mobileNum, Long remainDelayMs) {
		this(true, mobileNum, remainDelayMs);
	}

	public SmsCheckModel(boolean enabled, Long mobileNum, Long remainDelayMs) {
		super();
		this.enabled = enabled;
		this.mobileNum = mobileNum;
		this.remainDelayMs = remainDelayMs;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Long getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(Long mobileNum) {
		this.mobileNum = mobileNum;
	}

	public Long getRemainDelayMs() {
		return remainDelayMs;
	}

	public void setRemainDelayMs(Long remainDelayMs) {
		this.remainDelayMs = remainDelayMs;
	}

}
