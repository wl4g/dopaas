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
package com.wl4g.devops.coss.aws.model;

import com.wl4g.devops.coss.common.model.ObjectValue;

public class S3ObjectValue extends ObjectValue {

	/** The redirect location for this object */
	private String redirectLocation;

	private Integer taggingCount;

	/**
	 * Indicates if the requester is charged for downloading the data from
	 * Requester Pays Buckets.
	 */
	private boolean isRequesterCharged;

	public String getRedirectLocation() {
		return redirectLocation;
	}

	public void setRedirectLocation(String redirectLocation) {
		this.redirectLocation = redirectLocation;
	}

	public Integer getTaggingCount() {
		return taggingCount;
	}

	public void setTaggingCount(Integer taggingCount) {
		this.taggingCount = taggingCount;
	}

	public boolean isRequesterCharged() {
		return isRequesterCharged;
	}

	public void setRequesterCharged(boolean isRequesterCharged) {
		this.isRequesterCharged = isRequesterCharged;
	}

}