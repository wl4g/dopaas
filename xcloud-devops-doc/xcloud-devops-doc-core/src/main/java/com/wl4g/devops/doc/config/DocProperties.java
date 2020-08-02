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
package com.wl4g.devops.doc.config;

/**
 * @author vjay
 * @date 2020-01-15 16:17:00
 */
public class DocProperties {

	private String basePath;

	private String shareBaseUrl;

	private String docBaseUrl;

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getFilePath(String subPath) {
		return basePath + subPath;
	}

	public String getShareBaseUrl() {
		return shareBaseUrl;
	}

	public void setShareBaseUrl(String shareBaseUrl) {
		this.shareBaseUrl = shareBaseUrl;
	}

	public String getDocBaseUrl() {
		return docBaseUrl;
	}

	public void setDocBaseUrl(String docBaseUrl) {
		this.docBaseUrl = docBaseUrl;
	}
}