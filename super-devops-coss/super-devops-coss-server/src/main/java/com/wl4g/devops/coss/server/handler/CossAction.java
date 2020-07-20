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
package com.wl4g.devops.coss.server.handler;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNull;

import io.netty.handler.codec.http.HttpMethod;
import static io.netty.handler.codec.http.HttpMethod.*;

public enum CossAction {

	createBucket("createBucket", PUT),

	listBuckets("listBuckets", GET),

	deleteBucket("deleteBucket", DELETE),

	getBucketMetadata("getBucketMetadata", GET),

	getBucketAcl("getBucketAcl", GET),

	setBucketAcl("setBucketAcl", PUT),

	listObjects("listObjects", GET),

	getObject("getObject", GET),

	putObject("putObject", PUT),

	putObjectMetaData("putObjectMetaData", PUT),

	copyObject("copyObject", PUT),

	deleteObject("deleteObject", DELETE),

	deleteVersion("deleteVersion", DELETE),

	restoreObject("restoreObject", PUT),

	getObjectAcl("getObjectAcl", GET),

	setObjectAcl("setObjectAcl", PUT),

	doesObjectExist("doesObjectExist", GET),

	createSymlink("createSymlink", PUT),

	getSymlink("getSymlink", GET),

	getUrl("getUrl", GET);

	/**
	 * Coss interface action name
	 */
	final private String actionName;

	/**
	 * Coss interface action http method.
	 */
	final private HttpMethod method;

	private CossAction(String actionName, HttpMethod method) {
		this.actionName = actionName;
		this.method = method;
	}

	public String getActionName() {
		return actionName;
	}

	public HttpMethod getMethod() {
		return method;
	}

	/**
	 * Convert of action.
	 * 
	 * @param action
	 * @return
	 */
	public static CossAction of(String action) {
		CossAction _action = safeOf(action);
		notNull(_action, "Unknown action: %s", action);
		return _action;
	}

	/**
	 * Safe convert of action.
	 * 
	 * @param action
	 * @return
	 */
	public static CossAction safeOf(String action) {
		for (CossAction _action : values()) {
			if (_action.getActionName().equals(action) || _action.name().equals(action)) {
				return _action;
			}
		}
		return null;
	}

}