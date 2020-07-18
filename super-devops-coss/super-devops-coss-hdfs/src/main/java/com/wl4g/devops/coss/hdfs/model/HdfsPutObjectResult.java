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
package com.wl4g.devops.coss.hdfs.model;

import static java.util.Objects.isNull;

import java.util.Date;
import java.util.Properties;

import com.wl4g.devops.coss.common.model.CossPutObjectResult;

/**
 * The result class of a Put Object request.
 */
public class HdfsPutObjectResult extends CossPutObjectResult {

	/** The time this object expires, or null if it has no expiration */
	private Date expirationTime;

	/** The expiration rule for this object */
	private String expirationTimeRuleId;

	/** The content MD5 */
	private String contentMd5;

	/** The metadata returned as a result of PutObject operation. */
	private Properties metadata = new Properties();

	/**
	 * Indicate if the requester is charged for conducting this operation from
	 * Requester Pays Buckets.
	 */
	private boolean isRequesterCharged;

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	public String getExpirationTimeRuleId() {
		return expirationTimeRuleId;
	}

	public void setExpirationTimeRuleId(String expirationTimeRuleId) {
		this.expirationTimeRuleId = expirationTimeRuleId;
	}

	public String getContentMd5() {
		return contentMd5;
	}

	public void setContentMd5(String contentMd5) {
		this.contentMd5 = contentMd5;
	}

	public Properties getMetadata() {
		return metadata;
	}

	public void setMetadata(Properties metadata) {
		if (!isNull(metadata)) {
			this.metadata.putAll(metadata);
		}
	}

	public boolean isRequesterCharged() {
		return isRequesterCharged;
	}

	public void setRequesterCharged(boolean isRequesterCharged) {
		this.isRequesterCharged = isRequesterCharged;
	}

}