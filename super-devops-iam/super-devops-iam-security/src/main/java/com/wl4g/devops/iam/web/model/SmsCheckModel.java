package com.wl4g.devops.iam.web.model;

import java.io.Serializable;

/**
 * SMS verify check model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-24
 * @since
 */
public class SmsCheckModel implements Serializable {
	private static final long serialVersionUID = 2636165327046053795L;

	/**
	 * SMS PreCheck response key-name.
	 */
	final public static String KEY_SMS_CHECK = "checkSms";

	/**
	 * The remaining milliseconds to wait to re-apply for SMS dynamic password.
	 */
	private long remainDelayMs;

	public SmsCheckModel(long remainDelayMs) {
		super();
		this.remainDelayMs = remainDelayMs;
	}

	public long getRemainDelayMs() {
		return remainDelayMs;
	}

	public void setRemainDelayMs(long remainDelayMs) {
		this.remainDelayMs = remainDelayMs;
	}

}
