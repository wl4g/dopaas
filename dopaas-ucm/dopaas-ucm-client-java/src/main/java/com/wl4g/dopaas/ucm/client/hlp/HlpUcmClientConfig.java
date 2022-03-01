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
package com.wl4g.dopaas.ucm.client.hlp;

import static com.wl4g.dopaas.ucm.common.UCMConstants.URI_S_BASE;
import static com.wl4g.dopaas.ucm.common.UCMConstants.URI_S_SOURCE_WATCH;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static java.util.Objects.isNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.wl4g.dopaas.common.bean.ucm.model.AbstractConfigInfo.ConfigProfile;
import com.wl4g.dopaas.common.utils.InetHolder;
import com.wl4g.dopaas.common.utils.InetHolder.HostInfo;
import com.wl4g.dopaas.common.utils.InetHolder.InetProperties;
import com.wl4g.dopaas.ucm.client.internal.AbstractUcmClientConfig;
import com.wl4g.infra.common.lang.SystemUtils2;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Wither;

/**
 * HLP UCM client properties.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月3日
 * @since
 */
@Getter
@Setter
public class HlpUcmClientConfig extends AbstractUcmClientConfig<HlpUcmClientConfig> {
    private static final long serialVersionUID = -2133451846066162424L;

    /** Connect to UCM server based URI. */
    private String baseUri = DEF_BASEURI;

    /** Minimum waiting time for long polling failure. */
    private long longPollingMinDelay = DEF_LONGPOLLING_MIN_DELAY_MS;

    /** Maximum waiting time for long polling failure. */
    private long longPollingMaxDelay = DEF_LONGPOLLING_MAX_DELAY_MS;

    /**
     * Watching timeout on waiting to read data from the UCM Server.
     */
    private long longPollTimeout = DEF_WATCH_R_TIMEOUT_MS;

    /** Connect timeout */
    private long connectTimeout = DEF_CONN_TIMEOUT_MS;

    /** Max response size */
    private long maxResponseSize = 65535;

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
     * Default constructor of {@link HlpUcmClientConfig}, Not recommended!!!
     */
    public HlpUcmClientConfig() {
        super();
    }

    public HlpUcmClientConfig(@NotBlank String zone, @NotBlank String cluster, @NotBlank String serviceId,
            @NotEmpty List<ConfigProfile> profiles, @NotNull InetProperties inet, @NotBlank String baseUri,
            long longPollingMinDelay, long longPollingMaxDelay, long safeRefreshRateDelay, long retryReportingMinDelay,
            long retryReportingMaxDelay, long retryReportingFastFailThreshold, int eventThreads, @Nullable String consolePrompt,
            @Nullable Map<String, String> headers) {
        super(zone, cluster, serviceId, profiles, inet, eventThreads, safeRefreshRateDelay, retryReportingMinDelay,
                retryReportingMaxDelay, retryReportingFastFailThreshold, consolePrompt);
        isTrueOf(longPollingMinDelay > 0, "longPollingMinDelay>0");
        isTrue(longPollingMaxDelay > longPollingMinDelay, "longPollingMaxDelay>longPollingMinDelay(%s)", longPollingMinDelay);
        this.baseUri = hasTextOf(baseUri, "baseUri");
        this.longPollingMinDelay = longPollingMinDelay;
        this.longPollingMaxDelay = longPollingMaxDelay;
        this.headers = headers;
    }

    public HlpUcmClientConfig withInetHolder(InetHolder inetHolder) {
        this.inetHolder = inetHolder;
        return this;
    }

    public HlpUcmClientConfig withBaseUri(String baseUri) {
        this.baseUri = baseUri;
        return this;
    }

    public HlpUcmClientConfig withLongPollingMinDelay(long longPollingMinDelay) {
        this.longPollingMinDelay = longPollingMinDelay;
        return this;
    }

    public HlpUcmClientConfig withLongPollingMaxDelay(long longPollingMaxDelay) {
        this.longPollingMaxDelay = longPollingMaxDelay;
        return this;
    }

    public HlpUcmClientConfig withLongPollTimeout(long longPollTimeout) {
        this.longPollTimeout = longPollTimeout;
        return this;
    }

    public HlpUcmClientConfig withConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public HlpUcmClientConfig withMaxResponseSize(long maxResponseSize) {
        this.maxResponseSize = maxResponseSize;
        return this;
    }

    public HlpUcmClientConfig withHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
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

    public InetHolder getInetHolder() {
        return inetHolder;
    }

    /**
     * Prefix for UCM configuration properties.
     */
    public final static String AUTHORIZATION = "authorization";

    /** Default UCM client data center of application cluster. */
    public final static String DEF_ZONE_NAME = "none";

    /** Default UCM client application cluster service name mark. */
    public final static String DEF_CLUSTER_NAME = "defaultUcmClient";

    /**
     * UCM client application/service unique identification. (Default is the
     * system processId)
     */
    public final static String DEF_SERVICEID = SystemUtils2.LOCAL_PROCESS_ID;

    /** Default Connect to UCM server based URI. */
    public final static String DEF_BASEURI = "http://localhost:17030/ucm-server";

    /**
     * Default UCM client register network interface configuration.
     */
    public final static InetProperties DEF_INET = new InetProperties();

    /** Default Minimum waiting time for long polling failure. */
    public final static long DEF_LONGPOLLING_MIN_DELAY_MS = 2 * 1000L;

    /** Default Maximum waiting time for long polling failure. */
    public final static long DEF_LONGPOLLING_MAX_DELAY_MS = 15 * 1000L;

}