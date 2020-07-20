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
package com.wl4g.devops.coss.common.model.bucket;

import java.util.Date;

import com.wl4g.devops.coss.common.model.Owner;

/**
 * Bucket is the namespace in OSS. You could think it's a folder or container
 * under OSS.
 * <p>
 * Bucket name is globally unique in OSS and is immutable. Every object must
 * belong to a bucket. An application such as picture sharing website could be
 * mapped to one or multiple buckets. An OSS account could only create up to 10
 * bucket. And there's no limit on the files count or size under a bucket.
 * </p>
 * Bucket naming rules:
 * <ul>
 * <li>Can only contain low case letter, number or dash(-).</li>
 * <li>Can only start with low case letter or number.</li>
 * <li>The length must be between 3 to 63 bytes.</li>
 * </ul>
 */
public class Bucket {

	/** Bucket name */
	private String name;

	/** Bucket owner */
	private Owner owner;

	/** Created date. */
	private Date creationDate;

	/**
	 * Default constructor.
	 */
	public Bucket() {
	}

	/**
	 * Constructor with the bucket name parameter.
	 * 
	 * @param name
	 *            Bucket name.
	 */
	public Bucket(String name) {
		setName(name);
	}

	/**
	 * Gets the {@link Owner}.
	 * 
	 * @return The bucket owner or null if the owner is not known.
	 */
	public Owner getOwner() {
		return owner;
	}

	/**
	 * Sets the bucket owner (only used by SDK itself).
	 * 
	 * @param owner
	 *            Bucket owner.
	 */
	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	/**
	 * Gets the bucket's creation time.
	 * 
	 * @return Bucket's creation time or null if the creation time is unknown.
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Sets teh bucket's creation time.(it's only used by SDK itself).
	 * 
	 * @param creationDate
	 *            Bucket's creation time.
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Gets the bucket name
	 * 
	 * @return Bucket name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the bucket name (should only be used by the SDK itself).
	 * 
	 * @param name
	 *            Bucket name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "CossBucket [name=" + name + ", owner=" + owner + ", creationDate=" + creationDate + "]";
	}

}