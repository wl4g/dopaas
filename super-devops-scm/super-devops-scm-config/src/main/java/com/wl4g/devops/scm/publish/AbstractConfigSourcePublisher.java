/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
import com.google.common.collect.Multimaps;
import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseInstance;
import com.wl4g.devops.common.bean.scm.model.GetRelease;
import com.wl4g.devops.common.bean.scm.model.PreRelease;
import com.wl4g.devops.scm.config.ScmProperties;
import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.GenericTaskRunner.RunProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
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
public abstract class AbstractConfigSourcePublisher extends GenericTaskRunner<RunProperties> implements ConfigSourcePublisher {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** SCM properties configuration */
	final protected ScmProperties config;

	/** Deferred watch controller */
	final private AtomicBoolean watchController = new AtomicBoolean(false);

	/**
	 * Save all client monitors configuration requests globally (Using:
	 * HTTP-long-poling model).
	 */
	final private Map<String, Multimap<String, WatchDeferredResult<ResponseEntity<?>>>> watchRequests;

	public AbstractConfigSourcePublisher(ScmProperties config) {
		super(new RunProperties());
		this.config = config;
		this.watchRequests = new ConcurrentHashMap<>(32);
	}

	@Override
	protected void postStartupProperties() {
		if (watchController.compareAndSet(false, true)) {
			if (log.isInfoEnabled()) {
				log.info("Starting config source watchRequests: {}", watchRequests.size());
			}
		}
	}

	@Override
	protected void postCloseProperties() {
		if (watchController.compareAndSet(true, false)) {
			if (log.isInfoEnabled()) {
				log.info("Closing configSource watcher, watchRequests: {}", watchRequests.size());
			}
		}
	}

	@Override
	public void run() {
		while (watchController.get()) {
			// Scan poll published configuration
			try {
				Collection<PublishConfigWrapper> next = null;
				while (watchController.get() && !isEmpty(next = pollNextPublishedConfig())) {
					if (log.isInfoEnabled()) {
						log.info("Scan published config for - {}", next);
					}

					for (PublishConfigWrapper wrap : next) {
						Assert.state((wrap != null && isNotBlank(wrap.getCluster())),
								String.format("Published config group must not be blank! - %s", wrap));

						getCreateWithDeferreds(wrap.getCluster()).values().stream().filter(deferred -> {
							if (deferred != null) {
								GetRelease watch = deferred.getWatch();
								// Filters name space
								wrap.getNamespaces().retainAll(watch.getNamespaces());
								if (!CollectionUtils.isEmpty(wrap.getNamespaces())) {
									// Filters instance
									return wrap.getInstances().contains(watch.getInstance());
								}
							}
							return false;
						}).forEach(deferred -> {
							if (!deferred.isSetOrExpired()) {
								if (log.isDebugEnabled()) {
									log.debug("Set deferredResult - {}", deferred);
								}
								deferred.setResult(createResponse(OK, wrap.getMeta()));
							}
						});
					}
				}

				Thread.sleep(config.getWatchDelay());
			} catch (Throwable th) {
				log.error("Watching error!", th);
			}
		}
	}

	@Override
	public List<WatchDeferredResult<ResponseEntity<?>>> publish(PreRelease pre) {
		Assert.notNull(pre, "Publish release must not be null");

		// Got or create local watching deferredResults.
		List<WatchDeferredResult<ResponseEntity<?>>> deferreds = getCreateWithDeferreds(pre.getCluster()).values().stream()
				.collect(Collectors.toList());

		// Put watch instances(for clustering).
		publishConfig(new PublishConfigWrapper(pre));

		return deferreds;
	}

	@Override
	public WatchDeferredResult<ResponseEntity<?>> watch(GetRelease watch) {
		// Override creation listening latency.
		WatchDeferredResult<ResponseEntity<?>> deferred = doCreateWatchDeferred(watch);

		if (log.isInfoEnabled()) {
			log.info("Created watch deferredResult - {}", deferred);
		}
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
	protected abstract void publishConfig(PublishConfigWrapper wrap);

	/**
	 * Create watch deferred result.
	 * 
	 * @param watch
	 * @return
	 */
	protected WatchDeferredResult<ResponseEntity<?>> doCreateWatchDeferred(GetRelease watch) {
		Assert.notNull(watch, "Watch must not be null");

		// Create watch-deferred
		WatchDeferredResult<ResponseEntity<?>> deferred = new WatchDeferredResult<>(config.getLongPollTimeout(), watch);

		Multimap<String, WatchDeferredResult<ResponseEntity<?>>> deferreds = getCreateWithDeferreds(watch.getCluster());
		String watchKey = getWatchKey(watch.getInstance(), watch.getNamespaces());
		deferreds.put(watchKey, deferred);

		final String instance = watch.getInstance().toString();
		// When deferred Result completes (whether it is timeout or abnormal or
		// normal), remove the corresponding watch key from watchRequests
		deferred.onCompletion(() -> {
			if (log.isInfoEnabled()) {
				log.info("Completed watch instance - {}", instance);
			}
			deferreds.remove(watchKey, instance);
		});

		deferred.onTimeout(() -> {
			if (log.isWarnEnabled()) {
				log.warn("Timeout watch instance - {}", instance);
			}
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
		Assert.hasText(group, "Group must not be empty");

		Multimap<String, WatchDeferredResult<ResponseEntity<?>>> watchs = watchRequests.get(group);
		if (watchs == null) {
			watchRequests.putIfAbsent(group, (watchs = Multimaps.synchronizedSetMultimap(HashMultimap.create())));
		}

		if (log.isDebugEnabled()) {
			log.debug("Get-create watch group: {}, total: {}, deferredResults: {}", group, watchRequests.size(), watchs);
		}
		return watchs;
	}

	/**
	 * Generate watching deferred key.
	 * 
	 * @param instance
	 * @param namespace
	 * @return
	 */
	protected String getWatchKey(ReleaseInstance instance, List<String> namespaces) {
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
	public static class PublishConfigWrapper extends PreRelease {

		private static final long serialVersionUID = 1569807245009223834L;

		public PublishConfigWrapper(PreRelease pre) {
			super(pre.getCluster(), pre.getNamespaces(), pre.getMeta());
			setInstances(pre.getInstances());
		}

		public String asIdentify() {
			return getCluster() + "_" + getMeta().asText();
		}

	}

}