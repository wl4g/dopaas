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
package com.wl4g.devops.scm.client.config;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.isTrue;
import static com.wl4g.components.common.lang.Assert2.isTrueOf;
import static com.wl4g.components.common.lang.Assert2.notEmptyOf;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.scm.common.SCMConstants.URI_S_BASE;
import static com.wl4g.devops.scm.common.SCMConstants.URI_S_SOURCE_WATCH;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.components.common.lang.SystemUtils2;
import com.wl4g.devops.common.utils.InetHolder;
import com.wl4g.devops.common.utils.InetHolder.HostInfo;
import com.wl4g.devops.common.utils.InetHolder.InetProperties;
import com.wl4g.devops.scm.common.BaseScmProperties;
import com.wl4g.devops.scm.common.model.AbstractConfigInfo.ConfigProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Wither;

/**
 * SCM client properties.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月3日
 * @since
 */
@Getter
@Setter
@SuppressWarnings("unchecked")
@Wither
public class ScmClientProperties<T extends ScmClientProperties<?>> extends BaseScmProperties {

	private static final long serialVersionUID = -2133451846066162424L;

	/** Connect to SCM server based URI. */
	private String baseUri = DEF_BASEURI;

	/** SCM client application cluster service name mark. */
	private String zone = DEF_ZONE_NAME;

	/** SCM client application cluster service name mark. */
	private String cluster = DEF_CLUSTER_NAME;

	/**
	 * SCM client application/service unique identification. (Default is the
	 * system processId)
	 */
	private String serviceId = DEF_SERVICEID;

	/**
	 * Refresh name-space(configuration filename)</br>
	 * SCM server publishing must be consistent with this configuration or the
	 * refresh configuration will fail.(Accurate matching)
	 */
	private List<ConfigProfile> profiles = new ArrayList<>(2);

	/**
	 * SCM client register network interface configuration.
	 */
	private InetProperties inet = DEF_INET;

	/** Minimum waiting time for long polling failure. */
	private long longPollingMinDelay = DEF_LONGPOLLING_MIN_DELAY_MS;

	/** Maximum waiting time for long polling failure. */
	private long longPollingMaxDelay = DEF_LONGPOLLING_MAX_DELAY_MS;

	/**
	 * Frequency interval protection mechanism to control refresh failure too
	 * fast (ms).
	 */
	private long safeRefreshRateDelay = DEF_SAFE_REFRESH_DELAY_MS;

	/**
	 * Checkpoing reporting retry random min interval (ms).
	 */
	private long retryReportingMinDelay = DEF_RETRY_REPORTING_MIN_DELAY_MS;

	/**
	 * Checkpoing reporting retry random max interval (ms).
	 */
	private long retryReportingMaxDelay = DEF_RETRY_REPORTING_MAX_DELAY_MS;

	/**
	 * Retry failure reporting exceed threshold fast-fail. </br>
	 * When it is less than 0, it means not to give up
	 */
	private long retryReportingFastFailThreshold;

	/**
	 * Invoking async event threads pool maximum.
	 */
	private int eventThreads = DEF_EVENT_THREADS;

	/** SCM client console prompt. */
	private String consolePrompt;

	/**
	 * Additional headers used to create the client request.
	 */
	private Map<String, String> headers = new HashMap<>(4);

	// --- Temporary's. ---

	@Getter(lombok.AccessLevel.NONE)
	@Setter(lombok.AccessLevel.NONE)
	@Wither(lombok.AccessLevel.NONE)
	private transient InetHolder inetHolder;

	@Getter(lombok.AccessLevel.NONE)
	@Setter(lombok.AccessLevel.NONE)
	@Wither(lombok.AccessLevel.NONE)
	private transient String watchUri;

	/**
	 * Default constructor of {@link ScmClientProperties}, Not recommended!!!
	 */
	public ScmClientProperties() {
		super();
	}

	public ScmClientProperties(@NotBlank String baseUri, @Nullable String zone, @NotBlank String cluster,
			@NotBlank List<ConfigProfile> profiles, @NotNull InetProperties inet) {
		this(baseUri, zone, cluster, DEF_SERVICEID, profiles, inet, DEF_LONGPOLLING_MIN_DELAY_MS, DEF_LONGPOLLING_MAX_DELAY_MS,
				DEF_SAFE_REFRESH_DELAY_MS, DEF_RETRY_REPORTING_MIN_DELAY_MS, DEF_RETRY_REPORTING_MAX_DELAY_MS, -1,
				DEF_EVENT_THREADS, null, null, null, null);
	}

	/**
	 * Construct for {@link ScmClientProperties} </br>
	 * 
	 * The parameter: {@link InetHolder} is ignored (only to satisfy the Lombok
	 * syntax sugar)
	 * 
	 * @param cluster
	 *            SCM client application cluster service name mark.
	 * @param baseUri
	 *            Connect to SCM server based URI.
	 * @param inet
	 *            SCM client register network interface configuration.
	 * @param watchReadTimeout
	 *            Fetch timeout on waiting to read data from the SCM Server.
	 * @param longPollingMinDelay
	 *            Minimum waiting time for long polling failure, Prevent
	 *            avalanche.
	 * @param longPollingMaxDelay
	 *            Maximum waiting time for long polling failure, Prevent
	 *            avalanche.
	 * @param safeRefreshRateDelay
	 *            Frequency interval protection mechanism to control refresh
	 *            failure too fast (ms).
	 * @param profiles
	 *            Refresh name-space(configuration filename), SCM server
	 *            publishing must be consistent with this configuration or the
	 *            refresh configuration will fail.(Accurate matching)
	 * @param headers
	 *            Additional headers used to create the client request.
	 * @param ignore0
	 *            [Deprecated] only to satisfy the Lombok syntax sugar
	 */
	public ScmClientProperties(@NotBlank String baseUri, @Nullable String zone, @NotBlank String cluster,
			@NotBlank String serviceId, @Nonnull List<ConfigProfile> profiles, @NotNull InetProperties inet,
			long longPollingMinDelay, long longPollingMaxDelay, long safeRefreshRateDelay, long retryReportingMinDelay,
			long retryReportingMaxDelay, long retryReportingFastFailThreshold, int eventThreads, @Nullable String consolePrompt,
			@Nullable Map<String, String> headers, @Deprecated InetHolder ignore0, @Deprecated String ignore1) {
		this.baseUri = baseUri;
		this.zone = zone;
		this.cluster = cluster;
		this.serviceId = serviceId;
		this.profiles = profiles;
		this.inet = inet;
		this.longPollingMinDelay = longPollingMinDelay;
		this.longPollingMaxDelay = longPollingMaxDelay;
		this.safeRefreshRateDelay = safeRefreshRateDelay;
		this.retryReportingMinDelay = retryReportingMinDelay;
		this.retryReportingMaxDelay = retryReportingMaxDelay;
		this.retryReportingFastFailThreshold = retryReportingFastFailThreshold;
		this.eventThreads = eventThreads;
		this.profiles = profiles;
		this.consolePrompt = consolePrompt;
		this.headers = headers;
	}

	public T withCluster(String cluster) {
		this.cluster = cluster;
		return (T) this;
	}

	public T withServiceId(String serviceId) {
		this.serviceId = serviceId;
		return (T) this;
	}

	public T withBaseUri(String baseUri) {
		this.baseUri = baseUri;
		return (T) this;
	}

	public T withInet(InetProperties inet) {
		this.inet = inet;
		return (T) this;
	}

	public T withLongPollTimeout(long longPollingTimeout) {
		setLongPollTimeout(longPollingTimeout);
		return (T) this;
	}

	public T withConnectTimeout(long connectTimeout) {
		setConnectTimeout(connectTimeout);
		return (T) this;
	}

	public T withMaxResponseSize(long maxResponseSize) {
		setMaxResponseSize(maxResponseSize);
		return (T) this;
	}

	public T withLongPollingMinDelay(long longPollingMinDelay) {
		this.longPollingMinDelay = longPollingMinDelay;
		return (T) this;
	}

	public T withLongPollingMaxDelay(long longPollingMaxDelay) {
		this.longPollingMaxDelay = longPollingMaxDelay;
		return (T) this;
	}

	public T withsafeRefreshRateDelay(long safeRefreshRateDelay) {
		this.safeRefreshRateDelay = safeRefreshRateDelay;
		return (T) this;
	}

	public T withRetryReportingMinDelay(long retryReportingMinDelay) {
		this.retryReportingMinDelay = retryReportingMinDelay;
		return (T) this;
	}

	public T withRetryReportingMaxDelay(long retryReportingMaxDelay) {
		this.retryReportingMaxDelay = retryReportingMaxDelay;
		return (T) this;
	}

	public T withRetryReportingFastFailThreshold(long retryReportingFastFailThreshold) {
		this.retryReportingFastFailThreshold = retryReportingFastFailThreshold;
		return (T) this;
	}

	public T witheventThreads(int eventThreads) {
		this.eventThreads = eventThreads;
		return (T) this;
	}

	public T withProfiles(List<ConfigProfile> profiles) {
		this.profiles = profiles;
		return (T) this;
	}

	public T withHeaders(Map<String, String> headers) {
		this.headers = headers;
		return (T) this;
	}

	// --- Function's. ---

	public String getWatchUri() {
		synchronized (this) {
			if (isNull(watchUri)) {
				this.watchUri = getBaseUri().concat(URI_S_BASE).concat("/").concat(URI_S_SOURCE_WATCH);
			}
		}
		return watchUri;
	}

	public HostInfo getAvailableHostInfo() {
		synchronized (this) {
			if (isNull(inetHolder)) {
				this.inetHolder = new InetHolder(getInet());
			}
		}
		return inetHolder.getFirstNonLoopbackHostInfo();
	}

	/**
	 * Check validation configuration items.
	 */
	protected void validate() {
		// Apply defaults.
		setConsolePrompt(isBlank(getConsolePrompt()) ? getCluster() : getConsolePrompt());

		hasTextOf(baseUri, "baseUri");
		// hasTextOf(zone, "zone");
		// hasTextOf(cluster, "cluster");
		// hasTextOf(serviceId, "serviceId");
		notNullOf(inet, "inetProperties");
		notEmptyOf(profiles, "profiles");

		isTrueOf(longPollingMinDelay > 0, "longPollingMinDelay>0");
		isTrue(longPollingMaxDelay > longPollingMinDelay, "longPollingMaxDelay>longPollingMinDelay(%s)", longPollingMinDelay);
		isTrueOf(safeRefreshRateDelay > 0, "safeRefreshRateDelay>0");
		isTrueOf(retryReportingMinDelay > 0, "retryReportingMinDelay>0");
		isTrue(retryReportingMaxDelay > retryReportingMinDelay, "retryReportingMaxDelay>retryReportingMinDelay(%s)",
				retryReportingMinDelay);
		isTrueOf(eventThreads > 0, "eventThreads>0");
		// hasTextOf(consolePrompt, "consolePrompt");
	}

	// --- Default and constant's. ---

	/**
	 * Prefix for SCM configuration properties.
	 */
	public final static String AUTHORIZATION = "authorization";

	/** Default SCM client data center of application cluster. */
	public final static String DEF_ZONE_NAME = "none";

	/** Default SCM client application cluster service name mark. */
	public final static String DEF_CLUSTER_NAME = "defaultScmClient";

	/**
	 * SCM client application/service unique identification. (Default is the
	 * system processId)
	 */
	public final static String DEF_SERVICEID = SystemUtils2.LOCAL_PROCESS_ID;

	/** Default Connect to SCM server based URI. */
	public final static String DEF_BASEURI = "http://localhost:14043/scm-server";

	/**
	 * Default SCM client register network interface configuration.
	 */
	public final static InetProperties DEF_INET = new InetProperties();

	/** Default Minimum waiting time for long polling failure. */
	public final static long DEF_LONGPOLLING_MIN_DELAY_MS = 2 * 1000L;

	/** Default Maximum waiting time for long polling failure. */
	public final static long DEF_LONGPOLLING_MAX_DELAY_MS = 15 * 1000L;

	/**
	 * Default Frequency interval protection mechanism to control refresh
	 * failure too fast (ms).
	 */
	public final static long DEF_SAFE_REFRESH_DELAY_MS = 10_000L;

	/**
	 * Default Checkpoing reporting retry random min interval (ms).
	 */
	public final static long DEF_RETRY_REPORTING_MIN_DELAY_MS = 300L;

	/**
	 * Default Checkpoing reporting retry random max interval (ms).
	 */
	public final static long DEF_RETRY_REPORTING_MAX_DELAY_MS = 3000L;

	/**
	 * Default invoking async event threads pool maximum.
	 */
	public final static int DEF_EVENT_THREADS = 2;

}