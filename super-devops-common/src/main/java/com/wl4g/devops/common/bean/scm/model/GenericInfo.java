/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class GenericInfo implements Serializable {
	final private static long serialVersionUID = -299157686801700764L;

	/**
	 * Application name
	 */
	@NotNull
	@NotBlank
	private String group;

	/**
	 * Name-space(configuration file-name, like spring.profiles)
	 */
	@NotNull
	@NotBlank
	private String profile;

	/**
	 * Version release information
	 */
	private ReleaseMeta meta = new ReleaseMeta();

	public GenericInfo() {
		super();
	}

	public GenericInfo(String group, String namespace) {
		this(group, namespace, null);
	}

	public GenericInfo(String group, String namespace, ReleaseMeta meta) {
		super();
		setGroup(group);
		setProfile(namespace);
		setMeta(meta);
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String namespace) {
		this.profile = namespace;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		if (!StringUtils.isEmpty(group) && !"NULL".equalsIgnoreCase(group)) {
			this.group = group;
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
		return JacksonUtils.toJSONString(this);
	}

	public void validation(boolean versionValidate, boolean releaseIdValidate) {
		Assert.hasText(getGroup(), "`group` must not be empty");
		Assert.hasText(getProfile(), "`namespace` must not be empty");
		getMeta().validation(versionValidate, releaseIdValidate);
	}

	public static class ReleaseInstance implements Serializable {
		private static final long serialVersionUID = -4826329780329773259L;

		@NotBlank
		@NotNull
		private String host;

		@Min(1024)
		@Max(65535)
		@NotNull
		private Integer port;

		public ReleaseInstance() {
			super();
		}

		public ReleaseInstance(String host, Integer port) {
			super();
			this.host = host;
			this.port = port;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			if (!StringUtils.isEmpty(host) && !"NULL".equalsIgnoreCase(host)) {
				this.host = host;
			}
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			if (port <= 0 || port > 65535) {
				throw new IllegalArgumentException("Illegal ports are only allowed to be 0 ~ 65535");
			}
			this.port = port;
		}

		@Override
		public String toString() {
			return getHost() + ":" + getPort();
		}

		public void validation() {
			Assert.notNull(getHost(), "`host` is not allowed to be null.");
			Assert.notNull(getPort(), "`port` is not allowed to be null.");
			HostAndPort.fromString(toString());
		}

		public static ReleaseInstance of(String hostPortString) {
			HostAndPort hap = HostAndPort.fromString(hostPortString);
			return new ReleaseInstance(hap.getHostText(), hap.getPort());
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