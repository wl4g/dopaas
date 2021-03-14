/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.paas.cmdb.es.exception;

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