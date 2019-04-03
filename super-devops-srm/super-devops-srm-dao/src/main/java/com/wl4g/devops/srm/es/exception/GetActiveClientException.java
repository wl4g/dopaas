package com.wl4g.devops.srm.es.exception;

/**
 * 从资源池获取活跃客户端异常
 *
 * @author guzhandong
 * @CREATE 2017-05-17 10:56 PM
 */
public class GetActiveClientException extends RuntimeException {
	private static final long serialVersionUID = -2940174063419073155L;

	protected int defReturnCode = 500;

	public int getDefReturnCode() {
		return this.defReturnCode;
	}

	public void setDefReturnCode(int defReturnCode) {
		this.defReturnCode = defReturnCode;
	}

	public GetActiveClientException() {
		super();
	}

	public GetActiveClientException(String message) {
		super(message);
	}

	public GetActiveClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public GetActiveClientException(Throwable cause) {
		super(cause);
	}

	protected GetActiveClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
