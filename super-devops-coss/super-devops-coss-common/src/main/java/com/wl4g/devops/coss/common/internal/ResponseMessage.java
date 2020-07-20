/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.coss.common.internal;

import com.wl4g.devops.components.tools.common.remoting.standard.HttpStatus;
import com.wl4g.devops.coss.common.utils.COSSHeaders;

/**
 * {@link ResponseMessage}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月2日
 * @since
 */
public class ResponseMessage extends HttpMesssage {

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
		return statusCode / 100 == HttpStatus.OK.value() / 100;
	}

	public String getErrorResponseAsString() {
		return errorResponseAsString;
	}

	public void setErrorResponseAsString(String errorResponseAsString) {
		this.errorResponseAsString = errorResponseAsString;
	}

}