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
package com.wl4g.devops.coss.model.bucket;

import static java.util.Objects.isNull;

import java.util.HashMap;
import java.util.Map;

public class BucketMetadata {

	/**
	 * Bucket name.
	 */
	private String bucketName;

	/**
	 * Bucket region.
	 */
	private String bucketRegion;

	/**
	 * Other extended properties.
	 */
	private Map<String, String> attributes = new HashMap<>();

	public BucketMetadata() {
		super();
	}

	public BucketMetadata(String bucketName) {
		super();
		this.bucketName = bucketName;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getBucketRegion() {
		return bucketRegion;
	}

	public void setBucketRegion(String bucketRegion) {
		this.bucketRegion = bucketRegion;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		if (!isNull(attributes)) {
			this.attributes.putAll(attributes);
		}
	}

}
