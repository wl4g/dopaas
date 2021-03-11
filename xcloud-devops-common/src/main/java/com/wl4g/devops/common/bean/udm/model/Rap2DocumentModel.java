/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
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
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.devops.common.bean.udm.model;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * RAP2 api interfaces data model.</br>
 * </br>
 * Refer to <a href="http://rap2.taobao.org/">http://rap2.taobao.org/</a>
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-16
 * @sine v1.0
 * @see
 */
@Getter
@Setter
@Builder
public class Rap2DocumentModel {

	private DataInfo data;

	@Getter
	@Setter
	@Builder
	public static class DataInfo {
		private long id;
		private String name;
		private String description;
		private String logo;
		private String token;
		private boolean visibility;
		private long ownerId;
		private String organizationId;
		private long creatorId;
		private String lockerId;
		private Date createdAt;
		private Date updatedAt;
		private String deletedAt;
		private CreationInfo creator;
		private OwnerInfo owner;
		private String locker;
		private List<String> members;
		private String organization;
		private List<String> collaborators;
		private List<ModuleInfo> modules;
		private boolean canUserEdit;
	}

	@Getter
	@Setter
	@Builder
	public static class OwnerInfo {
		private long id;
		private String fullname;
		private String email;
	}

	@Getter
	@Setter
	@Builder
	public static class CreationInfo {
		private long id;
		private String fullname;
		private String email;
	}

	@Getter
	@Setter
	@Builder
	public static class ModuleInfo {
		private long id;
		private String name;
		private String description;
		private int priority;
		private long creatorId;
		private long repositoryId;
		private Date createdAt;
		private Date updatedAt;
		private String deletedAt;
		private List<InterfaceInfo> interfaces;
	}

	@Getter
	@Setter
	@Builder
	public static class InterfaceInfo {
		private long id;
		private String name;
		private String url;
		private String method;
		private String bodyOption;
		private String description;
		private int priority;
		private int status;
		private long creatorId;
		private String lockerId;
		private long moduleId;
		private long repositoryId;
		private Date createdAt;
		private Date updatedAt;
		private String deletedAt;
		private String locker;
		private List<PropertyInfo> properties;
	}

	@Getter
	@Setter
	@Builder
	public static class PropertyInfo {
		private long id;
		private String scope;
		private String type;
		private int pos;
		private String name;
		private String rule;
		private String value;
		private String description;
		private int parentId;
		private int priority;
		private long interfaceId;
		private long creatorId;
		private long moduleId;
		private long repositoryId;
		private boolean required;
		private Date createdAt;
		private Date updatedAt;
		private String deletedAt;
	}

}