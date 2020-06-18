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
package com.wl4g.devops.common.bean.scm.model;

import com.google.common.net.HostAndPort;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.validation.constraints.*;

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GenericInfo implements Serializable {
	final private static long serialVersionUID = -299157686801700764L;

	/**
	 * Application name(cluster name)
	 */
	@NotNull
	@NotBlank
	private String cluster;

	/**
	 * Name-space(configuration file-name, like spring.profiles)
	 */
	@NotNull
	@NotEmpty
	private List<String> namespaces = new ArrayList<>();

	/**
	 * Version release information
	 */
	private ReleaseMeta meta = new ReleaseMeta();

	public GenericInfo() {
		super();
	}

	public GenericInfo(String cluster, List<String> namespace) {
		this(cluster, namespace, null);
	}

	public GenericInfo(String cluster, List<String> namespaces, ReleaseMeta meta) {
		super();
		setCluster(cluster);
		setNamespaces(namespaces);
		setMeta(meta);
	}

	public List<String> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<String> namespaces) {
		if (!isEmpty(namespaces)) {
			this.namespaces.clear();
			this.namespaces.addAll(namespaces);
		}
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		if (!StringUtils.isEmpty(cluster) && !"NULL".equalsIgnoreCase(cluster)) {
			this.cluster = cluster;
		}
	}

	public ReleaseMeta getMeta() {
		return meta;
	}

	public void setMeta(ReleaseMeta meta) {
		if (meta != null) {
			this.meta = meta;
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " => " + toJSONString(this);
	}

	public void validation(boolean validVersion, boolean validRelease) {
		Assert.hasText(getCluster(), "`cluster` must not be empty");
		Assert.notEmpty(getNamespaces(), "`namespace` must not be empty");
		getMeta().validation(validVersion, validRelease);
	}

	public static class ReleaseInstance implements Serializable {
		private static final long serialVersionUID = -4826329780329773259L;

		@NotBlank
		@NotNull
		private String host;

		@Min(1024)
		@Max(65535)
		@NotNull
		private String endpoint;

		public ReleaseInstance() {
			super();
		}

		public ReleaseInstance(String host, String endpoint) {
			super();
			this.host = host;
			this.endpoint = endpoint;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			if (!StringUtils.isEmpty(host) && !"NULL".equalsIgnoreCase(host)) {
				this.host = host;
			}
		}

		public String getEndpoint() {
			return endpoint;
		}

		public void setEndpoint(String endpoint) {
			if (!StringUtils.isEmpty(endpoint) && !"NULL".equalsIgnoreCase(endpoint)) {
				this.endpoint = endpoint;
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((host == null) ? 0 : host.hashCode());
			result = prime * result + ((endpoint == null) ? 0 : endpoint.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ReleaseInstance other = (ReleaseInstance) obj;
			if (host == null) {
				if (other.host != null)
					return false;
			} else if (!host.equals(other.host))
				return false;
			if (endpoint == null) {
				if (other.endpoint != null)
					return false;
			} else if (!endpoint.equals(other.endpoint))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return getHost() + ":" + getEndpoint();
		}

		public void validation() {
			Assert.notNull(getHost(), "`host` is not allowed to be null.");
			Assert.notNull(getEndpoint(), "`port` is not allowed to be null.");
			HostAndPort.fromString(toString());
		}

		public static boolean eq(ReleaseInstance i1, ReleaseInstance i2) {
			return (i1 != null && i2 != null && StringUtils.equals(i1.toString(), i2.toString()));
		}

	}

	public static class ReleaseMeta implements Serializable {
		private static final long serialVersionUID = -4826329110329773259L;

		@NotBlank
		@NotNull
		private String version; // Release version(Required).

		@NotBlank
		@NotNull
		private String releaseId; // Release ID.

		public ReleaseMeta() {
			super();
		}

		public ReleaseMeta(String releaseId, String version) {
			super();
			this.releaseId = releaseId;
			this.version = version;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			if (!StringUtils.isEmpty(version) && !"NULL".equalsIgnoreCase(version)) {
				this.version = version;
			}
		}

		public String getReleaseId() {
			return releaseId;
		}

		public void setReleaseId(String releaseId) {
			if (!StringUtils.isEmpty(releaseId) && !"NULL".equalsIgnoreCase(releaseId)) {
				this.releaseId = releaseId;
			}
		}

		public String asText() {
			return getReleaseId() + "@" + getVersion();
		}

		@Override
		public String toString() {
			return asText();
		}

		public void validation(boolean validVersion, boolean validReleaseId) {
			if (validVersion) {
				Assert.notNull(getVersion(), "`version` is not allowed to be null.");
			}
			if (validReleaseId) {
				Assert.notNull(getReleaseId(), "`releaseId` is not allowed to be null.");
			}
		}

		public static ReleaseMeta of(String releaseMetaString) {
			if (!StringUtils.isEmpty(releaseMetaString) && releaseMetaString.contains("@")) {
				String arr[] = String.valueOf(releaseMetaString).split("@");
				return new ReleaseMeta(arr[0], arr[1]);
			}
			throw new IllegalStateException(String.format("Parmater 'releaseMetaString' : %s", releaseMetaString));
		}

	}

}