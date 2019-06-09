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

import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseInstance;
import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseMeta;
import com.wl4g.devops.common.bean.scm.model.GetRelease;
import com.wl4g.devops.common.bean.scm.model.PreRelease;
import com.wl4g.devops.scm.config.ScmProperties;
import com.wl4g.devops.support.task.GenericTaskRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Abstract configuration source publisher.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年10月26日
 * @since
 */
public abstract class AbstractConfigSourcePublisher extends GenericTaskRunner implements ConfigSourcePublisher {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** SCM properties config */
	final protected ScmProperties config;

	/** Deferred watch controller */
	final private AtomicBoolean watchController = new AtomicBoolean(false);

	/**
	 * Save all client monitors configuration requests globally (Using:
	 * HTTP-long-poling model).
	 */
	final private Map<String, Map<String, WatchDeferredResult<ResponseEntity<?>>>> watchRequests;

	public AbstractConfigSourcePublisher(ScmProperties config) {
		super(config);
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

			// Scan poll published configs.
			Collection<PublishConfigWrapper> next = null;
			while ((next = pollNextPublishedConfig()) != null) {
				if (log.isDebugEnabled()) {
					log.debug("Scan published config: {}", next);
				}

				for (PublishConfigWrapper wrap : next) {
					Collection<WatchDeferredResult<ResponseEntity<?>>> deferreds = getCreateLocalWatchDeferreds(wrap.getGroup())
							.values();
					for (WatchDeferredResult<ResponseEntity<?>> deferred : deferreds) {
						if (!deferred.isSetOrExpired()) {
							deferred.setResult(createResponse(OK, wrap.getMeta()));
						}
					}
				}
			}

			try {
				Thread.sleep(config.getScanDelay());
			} catch (InterruptedException e) {
				log.error("", e);
			}
		}
	}

	@Override
	public List<WatchDeferredResult<ResponseEntity<?>>> publish(PreRelease pre) {
		Assert.notNull(pre, "Publish release must not be null");

		// Got or create local watching deferredResults.
		List<WatchDeferredResult<ResponseEntity<?>>> deferreds = getCreateLocalWatchDeferreds(pre.getGroup()).values().stream()
				.collect(Collectors.toList());

		// Put watch instances(for clustering).
		putPublishConfig(new PublishConfigWrapper(pre.getGroup(), pre.getInstances(), pre.getMeta()));

		return deferreds;
	}

	@Override
	public WatchDeferredResult<ResponseEntity<?>> watch(GetRelease watch) {
		if (log.isInfoEnabled()) {
			log.info("On watch config source: {}", watch);
		}

		// Override creation listening latency.
		WatchDeferredResult<ResponseEntity<?>> deferred = doCreateWatchDeferred(watch);

		if (log.isInfoEnabled()) {
			log.info("Published! deferred result: {}", deferred);
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
	 * scan publish config source wrapper
	 * 
	 * @return
	 */
	protected abstract Collection<PublishConfigWrapper> pollNextPublishedConfig();

	/**
	 * Put published instances to distributed cache, support distributed
	 * 
	 * @param wrap
	 */
	protected abstract void putPublishConfig(PublishConfigWrapper wrap);

	/**
	 * Create watch deferred result.
	 * 
	 * @param watch
	 * @return
	 */
	protected WatchDeferredResult<ResponseEntity<?>> doCreateWatchDeferred(GetRelease watch) {
		Assert.notNull(watch, "Watch must not be null");

		// Create watch-deferred
		WatchDeferredResult<ResponseEntity<?>> deferred = new WatchDeferredResult<>(config.getDefaultTimeout());

		Map<String, WatchDeferredResult<ResponseEntity<?>>> deferredGroup = getCreateLocalWatchDeferreds(watch.getGroup());
		deferredGroup.put(getWatchKey(watch.getInstance(), watch.getNamespace()), deferred);

		final String instance = watch.getInstance().toString();
		// When deferred Result completes (whether it is timeout or abnormal or
		// normal), remove the corresponding watch key from watchRequests
		deferred.onCompletion(() -> {
			if (log.isInfoEnabled()) {
				log.info("Completion watch deferred for: {}", instance);
			}
			deferredGroup.remove(instance);
		});

		deferred.onTimeout(() -> {
			if (log.isWarnEnabled()) {
				log.warn("Watch deferred timeout for: {}", instance);
			}
			deferredGroup.remove(instance);

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
	protected Map<String, WatchDeferredResult<ResponseEntity<?>>> getCreateLocalWatchDeferreds(String group) {
		Assert.hasText(group, "Group must not be empty");

		Map<String, WatchDeferredResult<ResponseEntity<?>>> watchGroup = watchRequests.get(group);
		if (watchGroup == null) {
			watchRequests.putIfAbsent(group, (watchGroup = new ConcurrentHashMap<>(128)));
		}

		if (log.isInfoEnabled()) {
			log.info("Got create watch deferred results: {}, total: {}", watchGroup, watchRequests.size());
		}
		return watchGroup;
	}

	/**
	 * Generate watching deferred key.
	 * 
	 * @param instance
	 * @param namespace
	 * @return
	 */
	protected String getWatchKey(ReleaseInstance instance, String namespace) {
		Assert.notNull(instance, "Release instance must not be null");
		Assert.hasText(namespace, "Namespace must not be null");
		return instance.toString() + "-" + namespace;
	}

	/**
	 * Publish config source information wrapper
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年6月5日
	 * @since
	 */
	public static class PublishConfigWrapper implements Serializable {

		private static final long serialVersionUID = 1569807245009223834L;

		/** Release application group */
		final private String group;

		/** Release instances */
		final private List<ReleaseInstance> instances;

		/** Release meta information */
		final private ReleaseMeta meta;

		public PublishConfigWrapper(String group, List<ReleaseInstance> instances, ReleaseMeta meta) {
			Assert.notNull(group, "Group must not be null");
			Assert.notNull(instances, "Release instances must not be null");
			Assert.notNull(meta, "Release meta must not be null");
			this.group = group;
			this.instances = instances;
			this.meta = meta;
		}

		public String getGroup() {
			return group;
		}

		public List<ReleaseInstance> getInstances() {
			return instances;
		}

		public ReleaseMeta getMeta() {
			return meta;
		}

		public String asIdentify() {
			return getGroup() + "_" + getMeta().asText();
		}

		@Override
		public String toString() {
			return "WatchingWrapper [identify=" + asIdentify() + ", group=" + group + ", instances=" + instances + ", meta="
					+ meta + "]";
		}

	}

}