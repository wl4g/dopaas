package com.wl4g.devops.common.bean.scm.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.google.common.net.HostAndPort;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;

public class BaseModel implements Serializable {
	final private static long serialVersionUID = -299157686801700764L;

	@NotNull
	private String application; // Application name(Required)
	@NotNull
	private String profile; // Environment(Required)
	private ReleaseMeta releaseMeta = new ReleaseMeta(); // Version-Release

	public BaseModel() {
		super();
	}

	public BaseModel(String application, String profile) {
		this(application, profile, null);
	}

	public BaseModel(String application, String profile, ReleaseMeta releaseMeta) {
		super();
		this.setApplication(application);
		this.setProfile(profile);
		this.setReleaseMeta(releaseMeta);
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		if (!StringUtils.isEmpty(application) && !"NULL".equalsIgnoreCase(application)) {
			this.application = application;
		}
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		if (!StringUtils.isEmpty(profile) && !"NULL".equalsIgnoreCase(profile)) {
			this.profile = profile;
		}
	}

	public ReleaseMeta getReleaseMeta() {
		return releaseMeta;
	}

	public void setReleaseMeta(ReleaseMeta releaseMeta) {
		if (releaseMeta != null) {
			this.releaseMeta = releaseMeta;
		}
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

	public void validation(boolean validVersion, boolean validReleaseId) {
		Assert.notNull(getApplication(), "`application` is not allowed to be null.");
		Assert.notNull(getProfile(), "`profile` is not allowed to be null.");
		this.getReleaseMeta().validation(validVersion, validReleaseId);
	}

	public static class ReleaseInstance implements Serializable {
		private static final long serialVersionUID = -4826329780329773259L;

		private String host;
		private int port;

		public ReleaseInstance() {
			super();
		}

		public ReleaseInstance(String host, int port) {
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

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			if (port <= 0 || port > 65535) {
				throw new IllegalArgumentException("Illegal ports are only allowed to be 0 ~ 65535");
			}
			this.port = port;
		}

		@Override
		public String toString() {
			return this.getHost() + ":" + this.getPort();
		}

		public void validation() {
			Assert.notNull(getHost(), "`host` is not allowed to be null.");
			Assert.notNull(getPort(), "`port` is not allowed to be null.");
			HostAndPort.fromString(this.toString());
		}

		public static ReleaseInstance of(String hostPortString) {
			HostAndPort hap = HostAndPort.fromString(hostPortString);
			return new ReleaseInstance(hap.getHostText(), hap.getPort());
		}

	}

	public static class ReleaseMeta implements Serializable {
		private static final long serialVersionUID = -4826329110329773259L;

		private String version; // Release version(Required).
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
			return this.getReleaseId() + "@" + this.getVersion();
		}

		@Override
		public String toString() {
			return this.asText();
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
