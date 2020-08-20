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
package com.wl4g.devops.scm.client.config;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.isTrue;
import static com.wl4g.components.common.lang.Assert2.isTrueOf;
import static com.wl4g.components.common.lang.Assert2.notEmptyOf;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.scm.common.config.SCMConstants.URI_S_BASE;
import static com.wl4g.devops.scm.common.config.SCMConstants.URI_S_SOURCE_WATCH;
import static java.util.Objects.isNull;

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
import com.wl4g.devops.scm.common.config.BaseScmProperties;

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
public class ScmClientProperties<T extends ScmClientProperties<?>> extends BaseScmProperties {

	private static final long serialVersionUID = -2133451846066162424L;

	/** SCM client application cluster service name mark. */
	private String clusterName = DEF_CLUSTER_NAME;

	/**
	 * SCM client application/service unique identification. (Default is the
	 * system processId)
	 */
	private String serviceId = DEF_SERVICEID;

	/** Connect to SCM server based URI. */
	private String baseUri = DEF_BASEURI;

	/**
	 * SCM client register network interface configuration.
	 */
	private InetProperties inet = DEF_INET;

	/**
	 * Watching timeout on waiting to read data from the SCM Server.
	 */
	private long watchReadTimeout = DEF_WATCH_R_TIMEOUT_MS;

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
	private int asyncEventThreads = DEF_ASYNC_EVENT_THREADS;

	/**
	 * Refresh name-space(configuration filename)</br>
	 * SCM server publishing must be consistent with this configuration or the
	 * refresh configuration will fail.(Accurate matching)
	 */
	private List<String> profiles = new ArrayList<>(2);

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
	private transient String fetchUri;

	/**
	 * Default constructor of {@link ScmClientProperties}, Not recommended!!!
	 */
	public ScmClientProperties() {
		super();
	}

	public ScmClientProperties(String clusterName, String baseUri, InetProperties inet, List<String> namespaces) {
		this(clusterName, DEF_SERVICEID, baseUri, inet, DEF_WATCH_R_TIMEOUT_MS, DEF_LONGPOLLING_MIN_DELAY_MS,
				DEF_LONGPOLLING_MAX_DELAY_MS, DEF_SAFE_REFRESH_DELAY_MS, DEF_RETRY_REPORTING_MIN_DELAY_MS,
				DEF_RETRY_REPORTING_MAX_DELAY_MS, -1, DEF_ASYNC_EVENT_THREADS, namespaces, null, null, null);
	}

	/**
	 * Construct for {@link ScmClientProperties} </br>
	 * 
	 * The parameter: {@link InetHolder} is ignored (only to satisfy the Lombok
	 * syntax sugar)
	 * 
	 * @param clusterName
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
	 * @param safeRefreshProtectDelay
	 *            Frequency interval protection mechanism to control refresh
	 *            failure too fast (ms).
	 * @param namespaces
	 *            Refresh name-space(configuration filename), SCM server
	 *            publishing must be consistent with this configuration or the
	 *            refresh configuration will fail.(Accurate matching)
	 * @param headers
	 *            Additional headers used to create the client request.
	 * @param ignore0
	 *            [Deprecated] only to satisfy the Lombok syntax sugar
	 */
	public ScmClientProperties(@Nonnull String clusterName, @NotBlank String serviceId, @NotBlank String baseUri,
			@NotNull InetProperties inet, long watchReadTimeout, long longPollingMinDelay, long longPollingMaxDelay,
			long safeRefreshProtectDelay, long retryReportingMinDelay, long retryReportingMaxDelay,
			long retryReportingFastFailThreshold, int asyncEventThreads, @Nonnull List<String> namespaces,
			@Nullable Map<String, String> headers, @Deprecated InetHolder ignore0, @Deprecated String ignore1) {
		hasTextOf(clusterName, "clusterName");
		hasTextOf(serviceId, "serviceId");
		hasTextOf(baseUri, "baseUri");
		notNullOf(inet, "inetProperties");
		isTrueOf(watchReadTimeout > 0, "fetchReadTimeout>0");
		isTrueOf(longPollingMinDelay > 0, "longPollingMinDelay>0");
		isTrue(longPollingMaxDelay > longPollingMinDelay, "longPollingMaxDelay>longPollingMinDelay(%s)", longPollingMinDelay);
		isTrueOf(safeRefreshProtectDelay > 0, "safeRefreshProtectDelay>0");
		isTrueOf(retryReportingMinDelay > 0, "retryReportingMinDelay>0");
		isTrue(retryReportingMaxDelay > retryReportingMinDelay, "retryReportingMaxDelay>retryReportingMinDelay(%s)",
				retryReportingMinDelay);
		isTrueOf(asyncEventThreads > 0, "asyncEventThreads>0");
		notEmptyOf(namespaces, "namespaces");
		this.clusterName = clusterName;
		this.serviceId = serviceId;
		this.baseUri = baseUri;
		this.inet = inet;
		this.watchReadTimeout = watchReadTimeout;
		this.longPollingMinDelay = longPollingMinDelay;
		this.longPollingMaxDelay = longPollingMaxDelay;
		this.safeRefreshRateDelay = safeRefreshProtectDelay;
		this.retryReportingMinDelay = retryReportingMinDelay;
		this.retryReportingMaxDelay = retryReportingMaxDelay;
		this.retryReportingFastFailThreshold = retryReportingFastFailThreshold;
		this.asyncEventThreads = asyncEventThreads;
		this.profiles = namespaces;
		this.headers = headers;
	}

	public T withClusterName(String clusterName) {
		this.clusterName = clusterName;
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

	public T withWatchReadTimeout(long watchReadTimeout) {
		this.watchReadTimeout = watchReadTimeout;
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

	public T withSafeRefreshProtectDelay(long safeRefreshProtectDelay) {
		this.safeRefreshRateDelay = safeRefreshProtectDelay;
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

	public T withAsyncEventThreads(int asyncEventThreads) {
		this.asyncEventThreads = asyncEventThreads;
		return (T) this;
	}

	public T withNamespaces(List<String> namespaces) {
		this.profiles = namespaces;
		return (T) this;
	}

	public T withHeaders(Map<String, String> headers) {
		this.headers = headers;
		return (T) this;
	}

	// --- Function's. ---

	public String getWatchUri() {
		synchronized (this) {
			if (isNull(fetchUri)) {
				this.fetchUri = getBaseUri().concat(URI_S_BASE).concat("/").concat(URI_S_SOURCE_WATCH);
			}
		}
		return fetchUri;
	}

	public HostInfo getAvailableHostInfo() {
		synchronized (this) {
			if (isNull(inetHolder)) {
				this.inetHolder = new InetHolder(getInet());
			}
		}
		return inetHolder.getFirstNonLoopbackHostInfo();
	}

	// --- Default and constant's. ---

	/**
	 * Prefix for SCM configuration properties.
	 */
	public final static String AUTHORIZATION = "authorization";

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

	/**
	 * Default Fetch timeout on waiting to read data from the SCM Server.
	 */
	public final static int DEF_WATCH_R_TIMEOUT_MS = 8 * 1000;

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
	public final static int DEF_ASYNC_EVENT_THREADS = 1;

}