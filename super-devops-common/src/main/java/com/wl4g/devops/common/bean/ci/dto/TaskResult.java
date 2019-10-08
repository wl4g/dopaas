package com.wl4g.devops.common.bean.ci.dto;

import java.io.File;

/**
 * @author vjay
 * @date 2019-08-01 13:48:00
 */
public class TaskResult {

	private StringBuffer stringBuffer = new StringBuffer();

	private boolean isSuccess = true;

	private File logFile;

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

	public File getLogFile() {
		return logFile;
	}

	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}
}
