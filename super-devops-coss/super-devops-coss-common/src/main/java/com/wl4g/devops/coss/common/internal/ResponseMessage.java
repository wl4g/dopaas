package com.wl4g.devops.coss.common.internal;

import com.wl4g.devops.coss.common.utils.COSSHeaders;

public class ResponseMessage extends HttpMesssage {

	private static final int HTTP_SUCCESS_STATUS_CODE = 200;

	private String uri;
	private int statusCode;

	// For convenience of logging invalid response
	private String errorResponseAsString;

	public String getUri() {
		return uri;
	}

	public void setUrl(String uri) {
		this.uri = uri;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getRequestId() {
		return getHeaders().get(COSSHeaders.OSS_HEADER_REQUEST_ID);
	}

	public boolean isSuccessful() {
		return statusCode / 100 == HTTP_SUCCESS_STATUS_CODE / 100;
	}

	public String getErrorResponseAsString() {
		return errorResponseAsString;
	}

	public void setErrorResponseAsString(String errorResponseAsString) {
		this.errorResponseAsString = errorResponseAsString;
	}

}