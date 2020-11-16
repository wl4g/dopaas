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
package com.wl4g.devops.scm.publish;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import static com.google.common.collect.Multimaps.synchronizedSetMultimap;
import static com.wl4g.components.common.lang.Assert2.hasText;
import static com.wl4g.components.common.lang.Assert2.notNull;

import com.wl4g.components.common.task.RunnerProperties;
import com.wl4g.components.support.task.ApplicationTaskRunner;
import com.wl4g.devops.scm.common.model.FetchReleaseConfigRequest;
import com.wl4g.devops.scm.common.model.ReleaseConfigInfo;
import com.wl4g.devops.scm.common.model.AbstractConfigInfo.ConfigNode;
import com.wl4g.devops.scm.config.ScmProperties;

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

	/** SCM properties configuration */
	final protected ScmProperties config;

	/**
	 * Save all client monitors configuration requests globally (Using:
	 * HTTP-long-poling model).
	 */
	final private Map<String, Multimap<String, WatchDeferredResult<ResponseEntity<?>>>> watchRequests;

	public GenericConfigSourcePublisher(ScmProperties config) {
		super(new RunnerProperties(false, 1));
		this.config = config;
		this.watchRequests = new ConcurrentHashMap<>(32);
	}

	@Override
	public void run() {
		getWorker().scheduleWithFixedDelay(() -> {
			try {
				// Scan poll published configuration.
				Collection<PublishConfigWrapper> nexts = null;
				while (isActive() && !isEmpty(nexts = pollNextPublishedConfig())) {
					log.info("Poll published config for - {}", nexts);

					for (PublishConfigWrapper wrap : nexts) {
						if (isNull(wrap) || isBlank(wrap.getCluster())) {
							log.warn("Published config group must not be blank! - %s", wrap);
							continue;
						}

						getCreateWithDeferreds(wrap.getCluster()).values().stream().filter(deferred -> {
							if (nonNull(deferred)) {
								FetchReleaseConfigRequest watch = deferred.getWatch();
								// Filter namespace
								wrap.getProfiles().retainAll(watch.getProfiles());
								if (!CollectionUtils.isEmpty(wrap.getProfiles())) {
									// Filter instance
									return wrap.getNodes().contains(watch.getNode());
								}
							}
							return false;
						}).forEach(deferred -> {
							if (!deferred.isSetOrExpired()) {
								log.debug("Sets deferredResult - {}", deferred);
								deferred.setResult(createResponse(OK, wrap.getMeta()));
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
	public List<WatchDeferredResult<ResponseEntity<?>>> publish(ReleaseConfigInfo result) {
		notNull(result, "Publish release must not be null");

		// Got or create local watching deferredResults.
		List<WatchDeferredResult<ResponseEntity<?>>> deferreds = getCreateWithDeferreds(result.getCluster()).values().stream()
				.collect(toList());

		// Publishing to watch instances(cluster).
		doPublishConfig(new PublishConfigWrapper(result));

		return deferreds;
	}

	@Override
	public WatchDeferredResult<ResponseEntity<?>> watch(FetchReleaseConfigRequest watch) {
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
	protected abstract Collection<PublishConfigWrapper> pollNextPublishedConfig();

	/**
	 * Put published instances to distributed cache, support distributed
	 * 
	 * @param wrap
	 */
	protected abstract void doPublishConfig(PublishConfigWrapper wrap);

	/**
	 * Create watch deferred result.
	 * 
	 * @param watch
	 * @return
	 */
	protected WatchDeferredResult<ResponseEntity<?>> doCreateWatchDeferred(FetchReleaseConfigRequest watch) {
		notNull(watch, "Watch must not be null");

		// Create watch-deferred
		WatchDeferredResult<ResponseEntity<?>> deferred = new WatchDeferredResult<>(config.getLongPollTimeout(), watch);

		Multimap<String, WatchDeferredResult<ResponseEntity<?>>> deferreds = getCreateWithDeferreds(watch.getCluster());
		String watchKey = getWatchKey(watch.getNode(), watch.getProfiles());
		deferreds.put(watchKey, deferred);

		final String instance = watch.getNode().toString();
		// When deferred Result completes (whether it is timeout or abnormal or
		// normal), remove the corresponding watch key from watchRequests
		deferred.onCompletion(() -> {
			log.info("Completed watch instance - {}", instance);
			deferreds.remove(watchKey, instance);
		});

		deferred.onTimeout(() -> {
			log.warn("Timeout watch instance - {}", instance);
			deferreds.remove(watchKey, instance);

			// In response to 304(No any configuration modified), the
			// client will then re-establish the long-polling request.
			deferred.setResult(createResponse(NOT_MODIFIED, null));
		});

		return deferred;
	}

	/**
	 * Get or create local watch deferred result by group.
	 * 
	 * @param group
	 * @return
	 */
	protected Multimap<String, WatchDeferredResult<ResponseEntity<?>>> getCreateWithDeferreds(String group) {
		hasText(group, "Group must not be empty");

		Multimap<String, WatchDeferredResult<ResponseEntity<?>>> watchs = watchRequests.get(group);
		if (watchs == null) {
			watchRequests.putIfAbsent(group, (watchs = synchronizedSetMultimap(HashMultimap.create())));
		}

		log.debug("Got-created watch group: {}, total: {}, deferredResults: {}", group, watchRequests.size(), watchs);
		return watchs;
	}

	/**
	 * Generate watching deferred key.
	 * 
	 * @param instance
	 * @param namespace
	 * @return
	 */
	protected String getWatchKey(ConfigNode instance, List<String> namespaces) {
		Assert.notNull(instance, "Release instance must not be null");
		// Assert.notEmpty(namespaces, "Namespace must not be null");
		return instance.toString();
	}

	/**
	 * Publish configuration source information wrapper
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年6月5日
	 * @since
	 */
	public static class PublishConfigWrapper extends ReleaseConfigInfo {
		private static final long serialVersionUID = 1569807245009223834L;

		public PublishConfigWrapper() {
		}

		public PublishConfigWrapper(ReleaseConfigInfo result) {
			super(result.getCluster(), result.getProfiles(), result.getMeta(), result.getNodes());
		}

		public String asIdentify() {
			return getCluster() + "_" + getMeta().asText();
		}

	}

}