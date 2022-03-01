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
package com.wl4g.dopaas.ucm.client.internal;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.wl4g.dopaas.common.bean.ucm.model.AbstractConfigInfo.ConfigProfile;
import com.wl4g.dopaas.common.utils.InetHolder;
import com.wl4g.dopaas.common.utils.InetHolder.InetProperties;
import com.wl4g.dopaas.ucm.client.hlp.HlpUcmClientConfig;
import com.wl4g.infra.common.lang.SystemUtils2;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link AbstractUcmClientConfig}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-10
 * @since
 */
@Getter
@Setter
@SuppressWarnings("unchecked")
public abstract class AbstractUcmClientConfig<E extends AbstractUcmClientConfig<?>> implements Serializable {
    private static final long serialVersionUID = -242805976296191411L;

    /** UCM client application cluster service name mark. */
    private String zone = DEF_ZONE_NAME;

    /** UCM client application cluster service name mark. */
    private String cluster = DEF_CLUSTER_NAME;

    /**
     * UCM client application/service unique identification. (Default is the
     * system processId)
     */
    private String serviceId = DEF_SERVICEID;

    /**
     * Refresh name-space(configuration filename)</br>
     * UCM server publishing must be consistent with this configuration or the
     * refresh configuration will fail.(Accurate matching)
     */
    private List<ConfigProfile> profiles = new ArrayList<>(2);

    /**
     * UCM client register network interface configuration.
     */
    private InetProperties inet = DEF_INET;

    /**
     * Frequency interval protection mechanism to control refresh failure too
     * fast (ms).
     */
    private long safeRefreshRateDelay = DEF_SAFE_REFRESH_DELAY_MS;

    /**
     * Checking reporting retry random min interval (ms).
     */
    private long retryReportingMinDelay = DEF_RETRY_REPORTING_MIN_DELAY_MS;

    /**
     * Checking reporting retry random max interval (ms).
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

    /** UCM client console prompt. */
    private String consolePrompt;

    public AbstractUcmClientConfig() {
    }

    /**
     * Construct for {@link HlpUcmClientConfig} </br>
     * 
     * The parameter: {@link InetHolder} is ignored (only to satisfy the Lombok
     * syntax sugar)
     * 
     * @param zone
     *            UCM client application cluster of zone.
     * @param cluster
     *            UCM client application cluster service name mark.
     * @param serviceId
     *            UCM client application serviceId identifier.
     * @param profiles
     *            Refresh name-space(configuration filename), UCM server
     *            publishing must be consistent with this configuration or the
     *            refresh configuration will fail.(Accurate matching)
     * @param inet
     *            UCM client register network interface configuration.
     * @param eventThreads
     *            Receive change event bus threads.
     */
    public AbstractUcmClientConfig(@NotBlank String zone, @NotBlank String cluster, @NotBlank String serviceId,
            @NotEmpty List<ConfigProfile> profiles, @NotNull InetProperties inet, int eventThreads, long safeRefreshRateDelay,
            long retryReportingMinDelay, long retryReportingMaxDelay, long retryReportingFastFailThreshold,
            @Nullable String consolePrompt) {
        isTrueOf(safeRefreshRateDelay > 0, "safeRefreshRateDelay>0");
        isTrueOf(retryReportingMinDelay > 0, "retryReportingMinDelay>0");
        isTrue(retryReportingMaxDelay > retryReportingMinDelay, "retryReportingMaxDelay>retryReportingMinDelay(%s)",
                retryReportingMinDelay);
        isTrueOf(eventThreads > 0, "eventThreads");
        this.zone = hasTextOf(zone, "zone");
        this.cluster = hasTextOf(cluster, "cluster");
        this.serviceId = hasTextOf(serviceId, "serviceId");
        this.profiles = notEmptyOf(profiles, "profiles");
        this.inet = notNullOf(inet, "inet");
        this.safeRefreshRateDelay = safeRefreshRateDelay;
        this.retryReportingMinDelay = retryReportingMinDelay;
        this.retryReportingMaxDelay = retryReportingMaxDelay;
        this.retryReportingFastFailThreshold = retryReportingFastFailThreshold;
        this.eventThreads = eventThreads;
        this.consolePrompt = isBlank(consolePrompt) ? getCluster() : consolePrompt;
    }

    public E withZone(String zone) {
        this.zone = zone;
        return (E) this;
    }

    public E withCluster(String cluster) {
        this.cluster = cluster;
        return (E) this;
    }

    public E withServiceId(String serviceId) {
        this.serviceId = serviceId;
        return (E) this;
    }

    public E withProfiles(List<ConfigProfile> profiles) {
        this.profiles = profiles;
        return (E) this;
    }

    public E withInet(InetProperties inet) {
        this.inet = inet;
        return (E) this;
    }

    public E withSafeRefreshRateDelay(long safeRefreshRateDelay) {
        this.safeRefreshRateDelay = safeRefreshRateDelay;
        return (E) this;
    }

    public E withRetryReportingMinDelay(long retryReportingMinDelay) {
        this.retryReportingMinDelay = retryReportingMinDelay;
        return (E) this;
    }

    public E withRetryReportingMaxDelay(long retryReportingMaxDelay) {
        this.retryReportingMaxDelay = retryReportingMaxDelay;
        return (E) this;
    }

    public E withRetryReportingFastFailThreshold(long retryReportingFastFailThreshold) {
        this.retryReportingFastFailThreshold = retryReportingFastFailThreshold;
        return (E) this;
    }

    public E withEventThreads(int eventThreads) {
        this.eventThreads = eventThreads;
        return (E) this;
    }

    public E withConsolePrompt(String consolePrompt) {
        this.consolePrompt = consolePrompt;
        return (E) this;
    }

    /** Default UCM client data center of application cluster. */
    public final static String DEF_ZONE_NAME = "defaultZone";

    /** Default UCM client application cluster service name mark. */
    public final static String DEF_CLUSTER_NAME = "defaultUcmClient";

    /**
     * UCM client application/service unique identification. (Default is the
     * system processId)
     */
    public final static String DEF_SERVICEID = SystemUtils2.LOCAL_PROCESS_ID;

    /**
     * Default UCM client register network interface configuration.
     */
    public final static InetProperties DEF_INET = new InetProperties();

    /**
     * Default Fetch timeout on waiting to read data from the UCM Server.
     */
    public final static long DEF_WATCH_R_TIMEOUT_MS = 30 * 1000L;

    /**
     * Default connect timeoutMs.
     */
    public final static long DEF_CONN_TIMEOUT_MS = 6 * 1000L;

    /**
     * Default invoking async event threads pool maximum.
     */
    public final static int DEF_EVENT_THREADS = 2;

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

}