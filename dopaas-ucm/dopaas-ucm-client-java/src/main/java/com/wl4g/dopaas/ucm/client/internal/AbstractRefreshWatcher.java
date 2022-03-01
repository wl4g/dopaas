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

import static com.wl4g.dopaas.ucm.common.UCMConstants.WATCH_CHANGED;
import static com.wl4g.dopaas.ucm.common.UCMConstants.WATCH_CHECKPOINT;
import static com.wl4g.dopaas.ucm.common.UCMConstants.WATCH_NOT_MODIFIED;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo;
import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo.ReleaseContent;
import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfoRequest;
import com.wl4g.dopaas.ucm.client.event.ConfigEventListener;
import com.wl4g.dopaas.ucm.client.event.UcmEventPublisher;
import com.wl4g.dopaas.ucm.client.event.UcmEventSubscriber;
import com.wl4g.dopaas.ucm.client.recorder.ChangeRecorder;
import com.wl4g.dopaas.ucm.client.recorder.ReleaseConfigSourceWrapper;
import com.wl4g.dopaas.ucm.client.utils.NodeHolder;
import com.wl4g.dopaas.ucm.common.config.UcmConfigSource;
import com.wl4g.dopaas.ucm.common.config.resolve.DefaultPropertySourceResolver;
import com.wl4g.dopaas.ucm.common.config.resolve.PropertySourceResolver;
import com.wl4g.dopaas.ucm.common.exception.UcmException;
import com.wl4g.infra.common.eventbus.EventBusSupport;
import com.wl4g.infra.common.task.GenericTaskRunner;
import com.wl4g.infra.common.task.RunnerProperties;

/**
 * Generic abstract refresh watcher.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年10月20日
 * @since
 * @see {@link org.springframework.cloud.zookeeper.config.ConfigWatcher
 *      ConfigWatcher}
 */
public abstract class AbstractRefreshWatcher<T extends AbstractUcmClientConfig<?>> extends GenericTaskRunner<RunnerProperties>
        implements RefreshWatcher {

    /** UCM client configuration */
    protected final T config;

    /** UCM refresh of {@link UcmEventPublisher}. */
    protected final UcmEventPublisher publisher;

    /** {@link UcmEventSubscriber} */
    protected final UcmEventSubscriber subscriber;

    /** UCM client instance holder */
    protected final NodeHolder nodeHolder;

    /** {@link ChangeRecorder} */
    protected final ChangeRecorder recorder;

    /** {@link PropertySourceResolver} */
    protected final PropertySourceResolver resolver;

    /**
     * 
     * Last Update Time
     */
    protected long lastRefreshTime = 0;

    public AbstractRefreshWatcher(@NotNull RunnerProperties runner, @NotNull T config, @NotNull ChangeRecorder repository,
            @Nullable ConfigEventListener... listeners) {
        super(runner);
        this.config = notNullOf(config, "config");
        this.recorder = notNullOf(repository, "repository");
        EventBusSupport bus = new EventBusSupport(getUcmConfig().getEventThreads());
        this.publisher = new UcmEventPublisher(this, bus);
        this.subscriber = new UcmEventSubscriber(this, bus, listeners);
        this.nodeHolder = new NodeHolder(config);
        this.resolver = new DefaultPropertySourceResolver();
    }

    /**
     * Gets {@link UcmClientProperties}
     * 
     * @return
     */
    public T getUcmConfig() {
        return config;
    }

    /**
     * Gets {@link UcmEventPublisher}
     * 
     * @return
     */
    public UcmEventPublisher getPublisher() {
        return publisher;
    }

    /**
     * Gets {@link UcmEventSubscriber}
     * 
     * @return
     */
    public UcmEventSubscriber getSubscriber() {
        return subscriber;
    }

    /**
     * Gets {@link NodeHolder}
     * 
     * @return
     */
    public NodeHolder getNodeHolder() {
        return nodeHolder;
    }

    /**
     * Gets {@link ChangeRecorder}
     * 
     * @return
     */
    public ChangeRecorder getRecorder() {
        return recorder;
    }

    @Override
    public void close() throws IOException {
        super.close();
        EventBusSupport.getDefault().close();
    }

    /**
     * New create {@link FetchReleaseConfigRequest}
     * 
     * @return
     */
    protected ReleaseConfigInfoRequest createFetchRequest() {
        // Create config watching fetching command
        ReleaseConfigSourceWrapper last = recorder.lastRelease();

        ReleaseConfigInfoRequest fetch = new ReleaseConfigInfoRequest();
        fetch.setZone(config.getZone());
        fetch.setCluster(config.getCluster());
        fetch.setNode(nodeHolder.getConfigNode());
        fetch.setProfiles(config.getProfiles());
        fetch.setMeta(nonNull(last) ? last.getRelease().getMeta() : null);
        return fetch;
    }

    /**
     * Handling watch result.
     * 
     * @param command
     * @param info
     */
    protected void handleWatchResult(int command, ReleaseConfigInfo info) {
        switch (command) {
        case WATCH_CHANGED:
            lastRefreshTime = currentTimeMillis();

            // Extract config result
            notNull(info, UcmException.class, "Watch received config source not available");
            info.validate(true, true);

            // Print release sources
            printConfigSources(info);

            // Resolving to property sources.
            List<UcmConfigSource> sources = safeList(info.getReleases()).stream()
                    .map(r -> resolver.resolve(r.getProfile(), r.getSourceContent()))
                    .collect(toList());

            // Addition refresh config source.
            ReleaseConfigSourceWrapper wrapper = new ReleaseConfigSourceWrapper(info, sources);
            recorder.save(wrapper);

            // Publishing refresh
            getPublisher().publishRefreshEvent(wrapper);
            break;
        case WATCH_CHECKPOINT:
            // Report refresh changed
            getPublisher().publishCheckpointEvent(this);
            break;
        case WATCH_NOT_MODIFIED: // Next long-polling
            log.trace("Unchanged and continue next long-polling ... ");
            break;
        default:
            throw new UcmException(format("Error watch unknown protocal command: '%s'", command));
        }
    }

    /**
     * Prints configuration sources.
     * 
     * @param release
     */
    protected void printConfigSources(ReleaseConfigInfo release) {
        log.info("Fetched UCM config for cluster: {}, profiles: {}, meta: {}", release.getZone(), release.getCluster(),
                release.getMeta());

        if (log.isDebugEnabled()) {
            List<ReleaseContent> rss = release.getReleases();
            if (rss != null) {
                int pscount = release.getReleases().size();
                log.debug("Release config profiles: {}, the property sources sizeof: {}", rss.size(), pscount);
            }
        }
    }

    /** UCM encrypted field identification prefix */
    final public static String CIPHER_PREFIX = "{cipher}";

}