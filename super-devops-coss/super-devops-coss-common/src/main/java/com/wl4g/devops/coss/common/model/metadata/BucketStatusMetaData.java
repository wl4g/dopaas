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
package com.wl4g.devops.coss.common.model.metadata;

import java.io.Serializable;

/**
 * @author vjay
 * @date 2020-03-24 14:40:00
 */
public class BucketStatusMetaData implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	/**
	 * Storage usage (B)
	 */
	private long storageUsage = 0;

	/**
	 * Number of request
	 */
	private long numberOfRequests = 0;

	/**
	 * Number of Documents
	 */
	private long numberOfDocuments = 0;

	/**
	 * Create Date
	 */
	private long createDate = 0;

	/**
	 * Modify Date
	 */
	private long modifyDate = 0;

	public long getStorageUsage() {
		return storageUsage;
	}

	public void setStorageUsage(long storageUsage) {
		this.storageUsage = storageUsage;
	}

	public long getNumberOfRequests() {
		return numberOfRequests;
	}

	public void setNumberOfRequests(long numberOfRequests) {
		this.numberOfRequests = numberOfRequests;
	}

	public long getNumberOfDocuments() {
		return numberOfDocuments;
	}

	public void setNumberOfDocuments(long numberOfDocuments) {
		this.numberOfDocuments = numberOfDocuments;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	public long getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(long modifyDate) {
		this.modifyDate = modifyDate;
	}

}