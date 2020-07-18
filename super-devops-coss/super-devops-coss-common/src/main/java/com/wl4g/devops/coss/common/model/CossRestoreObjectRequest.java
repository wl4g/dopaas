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
package com.wl4g.devops.coss.common.model;

public class CossRestoreObjectRequest {

	/**
	 * The name of the bucket containing the reference to the object to restore
	 * which is now stored in COSS provider Archived.
	 */
	private String bucketName;

	/**
	 * The key, the name of the reference to the object to restore, which is now
	 * stored in COSS provider Archived.
	 */
	private String key;

	/**
	 * Optional version ID specifying which version of the object to restore. If
	 * not specified, the most recent version will be restored.
	 */
	private String versionId;

	public CossRestoreObjectRequest() {
		super();
	}

	public CossRestoreObjectRequest(String bucketName, String key, String versionId) {
		super();
		this.bucketName = bucketName;
		this.key = key;
		this.versionId = versionId;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

}