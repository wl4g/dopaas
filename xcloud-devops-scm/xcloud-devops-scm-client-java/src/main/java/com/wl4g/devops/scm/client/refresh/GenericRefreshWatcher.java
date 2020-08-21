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
package com.wl4g.devops.scm.client.refresh;

import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.components.common.codec.CodecSource;
import com.wl4g.components.common.crypto.symmetric.AES128ECBPKCS5;
import com.wl4g.components.common.eventbus.EventBusSupport;
import com.wl4g.components.common.task.GenericTaskRunner;
import com.wl4g.components.common.task.RunnerProperties;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.event.ConfigEventListener;
import com.wl4g.devops.scm.client.event.ScmEventPublisher;
import com.wl4g.devops.scm.client.event.ScmEventSubscriber;
import com.wl4g.devops.scm.client.repository.RefreshConfigRepository;
import com.wl4g.devops.scm.client.utils.NodeHolder;
import com.wl4g.devops.scm.common.command.FetchConfigRequest;
import com.wl4g.devops.scm.common.command.ReleaseConfigInfo;
import com.wl4g.devops.scm.common.command.GenericConfigInfo.ConfigMeta;
import com.wl4g.devops.scm.common.command.ReleaseConfigInfo.IniPropertySource;
import com.wl4g.devops.scm.common.exception.ScmException;

import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.lang.ThreadUtils2.sleep;
import static com.wl4g.devops.scm.common.config.SCMConstants.*;
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
	protected final NodeHolder holder;

	/** {@link RefreshConfigRepository} */
	protected final RefreshConfigRepository repository;

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
		this.holder = new NodeHolder(config);
	}

	public ScmClientProperties<?> getScmConfig() {
		return config;
	}

	public ScmEventPublisher getPublisher() {
		return publisher;
	}

	public ScmEventSubscriber getSubscriber() {
		return subscriber;
	}

	public NodeHolder getHolder() {
		return holder;
	}

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
		return new FetchConfigRequest(config.getClusterName(), config.getProfiles(), meta, holder.getConfigNode());
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
	 * @param source
	 */
	protected void handleWatchResult(int command, ReleaseConfigInfo source) {
		switch (command) {
		case WATCH_CHANGED:
			lastRefreshTime = currentTimeMillis();

			// Extract config result
			notNull(source, ScmException.class, "Watch received config source not available");
			source.validate(true, true);

			// Print configuration sources
			printConfigSources(source);

			// Addition refresh config source.
			repository.saveReleaseConfig(source);

			// Publishing refresh
			publisher.publishRefreshEvent(source);
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
	 * Resolver cipher configuration source.
	 * 
	 * @param source
	 */
	protected void resolvesCipherSource(ReleaseConfigInfo source) {
		log.debug("Resolver cipher configuration propertySource ...");

		for (IniPropertySource ps : source.getPropertySources()) {
			ps.getSource().forEach((key, value) -> {
				String cipher = String.valueOf(value);
				if (cipher.startsWith(CIPHER_PREFIX)) {
					try {
						// TODO using dynamic cipherKey??
						byte[] cipherKey = AES128ECBPKCS5.getEnvCipherKey("DEVOPS_CIPHER_KEY");
						String cipherText = cipher.substring(CIPHER_PREFIX.length());
						// TODO fromHex()??
						String plain = new AES128ECBPKCS5().decrypt(cipherKey, CodecSource.fromHex(cipherText)).toString();
						ps.getSource().put(key, plain);

						log.debug("Decryption property key: {}, cipherText: {}, plainText: {}", key, cipher, plain);
					} catch (Exception e) {
						throw new ScmException("Cipher decryption error.", e);
					}
				}
			});
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
			List<IniPropertySource> pss = source.getPropertySources();
			if (pss != null) {
				int psSourceCount = pss.stream().map(ps -> ps.getSource().size()).reduce((c, s) -> s += c).get();
				log.debug("Release config profiles: {}, the property sources sizeof: {}", pss.size(), psSourceCount);
			}
		}

	}

	/** SCM encrypted field identification prefix */
	final public static String CIPHER_PREFIX = "{cipher}";

}