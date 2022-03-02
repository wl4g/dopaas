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
package com.wl4g.dopaas.ucm.publish;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import static com.google.common.collect.Multimaps.synchronizedSetMultimap;
import static com.wl4g.infra.common.lang.Assert2.hasText;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import com.wl4g.infra.common.task.RunnerProperties;
import com.wl4g.infra.common.task.RunnerProperties.StartupMode;
import com.wl4g.infra.core.task.ApplicationTaskRunner;

import lombok.NoArgsConstructor;

import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfoRequest;
import com.wl4g.dopaas.common.bean.ucm.model.BaseConfigInfo.ConfigInstance;
import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo;
import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo.ConfigProfile;
import com.wl4g.dopaas.ucm.config.UcmProperties;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Abstract configuration source publisher.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年10月26日
 * @since
 */
public abstract class GenericConfigSourcePublisher extends ApplicationTaskRunner<RunnerProperties>
        implements ConfigSourcePublisher {

    /** UCM properties configuration */
    protected final UcmProperties config;

    /**
     * Saved all client monitors configuration requests globally (Using:
     * HTTP-long-poling model).
     */
    private final Map<String, Multimap<String, WatchDeferredResult<ResponseEntity<?>>>> watchRequests;

    public GenericConfigSourcePublisher(UcmProperties config) {
        super(new RunnerProperties(StartupMode.SYNC, 1));
        this.config = notNullOf(config, "config");
        this.watchRequests = new ConcurrentHashMap<>(32);
    }

    @Override
    public void run() {
        getWorker().scheduleWithFixedDelay(() -> {
            try {
                // Scan poll published configuration.
                Collection<PublishedConfigWrapper> nexts = null;
                while (isActive() && !isEmpty(nexts = pollNextPublishedConfig())) {
                    log.info("Poll published config for - {}", nexts);

                    for (PublishedConfigWrapper published : nexts) {
                        if (isNull(published) || isBlank(published.getCluster())) {
                            log.warn("Published config group must not be blank! - %s", published);
                            continue;
                        }

                        getOrCreateDeferredsWithCluster(published.getZone(), published.getCluster()).values()
                                .stream()
                                .filter(deferred -> {
                                    if (nonNull(deferred)) {
                                        ReleaseConfigInfoRequest watch = deferred.getWatch();
                                        // Filtering profiles
                                        published.getProfiles().retainAll(watch.getProfiles());
                                        if (!CollectionUtils.isEmpty(published.getProfiles())) {
                                            // Filtering instances
                                            return published.getInstances().contains(watch.getInstance());
                                        }
                                    }
                                    return false;
                                })
                                .forEach(deferred -> {
                                    if (!deferred.isSetOrExpired()) {
                                        log.debug("Sets deferredResult - {}", deferred);
                                        deferred.setResult(createResponse(OK, published.getMeta()));
                                    }
                                });
                    }
                }
            } catch (Throwable th) {
                log.error("Watching running error!", th);
            }
        }, 3000L, config.getWatchDelay(), MILLISECONDS);

    }

    @Override
    public List<WatchDeferredResult<ResponseEntity<?>>> publish(ReleaseConfigInfo release) {
        notNull(release, "Publish release must not be null");

        // Got or create local watching deferredResults.
        List<WatchDeferredResult<ResponseEntity<?>>> deferreds = getOrCreateDeferredsWithCluster(release.getZone(),
                release.getCluster()).values().stream().collect(toList());

        // Publishing to watch instances(cluster).
        doPublishConfig(new PublishedConfigWrapper(release));

        return deferreds;
    }

    @Override
    public WatchDeferredResult<ResponseEntity<?>> watch(ReleaseConfigInfoRequest watch) {
        // Override creation listening latency.
        WatchDeferredResult<ResponseEntity<?>> deferred = doCreateWatchDeferred(watch);

        log.info("Created watch deferred - {}", deferred);
        return deferred;
    }

    /**
     * Create response entity.
     * 
     * @param status
     * @param body
     * @return
     */
    protected <T> ResponseEntity<T> createResponse(HttpStatus status, T body) {
        return new ResponseEntity<T>(body, status);
    }

    /**
     * Poll scan published configuration source
     * 
     * @return
     */
    protected abstract Collection<PublishedConfigWrapper> pollNextPublishedConfig();

    /**
     * Put published instances to distributed cache, support distributed
     * 
     * @param wrap
     */
    protected abstract void doPublishConfig(PublishedConfigWrapper wrap);

    /**
     * Create watch deferred result.
     * 
     * @param watch
     * @return
     */
    protected WatchDeferredResult<ResponseEntity<?>> doCreateWatchDeferred(ReleaseConfigInfoRequest watch) {
        notNullOf(watch, "watch");

        // Create watch-deferred
        WatchDeferredResult<ResponseEntity<?>> deferred = new WatchDeferredResult<>(config.getLongPollTimeout(), watch);

        Multimap<String, WatchDeferredResult<ResponseEntity<?>>> deferreds = getOrCreateDeferredsWithCluster(watch.getZone(),
                watch.getCluster());
        String instanceWatchKey = getInstanceWatchKey(watch.getInstance(), watch.getProfiles());
        deferreds.put(instanceWatchKey, deferred);

        final String instance = watch.getInstance().toString();
        // When deferred Result completes (whether it is timeout or abnormal or
        // normal), remove the corresponding watch key from watchRequests
        deferred.onCompletion(() -> {
            log.info("Completed watch instance - {}", instance);
            deferreds.remove(instanceWatchKey, instance);
        });

        deferred.onTimeout(() -> {
            log.warn("Timeout watch instance - {}", instance);
            deferreds.remove(instanceWatchKey, instance);

            // In response to 304(No any configuration modified), the
            // client will then re-establish the long-polling request.
            deferred.setResult(createResponse(NOT_MODIFIED, null));
        });

        return deferred;
    }

    /**
     * Get or create local watch deferred result by group.
     * 
     * @param cluster
     * @return
     */
    protected Multimap<String, WatchDeferredResult<ResponseEntity<?>>> getOrCreateDeferredsWithCluster(String zone,
            String cluster) {
        hasTextOf(zone, "zone");
        hasTextOf(cluster, "cluster");

        String clusterWatchKey = getClusterWatchKey(zone, cluster);
        Multimap<String, WatchDeferredResult<ResponseEntity<?>>> watchs = watchRequests.get(clusterWatchKey);
        if (watchs == null) {
            watchRequests.putIfAbsent(clusterWatchKey, (watchs = synchronizedSetMultimap(HashMultimap.create())));
        }

        log.debug("Got-created watch zone: {}, cluster: {}, watchRequets: {}, deferredResults: {}", zone, cluster,
                watchRequests.size(), watchs);
        return watchs;
    }

    /**
     * Generate cluster watching deferred key.
     * 
     * @param instance
     * @param namespace
     * @return
     */
    protected String getClusterWatchKey(String zone, String cluster) {
        hasTextOf(zone, "zone");
        hasTextOf(cluster, "cluster");
        return zone.concat("/").concat(cluster);
    }

    /**
     * Generate instance watching deferred key.
     * 
     * @param instance
     * @param namespace
     * @return
     */
    protected String getInstanceWatchKey(ConfigInstance instance, List<ConfigProfile> profiles) {
        notNullOf(instance, "instance");
        return instance.toString();
    }

    /**
     * Publish configuration source information wrapper
     * 
     * @author Wangl.sir <983708408@qq.com>
     * @version v1.0 2019年6月5日
     * @since
     */
    @NoArgsConstructor
    public static class PublishedConfigWrapper extends ReleaseConfigInfo {
        private static final long serialVersionUID = 1569807245009223834L;

        public PublishedConfigWrapper(ReleaseConfigInfo release) {
            super(release);
        }

        public String asIdentify() {
            return getZone().concat("_").concat(getCluster()).concat("_").concat(getMeta().asText());
        }

    }

}