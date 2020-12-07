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
package com.wl4g.devops.common.bean.doc.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * {@link Swagger2ApiModel}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-07
 * @sine v1.0
 * @see
 */
@Getter
@Setter
// @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
// @JsonSubTypes({ @JsonSubTypes.Type(value = InputPageModel.class, name =
// "input"),
// @JsonSubTypes.Type(value = NumberPageModel.class, name = "number") })

public class Swagger2ApiModel {

	private String swagger;
	private AppInfo info;
	private String host;
	private String basePath;
	private List<Tags> tags;
	private Map<String, Map<String, PathInfo>> paths;
	private Map<String, DefinitionInfo> definitions;

	@Getter
	@Setter
	public static class AppInfo {
		private String version;
		private String title;
		private ContactInfo contact;
		private LicenseInfo license;

		@Getter
		@Setter
		public static class ContactInfo {
			private String name;
			private String url;
			private String email;
		}

		@Getter
		@Setter
		public static class LicenseInfo {
		}

	}

	@Getter
	@Setter
	public static class Tags {
		private String name;
		private String description;
	}

	@Getter
	@Setter
	public static class PathInfo {
		private List<String> tags;
		private String summary;
		private String operationId;
		private List<String> consumes;
		private List<String> produces;
		private List<ParameterInfo> parameters;
		private Map<String, ResponseInfo> responses;
		private boolean deprecated;
		@JsonProperty("x-order")
		private String xOrder;

		@Getter
		@Setter
		public static class ParameterInfo {
			private String in;
			private String name;
			private String description;
			private boolean required;
			private String type;
			private String format;
			private Schema schema;
		}

		@Getter
		@Setter
		public static class ResponseInfo {
			private String description;
			private Schema schema;
		}

	}

	@Getter
	@Setter
	@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "$id")
	public static class DefinitionInfo {
		private String type;
		private String title;
		private Map<String, PropertyInfo> properties;

		@Getter
		@Setter
		public static class PropertyInfo extends DefinitionInfo {
			private String type;
			private String format;
			private Boolean readOnly;

			// @JsonIdentityReference
			// @JsonBackReference("items")
			private Items items;

			@Getter
			@Setter
			public static class Items extends DefinitionInfo {
			}

		}

	}

	@Getter
	@Setter
	public static class Schema extends DefinitionInfo {
	}

}