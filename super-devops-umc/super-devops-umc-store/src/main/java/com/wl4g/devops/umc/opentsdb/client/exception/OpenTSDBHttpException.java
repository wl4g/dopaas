package com.wl4g.devops.umc.opentsdb.client.exception;

import com.wl4g.devops.umc.opentsdb.client.bean.response.ErrorResponse;

public class OpenTSDBHttpException extends RuntimeException {

	private static final long serialVersionUID = 597320744973506543L;

	final private ErrorResponse errorResponse;

	public OpenTSDBHttpException(ErrorResponse errorResponse) {
		super(errorResponse.toString());
		this.errorResponse = errorResponse;
	}

	public ErrorResponse getErrorResponse() {
		return errorResponse;
	}

}
