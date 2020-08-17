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
@Wither
public class ScmClientProperties extends BaseScmProperties {
	private static final long serialVersionUID = -2133451846066162424L;

	/** SCM client application cluster service name mark. */
	private String clusterName = DEF_CLUSTER_NAME;

	/** SCM client application endpoint(port). */
	private int serverPort = DEF_SERVER_PORT;

	/** Connect to SCM server based URI. */
	private String baseUri = DEF_BASEURI;

	/**
	 * SCM client register network interface configuration.
	 */
	private InetProperties inet = DEF_INET;

	/**
	 * Watching timeout on waiting to read data from the SCM Server.
	 */
	private int watchReadTimeout = DEF_WATCH_READ_TIMEOUT_MS;

	/** Minimum waiting time for long polling failure. */
	private long longPollDelay = DEF_LONG_POLL_DELAY_MS;

	/** Maximum waiting time for long polling failure. */
	private long longPollMaxDelay = DEF_LONG_POLL_MAX_DELAY_MS;

	/**
	 * Frequency interval protection mechanism to control refresh failure too
	 * fast (ms).
	 */
	private long refreshProtectIntervalMs = DEF_REFRESH_PROTECT_INTERVAL_MS;

	/**
	 * Refresh name-space(configuration filename)</br>
	 * SCM server publishing must be consistent with this configuration or the
	 * refresh configuration will fail.(Accurate matching)
	 */
	private List<String> namespaces = new ArrayList<>(2);

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

	public ScmClientProperties(String clusterName, int serverPort, String baseUri, InetProperties inet, List<String> namespaces) {
		this(clusterName, serverPort, baseUri, inet, DEF_WATCH_READ_TIMEOUT_MS, DEF_LONG_POLL_DELAY_MS,
				DEF_LONG_POLL_MAX_DELAY_MS, DEF_REFRESH_PROTECT_INTERVAL_MS, namespaces, null, null, null);
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
	 * @param fetchReadTimeout
	 *            Fetch timeout on waiting to read data from the SCM Server.
	 * @param longPollDelay
	 *            Minimum waiting time for long polling failure, Prevent
	 *            avalanche.
	 * @param longPollMaxDelay
	 *            Maximum waiting time for long polling failure, Prevent
	 *            avalanche.
	 * @param refreshProtectIntervalMs
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
	public ScmClientProperties(String clusterName, int serverPort, String baseUri, InetProperties inet, int fetchReadTimeout,
			long longPollDelay, long longPollMaxDelay, long refreshProtectIntervalMs, List<String> namespaces,
			Map<String, String> headers, @Deprecated InetHolder ignore0, @Deprecated String ignore1) {
		hasTextOf(clusterName, "clusterName");
		isTrue(serverPort > 1024, "serverPort>1024");
		hasTextOf(baseUri, "baseUri");
		notNullOf(inet, "inet");
		isTrueOf(fetchReadTimeout > 0, "fetchReadTimeout>0");
		isTrueOf(longPollDelay > 0, "longPollDelay>0");
		isTrue(longPollMaxDelay > longPollDelay, "longPollMaxDelay>longPollDelay(%s)", longPollDelay);
		isTrueOf(refreshProtectIntervalMs > 0, "refreshProtectIntervalMs>0");
		notEmptyOf(namespaces, "namespaces");
		// notNullOf(headers, "headers");
		this.clusterName = clusterName;
		this.serverPort = serverPort;
		this.baseUri = baseUri;
		this.inet = inet;
		this.watchReadTimeout = fetchReadTimeout;
		this.longPollDelay = longPollDelay;
		this.longPollMaxDelay = longPollMaxDelay;
		this.refreshProtectIntervalMs = refreshProtectIntervalMs;
		this.namespaces = namespaces;
		this.headers = headers;
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

	/** Default SCM client application endpoint(port). */
	public final static int DEF_SERVER_PORT = 8080;

	/** Default Connect to SCM server based URI. */
	public final static String DEF_BASEURI = "http://localhost:14043/scm-server";

	/**
	 * Default SCM client register network interface configuration.
	 */
	public final static InetProperties DEF_INET = new InetProperties();

	/**
	 * Default Fetch timeout on waiting to read data from the SCM Server.
	 */
	public final static int DEF_WATCH_READ_TIMEOUT_MS = 8 * 1000;

	/** Default Minimum waiting time for long polling failure. */
	public final static long DEF_LONG_POLL_DELAY_MS = 2 * 1000L;

	/** Default Maximum waiting time for long polling failure. */
	public final static long DEF_LONG_POLL_MAX_DELAY_MS = 15 * 1000L;

	/**
	 * Default Frequency interval protection mechanism to control refresh
	 * failure too fast (ms).
	 */
	public final static long DEF_REFRESH_PROTECT_INTERVAL_MS = 10_000L;

}