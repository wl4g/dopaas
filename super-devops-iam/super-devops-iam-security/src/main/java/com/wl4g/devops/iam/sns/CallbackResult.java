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
package com.wl4g.devops.iam.sns;

import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * {@link CallbackResult}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年2月7日 v1.0.0
 * @see
 */
public class CallbackResult {

	/**
	 * OAuth2 callback refreshUrl
	 */
	final private String refreshUrl;

	public CallbackResult(String refreshUrl) {
		hasTextOf(refreshUrl, "SNS oauth2 refreshUrl");
		this.refreshUrl = refreshUrl;
	}

	public boolean hasRefreshUrl() {
		return !isBlank(getRefreshUrl());
	}

	public String getRefreshUrl() {
		return refreshUrl;
	}

	@Override
	public String toString() {
		return refreshUrl;
	}

}
