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
package com.wl4g.devops.coss.aliyun.model.bucket;

import com.wl4g.devops.coss.common.model.Owner;
import com.wl4g.devops.coss.common.model.bucket.Bucket;

public class OssBucket extends Bucket {

	/** Bucket location */
	private String location;

	/** Storage class (e.g. aliyun-oss => Standard, IA, Archive) */
	private String storageType;

	/** External endpoint.It could be accessed from anywhere. */
	private String extranetEndpoint;

	/**
	 * Internal endpoint. It could be accessed within AliCloud under the same
	 * location.
	 */
	private String intranetEndpoint;

	public OssBucket() {
	}

	public OssBucket(com.aliyun.oss.model.Bucket ossBucket) {
		setName(ossBucket.getName());
		setLocation(ossBucket.getLocation());
		setOwner(new Owner(ossBucket.getOwner().getId(), ossBucket.getOwner().getDisplayName()));
		setStorageType(ossBucket.getStorageClass().name());
		setCreationDate(ossBucket.getCreationDate());
		setExtranetEndpoint(ossBucket.getExtranetEndpoint());
		setIntranetEndpoint(ossBucket.getIntranetEndpoint());
	}

	/**
	 * Gets the bucket location.
	 * 
	 * @return Bucket location.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the bucket location.
	 * 
	 * @param location
	 */
	public OssBucket setLocation(String location) {
		this.location = location;
		return this;
	}

	/**
	 * Gets the storage class
	 * 
	 * @return storage class
	 */
	public String getStorageType() {
		return storageType;
	}

	/**
	 * Sets the bucket's storage class
	 * 
	 * @param storageClass
	 */
	public OssBucket setStorageType(String storageClass) {
		this.storageType = storageClass;
		return this;
	}

	/**
	 * Gets the external endpoint.
	 * 
	 * @return external endpoint
	 */
	public String getExtranetEndpoint() {
		return extranetEndpoint;
	}

	/**
	 * Sets the external endpoint.
	 * 
	 * @param endpoint
	 *            external endpoint
	 */
	public OssBucket setExtranetEndpoint(String endpoint) {
		this.extranetEndpoint = endpoint;
		return this;
	}

	/**
	 * Gets the internal endpoint.
	 * 
	 * @return Internal endpoint
	 */
	public String getIntranetEndpoint() {
		return intranetEndpoint;
	}

	/**
	 * Sets the internal endpoint.
	 * 
	 * @param endpoint
	 *            Internal endpoint
	 */
	public OssBucket setIntranetEndpoint(String endpoint) {
		this.intranetEndpoint = endpoint;
		return this;
	}

	/**
	 * The override of toString(). Returns the bucket name, creation date, owner
	 * and location, with optional storage class.
	 */
	@Override
	public String toString() {
		if (storageType == null) {
			return "OSSBucket [name=" + getName() + ", creationDate=" + getCreationDate() + ", owner=" + getOwner()
					+ ", location=" + getLocation() + "]";
		} else {
			return "OSSBucket [name=" + getName() + ", creationDate=" + getCreationDate() + ", owner=" + getOwner()
					+ ", location=" + getLocation() + ", storageClass=" + getStorageType() + "]";
		}
	}

}