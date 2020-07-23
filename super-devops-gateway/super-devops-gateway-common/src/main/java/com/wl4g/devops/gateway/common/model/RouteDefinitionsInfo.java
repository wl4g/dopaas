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
package com.wl4g.devops.gateway.common.model;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNull;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

/**
 * {@link RouteDefinitionsInfo}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-07-23
 * @since
 * @see {@link org.springframework.cloud.gateway.route.RouteDefinition}
 */
@Validated
public class RouteDefinitionsInfo implements Serializable {
	private static final long serialVersionUID = 4246012683066016447L;

	@NotEmpty
	private String id = UUID.randomUUID().toString();
	@NotEmpty
	@Valid
	private List<PredicateInfo> predicates = new ArrayList<>();
	@Valid
	private List<OperableFilterInfo> filters = new ArrayList<>();
	@NotNull
	private URI uri;

	private Map<String, Object> metadata = new HashMap<>();
	private int order = 0;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<PredicateInfo> getPredicates() {
		return predicates;
	}

	public void setPredicates(List<PredicateInfo> predicates) {
		this.predicates = predicates;
	}

	public List<OperableFilterInfo> getFilters() {
		return filters;
	}

	public void setFilters(List<OperableFilterInfo> filters) {
		this.filters = filters;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		RouteDefinitionsInfo that = (RouteDefinitionsInfo) o;
		return this.order == that.order && Objects.equals(this.id, that.id) && Objects.equals(this.predicates, that.predicates)
				&& Objects.equals(this.filters, that.filters) && Objects.equals(this.uri, that.uri)
				&& Objects.equals(this.metadata, that.metadata);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.predicates, this.filters, this.uri, this.metadata, this.order);
	}

	@Override
	public String toString() {
		return "RouteDefinition{" + "id='" + id + '\'' + ", predicates=" + predicates + ", filters=" + filters + ", uri=" + uri
				+ ", order=" + order + ", metadata=" + metadata + '}';
	}

	/**
	 * {@link PredicateInfo}
	 *
	 * @since
	 */
	@Validated
	public static class PredicateInfo implements Serializable {
		private static final long serialVersionUID = 1L;

		@NotNull
		private String name;
		private Map<String, String> args = new LinkedHashMap<>();

		public PredicateInfo() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			PredicateType.validate(name);
			this.name = name;
		}

		public Map<String, String> getArgs() {
			return args;
		}

		public void setArgs(Map<String, String> args) {
			this.args = args;
		}

		public void addArg(String key, String value) {
			this.args.put(key, value);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			PredicateInfo that = (PredicateInfo) o;
			return Objects.equals(name, that.name) && Objects.equals(args, that.args);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, args);
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("PredicateDefinition{");
			sb.append("name='").append(name).append('\'');
			sb.append(", args=").append(args);
			sb.append('}');
			return sb.toString();
		}

	}

	/**
	 * {@link OperableFilterInfo}
	 *
	 * @since
	 */
	@Validated
	public static class OperableFilterInfo implements Serializable {
		private static final long serialVersionUID = 6514822745098372901L;

		@NotNull
		private String name;
		private Map<String, String> args = new LinkedHashMap<>();

		public OperableFilterInfo() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			OperableFilterType.validate(name);
			this.name = name;
		}

		public Map<String, String> getArgs() {
			return args;
		}

		public void setArgs(Map<String, String> args) {
			this.args = args;
		}

		public void addArg(String key, String value) {
			this.args.put(key, value);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			OperableFilterInfo that = (OperableFilterInfo) o;
			return Objects.equals(name, that.name) && Objects.equals(args, that.args);
		}

		@Override
		public int hashCode() {
			return Objects.hash(name, args);
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("FilterDefinition{");
			sb.append("name='").append(name).append('\'');
			sb.append(", args=").append(args);
			sb.append('}');
			return sb.toString();
		}

	}

	/**
	 * {@link OperableFilterType}
	 *
	 * @since
	 * @see <a href=
	 *      "https://cloud.spring.io/spring-cloud-gateway/reference/html/#gatewayfilter-factories">https://cloud.spring.io/spring-cloud-gateway/reference/html/#gatewayfilter-factories</a>
	 */
	public static enum OperableFilterType {

		// Request headers.

		AddRequestHeader,

		RemoveRequestHeader,

		SetRequestHeader,

		// Request parameters.

		AddRequestParameter,

		RemoveRequestParameter,

		// Response headers.

		AddResponseHeader,

		RemoveResponseHeader,

		SetResponseHeader,

		SetStatus,

		DedupeResponseHeader,

		// Request path.

		RewritePath;

		/**
		 * Parse {@link OperableFilterType} of filter name.
		 * 
		 * @param filterName
		 * @return
		 */
		public final static OperableFilterType of(String filterName) {
			for (OperableFilterType t : values()) {
				if (t.name().equals(filterName)) {
					return t;
				}
			}
			return null;
		}

		/**
		 * Validation filter name with {@link OperableFilterType}
		 * 
		 * @param filterName
		 */
		public final static void validate(String filterName) {
			notNull(of(filterName), "Invalid filter alias: %s", filterName);
		}

	}

	/**
	 * {@link PredicateType}
	 *
	 * @since
	 * @see <a href=
	 *      "https://cloud.spring.io/spring-cloud-gateway/reference/html/#gateway-request-predicates-factories">https://cloud.spring.io/spring-cloud-gateway/reference/html/#gateway-request-predicates-factories</a>
	 */
	public static enum PredicateType {

		Before,

		After,

		Between,

		Cookie,

		Header,

		Host,

		Method,

		Path,

		Query,

		RemoteAddr,

		Weight;

		/**
		 * Parse {@link PredicateType} of predication name.
		 * 
		 * @param predicateName
		 * @return
		 */
		public final static PredicateType of(String predicateName) {
			for (PredicateType t : values()) {
				if (t.name().equals(predicateName)) {
					return t;
				}
			}
			return null;
		}

		/**
		 * Validation filter name with {@link PredicateType}
		 * 
		 * @param predicateName
		 */
		public final static void validate(String predicateName) {
			notNull(of(predicateName), "Invalid route predicate alias: %s", predicateName);
		}

	}

}
