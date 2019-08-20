package com.wl4g.devops.common.bean.ci.dto;

/**
 * @author vjay
 * @date 2019-08-01 13:48:00
 */
public class TaskResult {

	private StringBuffer stringBuffer = new StringBuffer();

	private boolean isSuccess = true;

	public StringBuffer getStringBuffer() {
		return stringBuffer;
	}

	public void setStringBuffer(StringBuffer stringBuffer) {
		this.stringBuffer = stringBuffer;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean success) {
		isSuccess = success;
	}
}
