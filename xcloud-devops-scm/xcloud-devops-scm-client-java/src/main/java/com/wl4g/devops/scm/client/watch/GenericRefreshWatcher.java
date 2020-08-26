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
package com.wl4g.devops.scm.client.watch;

import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.components.common.eventbus.EventBusSupport;
import com.wl4g.components.common.task.GenericTaskRunner;
import com.wl4g.components.common.task.RunnerProperties;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.event.ConfigEventListener;
import com.wl4g.devops.scm.client.event.ScmEventPublisher;
import com.wl4g.devops.scm.client.event.ScmEventSubscriber;
import com.wl4g.devops.scm.client.repository.RefreshConfigRepository;
import com.wl4g.devops.scm.client.utils.NodeHolder;
import com.wl4g.devops.scm.common.config.ScmPropertySource;
import com.wl4g.devops.scm.common.config.resolve.DefaultPropertySourceResolver;
import com.wl4g.devops.scm.common.config.resolve.PropertySourceResolver;
import com.wl4g.devops.scm.common.exception.ScmException;
import com.wl4g.devops.scm.common.model.FetchConfigRequest;
import com.wl4g.devops.scm.common.model.ReleaseConfigInfo;
import com.wl4g.devops.scm.common.model.ReleaseConfigInfo.ReleaseConfigSource;
import com.wl4g.devops.scm.common.model.AbstractConfigInfo.ConfigMeta;

import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.lang.ThreadUtils2.sleep;
import static com.wl4g.devops.scm.common.SCMConstants.*;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * Generic abstract refresh watcher.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年10月20日
 * @since
 * @see {@link org.springframework.cloud.zookeeper.config.ConfigWatcher
 *      ConfigWatcher}
 */
public abstract class GenericRefreshWatcher extends GenericTaskRunner<RunnerProperties> implements RefreshWatcher {

	/** SCM client configuration */
	protected final ScmClientProperties<?> config;

	/** SCM refresh of {@link ScmEventPublisher}. */
	protected final ScmEventPublisher publisher;

	/** {@link ScmEventSubscriber} */
	protected final ScmEventSubscriber subscriber;

	/** SCM client instance holder */
	protected final NodeHolder nodeHolder;

	/** {@link RefreshConfigRepository} */
	protected final RefreshConfigRepository repository;

	/** {@link PropertySourceResolver} */
	protected final PropertySourceResolver resolver;

	/**
	 * 
	 * Last Update Time
	 */
	protected long lastRefreshTime = 0;

	public GenericRefreshWatcher(@NotNull RunnerProperties runner, @NotNull ScmClientProperties<?> config,
			@NotNull RefreshConfigRepository repository, @Nullable ConfigEventListener... listeners) {
		super(runner);
		notNullOf(config, "config");
		notNullOf(repository, "repository");
		// notNullOf(listeners, "listeners");
		this.config = config;
		this.repository = repository;
		this.publisher = new ScmEventPublisher(this);
		this.subscriber = new ScmEventSubscriber(this, listeners);
		this.nodeHolder = new NodeHolder(config);
		this.resolver = new DefaultPropertySourceResolver();
	}

	/**
	 * Gets {@link ScmClientProperties}
	 * 
	 * @return
	 */
	public ScmClientProperties<?> getScmConfig() {
		return config;
	}

	/**
	 * Gets {@link ScmEventPublisher}
	 * 
	 * @return
	 */
	public ScmEventPublisher getPublisher() {
		return publisher;
	}

	/**
	 * Gets {@link ScmEventSubscriber}
	 * 
	 * @return
	 */
	public ScmEventSubscriber getSubscriber() {
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
	 * Gets {@link RefreshConfigRepository}
	 * 
	 * @return
	 */
	public RefreshConfigRepository getRepository() {
		return repository;
	}

	@Override
	public void close() throws IOException {
		super.close();
		EventBusSupport.getDefault(-1).close();
	}

	/**
	 * New create {@link FetchConfigRequest}
	 * 
	 * @return
	 */
	protected FetchConfigRequest createFetchRequest() {
		// Create config watching fetching command
		ReleaseConfigInfo last = repository.getLastReleaseConfig();
		ConfigMeta meta = nonNull(last) ? last.getMeta() : null;
		return new FetchConfigRequest(config.getClusterName(), config.getProfiles(), meta, nodeHolder.getConfigNode());
	}

	/**
	 * Delay refresh portection freq limit.
	 */
	protected void beforeSafeRefreshProtectDelaying() {
		long now = currentTimeMillis();
		long diffIntervalMs = now - lastRefreshTime;
		if (diffIntervalMs < config.getSafeRefreshRateDelay()) {
			log.warn(
					"Refresh too fast? Watch long polling waiting...  lastUpdateTime: {}, now: {}, safeRefreshProtectDelay: {}, diffIntervalMs: {}",
					lastRefreshTime, now, config.getSafeRefreshRateDelay(), diffIntervalMs);
			sleep(config.getSafeRefreshRateDelay());
		}
	}

	/**
	 * Handling watch result.
	 * 
	 * @param command
	 * @param release
	 */
	protected void handleWatchResult(int command, ReleaseConfigInfo release) {
		switch (command) {
		case WATCH_CHANGED:
			lastRefreshTime = currentTimeMillis();

			// Extract config result
			notNull(release, ScmException.class, "Watch received config source not available");
			release.validate(true, true);

			// Print release sources
			printConfigSources(release);

			// Resolve property sources
			ScmPropertySource source = resolver.resolve("", "");

			// Addition refresh config source.
			repository.saveReleaseConfig(release);

			// Publishing refresh
			publisher.publishRefreshEvent(release);
			break;
		case WATCH_CHECKPOINT:
			// Report refresh changed
			publisher.publishCheckpointEvent(this);
			break;
		case WATCH_NOT_MODIFIED: // Next long-polling
			log.trace("Unchanged and continue next long-polling ... ");
			break;
		default:
			throw new ScmException(format("Error watch unknown protocal command: '%s'", command));
		}
	}

	/**
	 * Prints configuration sources.
	 * 
	 * @param source
	 */
	protected void printConfigSources(ReleaseConfigInfo source) {
		log.info("Fetched SCM config for cluster: {}, profiles: {}, meta: {}", source.getCluster(), source.getProfiles(),
				source.getMeta());

		if (log.isDebugEnabled()) {
			List<ReleaseConfigSource> rss = source.getReleases();
			if (rss != null) {
				int pscount = source.getReleases().size();
				log.debug("Release config profiles: {}, the property sources sizeof: {}", rss.size(), pscount);
			}
		}
	}

	/** SCM encrypted field identification prefix */
	final public static String CIPHER_PREFIX = "{cipher}";

}