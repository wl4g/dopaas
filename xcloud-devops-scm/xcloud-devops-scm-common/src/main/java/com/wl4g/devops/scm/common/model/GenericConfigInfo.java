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
package com.wl4g.devops.scm.common.model;

import com.google.common.net.HostAndPort;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.*;

import static com.wl4g.components.common.collection.CollectionUtils2.isEmpty;
import static com.wl4g.components.common.lang.Assert2.hasText;
import static com.wl4g.components.common.lang.Assert2.notEmpty;
import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link GenericConfigInfo}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018-08-17
 * @since
 */
public abstract class GenericConfigInfo implements Serializable {
	final private static long serialVersionUID = -299157686801700764L;

	/**
	 * Application name(cluster name)
	 */
	@NotNull
	@NotBlank
	private String cluster;

	/**
	 * Configuration files, The like spring.profiles.
	 */
	@NotNull
	@NotEmpty
	private List<String> profiles = new ArrayList<>();

	/**
	 * Configuration version and release info
	 */
	private ConfigMeta meta = new ConfigMeta();

	public GenericConfigInfo() {
		super();
	}

	public GenericConfigInfo(String cluster, List<String> namespace) {
		this(cluster, namespace, null);
	}

	public GenericConfigInfo(String cluster, List<String> namespaces, ConfigMeta meta) {
		setCluster(cluster);
		setProfiles(namespaces);
		setMeta(meta);
	}

	public List<String> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<String> namespaces) {
		if (!isEmpty(namespaces)) {
			this.profiles.clear();
			this.profiles.addAll(namespaces);
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

	public ConfigMeta getMeta() {
		return meta;
	}

	public void setMeta(ConfigMeta meta) {
		if (meta != null) {
			this.meta = meta;
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " => " + toJSONString(this);
	}

	public void validate(boolean validVersion, boolean validRelease) {
		hasText(getCluster(), "`cluster` must not be empty");
		notEmpty(getProfiles(), "`namespace` must not be empty");
		getMeta().validation(validVersion, validRelease);
	}

	/**
	 * {@link ConfigNode}
	 *
	 * @since
	 */
	public static class ConfigNode implements Serializable {
		private static final long serialVersionUID = -4826329780329773259L;

		@NotBlank
		@NotNull
		private String host;

		@NotBlank
		@NotNull
		private String serviceId;

		public ConfigNode() {
			super();
		}

		public ConfigNode(String host, String serviceId) {
			super();
			this.host = host;
			this.serviceId = serviceId;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			if (!StringUtils.isEmpty(host) && !"NULL".equalsIgnoreCase(host)) {
				this.host = host;
			}
		}

		public String getServiceId() {
			return serviceId;
		}

		public void setServiceId(String serviceId) {
			if (!StringUtils.isEmpty(serviceId) && !"NULL".equalsIgnoreCase(serviceId)) {
				this.serviceId = serviceId;
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((host == null) ? 0 : host.hashCode());
			result = prime * result + ((serviceId == null) ? 0 : serviceId.hashCode());
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
			ConfigNode other = (ConfigNode) obj;
			if (host == null) {
				if (other.host != null)
					return false;
			} else if (!host.equals(other.host))
				return false;
			if (serviceId == null) {
				if (other.serviceId != null)
					return false;
			} else if (!serviceId.equals(other.serviceId))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return getHost() + ":" + getServiceId();
		}

		public void validation() {
			notNull(getHost(), "`host` is not allowed to be null.");
			notNull(getServiceId(), "`port` is not allowed to be null.");
			HostAndPort.fromString(toString());
		}

		public static boolean eq(ConfigNode i1, ConfigNode i2) {
			return (i1 != null && i2 != null && StringUtils.equals(i1.toString(), i2.toString()));
		}

	}

	/**
	 * {@link ConfigMeta}
	 *
	 * @since
	 */
	public static class ConfigMeta implements Serializable {
		private static final long serialVersionUID = -4826329110329773259L;

		@NotBlank
		@NotNull
		private String version; // Release version(Required).

		@NotBlank
		@NotNull
		private String releaseId; // Release ID.

		public ConfigMeta() {
			super();
		}

		public ConfigMeta(String releaseId, String version) {
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
				notNull(getVersion(), "`version` is not allowed to be null.");
			}
			if (validReleaseId) {
				notNull(getReleaseId(), "`releaseId` is not allowed to be null.");
			}
		}

		public static ConfigMeta of(String releaseMetaString) {
			if (!StringUtils.isEmpty(releaseMetaString) && releaseMetaString.contains("@")) {
				String arr[] = String.valueOf(releaseMetaString).split("@");
				return new ConfigMeta(arr[0], arr[1]);
			}
			throw new IllegalStateException(String.format("Parmater 'releaseMetaString' : %s", releaseMetaString));
		}

	}

}