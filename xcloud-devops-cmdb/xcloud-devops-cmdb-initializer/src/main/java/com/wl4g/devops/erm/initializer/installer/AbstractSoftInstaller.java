/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.cmdb.initializer.installer;

import static com.wl4g.component.common.lang.Assert2.hasTextOf;
import static com.wl4g.component.common.lang.Assert2.isTrue;
import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static com.wl4g.component.common.lang.TypeConverts.parseIntOrNull;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.split;

import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.component.common.task.GenericTaskRunner;
import com.wl4g.component.common.task.RunnerProperties;
import com.wl4g.devops.cmdb.initializer.exception.InvalidPkgVersionException;
import com.wl4g.devops.cmdb.initializer.exception.UnsupportedInstallPkgVersionException;

import io.netty.util.internal.UnstableApi;

/**
 * {@link RemovableSoftInstaller}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-07-24
 * @sine v1.0.0
 * @see
 */
public abstract class AbstractSoftInstaller<C extends InstallerConfiguration> extends GenericTaskRunner<RunnerProperties>
		implements Installer {

	protected final SmartLogger log = getLogger(getClass());

	protected final C config;

	public AbstractSoftInstaller(C config) {
		notNullOf(config, "installConfig");
		this.config = config;
	}

	@Override
	public void run() {

		// Step1: Installation pre processing.
		preHandleInstallation();

		// Step2: Check installing package version.
		checkInstallPackageVersions();

		// Step3: Load package from repository.

		// Step4: Unpackage and installation pre processing.

		// Step5: Generate multi configuration.

		// Step6: Distributes to nodes and installations.

		// Step7: Startup all node instance.

		// Step8: Collect all instance run configuration info.

	}

	/**
	 * Installation pre processing.
	 */
	protected abstract void preHandleInstallation();

	/**
	 * Check installation package versions.
	 */
	protected void checkInstallPackageVersions() {
		VersionRange vr = supportVersionRange();
		notNullOf(vr, "supportedVersionRange");

		isTrue(vr.checkOutofVersionRange(config.getVersion()), UnsupportedInstallPkgVersionException.class,
				"Unsupported install package version: %s, must be between %s ~ %s");
	}

	/**
	 * Support version range
	 * 
	 * @return
	 */
	protected VersionRange supportVersionRange() {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@link VersionRange}
	 * 
	 * @see
	 */
	protected static class VersionRange {

		final private SoftVersion min;
		final private SoftVersion max;

		public VersionRange(SoftVersion min, SoftVersion max) {
			notNullOf(min, "min");
			notNullOf(max, "max");
			this.min = min;
			this.max = max;
		}

		public SoftVersion getMin() {
			return min;
		}

		public SoftVersion getMax() {
			return max;
		}

		/**
		 * Check outof version ranges.
		 * 
		 * @param v
		 * @return
		 */
		public boolean checkOutofVersionRange(SoftVersion v) {
			int targetV = v.toVersionCombineNumber();
			return min.toVersionCombineNumber() < targetV && max.toVersionCombineNumber() > targetV;
		}

	}

	/**
	 * {@link SoftVersion}
	 * 
	 * @see
	 */
	public static class SoftVersion implements Comparable<SoftVersion> {

		/** Major version */
		final private Integer major;

		/** Minor version */
		final private Integer minor;

		/** Revision version */
		final private Integer revision;

		/** Dependencies libras versions */
		final private Integer[] depends;

		public SoftVersion(int major, int minor, int revision) {
			this(major, minor, revision, null);
		}

		public SoftVersion(int major, int minor, int revision, Integer[] depends) {
			notNullOf(major, "majorVersion");
			notNullOf(minor, "minorVersion");
			notNullOf(revision, "revisionVersion");
			this.major = major;
			this.minor = minor;
			this.revision = revision;
			this.depends = depends;
		}

		public Integer getMajor() {
			return major;
		}

		public Integer getMinor() {
			return minor;
		}

		public Integer getRevision() {
			return revision;
		}

		public Integer[] getDepends() {
			return depends;
		}

		@Override
		public int compareTo(SoftVersion o) {
			int thatV = toVersionCombineNumber();
			int targetV = o.toVersionCombineNumber();
			return thatV - targetV;
		}

		public int toVersionCombineNumber() {
			return parseIntOrNull(toString().replace(".", ""));
		}

		@Override
		public String toString() {
			return valueOf(getMajor()).concat(".").concat(valueOf(getMinor()).concat(".").concat(valueOf(getRevision())));
		}

		/**
		 * Parsing {@link SoftVersion} from string.
		 * 
		 * @param version
		 * @return
		 */
		@UnstableApi
		public static SoftVersion parse(String version) {
			hasTextOf(version, InvalidPkgVersionException.class, "version");

			// Remove 'V' prefix. (if necessary)
			if (version.toUpperCase().startsWith("V")) {
				version = version.substring(1);
			}

			// Remove '-'/'_' prefix. (if necessary)
			int index1 = version.lastIndexOf("_");
			if (index1 > 0) {
				version = version.substring(index1);
			}
			int index2 = version.lastIndexOf("-");
			if (index2 > 0) {
				version = version.substring(index2);
			}

			String[] parts = split(version, ".");
			if (parts.length < 3) {
				throw new InvalidPkgVersionException(format("Invalid version: %s, e.g: 1.0.2", version));
			}

			// Stardand: major/minor/revision numbers. (e.g: redis-6.0.6.tar.gz)
			int major = parseIntOrNull(parts[0]);
			int minor = parseIntOrNull(parts[1]);
			int revision = parseIntOrNull(parts[2]);

			// Special: e.g: kafka_2.10-0.10.2.0/kafka-0.10.2.0_2.10
			// ...
			// int depends = parseIntOrNull(parts[3]);

			return new SoftVersion(major, minor, revision, null);
		}

	}

}