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

import com.wl4g.devops.coss.common.model.ACL;

/**
 * The predefined Access Control List (ACL)
 * <p>
 * It defines some common permissions.
 * </p>
 */
public enum ACL {

	/**
	 * This is only for object, means the permission inherits the bucket's
	 * permission.
	 */
	Default("default"), // 0755

	/**
	 * The owner has the {@link com.amazonaws.services.s3.model.Permission},
	 * other {@link com.aliyun.oss.model.ObjectPermission} does not have access.
	 */
	Private("private"), // 0700

	/**
	 * Owner has the full control of the object. Other users only have read
	 * access.
	 */
	PublicRead("public-read"), // 0755

	/**
	 * Both the owner and {@link GroupGrantee#AllUsers} have
	 * {@link Permission#FullControl}. It's not safe and thus not recommended.
	 */
	PublicReadWrite("public-read-write"); // 0777

	private String cannedAclString;

	private ACL(String cannedAclString) {
		this.cannedAclString = cannedAclString;
	}

	@Override
	public String toString() {
		return this.cannedAclString;
	}

	public static ACL parse(String acl) {
		for (ACL cacl : ACL.values()) {
			if (cacl.toString().equalsIgnoreCase(acl) || acl.equalsIgnoreCase(cacl.name())) {
				return cacl;
			}
		}

		throw new IllegalArgumentException("Unable to parse the provided acl " + acl);
	}

	public static String[] cannedAclStrings() {
		String[] result = new String[ACL.values().length];
		for (int i = 0; i < ACL.values().length; i++) {
			result[i] = ACL.values()[i].toString();
		}
		return result;
	}

}