package com.wl4g.devops.iam.web.model;

import java.io.Serializable;

/**
 * SMS PreCheck response.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月20日
 * @since
 */
public class SMSVerifyCheckModel implements Serializable {
	private static final long serialVersionUID = -5279195217830694103L;

	final public static String KEY_SMS_CHECKER = "checkSms";

	/**
	 * Apply SMS verification code to create a timestamp
	 */
	private long createTime;

	/**
	 * The number of milliseconds to wait after applying for an SMS dynamic
	 * password (you can reapply).
	 */
	private long delayMs;

	/**
	 * The remaining milliseconds to wait to re-apply for SMS dynamic password
	 */
	private long remainDelayMs;

	public SMSVerifyCheckModel() {
		super();
	}

	public SMSVerifyCheckModel(long createTime, long delayMs, long remainDelayMs) {
		setCreateTime(createTime);
		setDelayMs(delayMs);
		setRemainDelayMs(remainDelayMs);
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getDelayMs() {
		return delayMs;
	}

	public void setDelayMs(long delayMs) {
		this.delayMs = delayMs;
	}

	public long getRemainDelayMs() {
		return remainDelayMs;
	}

	public void setRemainDelayMs(long remainDelayMs) {
		this.remainDelayMs = remainDelayMs;
	}

}
