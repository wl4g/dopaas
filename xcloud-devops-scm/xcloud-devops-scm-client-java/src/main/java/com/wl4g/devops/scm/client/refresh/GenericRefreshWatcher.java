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

import com.github.rholder.retry.Attempt;
import com.github.rholder.retry.RetryListener;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.wl4g.components.common.codec.CodecSource;
import com.wl4g.components.common.crypto.symmetric.AES128ECBPKCS5;
import com.wl4g.components.common.task.GenericTaskRunner;
import com.wl4g.components.common.task.RunnerProperties;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.event.support.ScmEventPublisher;
import com.wl4g.devops.scm.client.event.support.ScmEventSubscriber;
import com.wl4g.devops.scm.client.utils.NodeHolder;
import com.wl4g.devops.scm.common.command.WatchCommand;
import com.wl4g.devops.scm.common.command.WatchCommandResult;
import com.wl4g.devops.scm.common.command.ReportCommand.ChangedRecord;
import com.wl4g.devops.scm.common.command.WatchCommandResult.ReleasePropertySource;
import com.wl4g.devops.scm.common.exception.ScmException;

import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.lang.ThreadUtils2.sleep;
import static com.wl4g.devops.scm.client.refresh.RefreshConfigHolder.*;
import static com.wl4g.devops.scm.common.config.SCMConstants.*;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Abstract refresh watcher.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年10月20日
 * @since
 * @see {@link org.springframework.cloud.zookeeper.config.ConfigWatcher
 *      ConfigWatcher}
 */
public abstract class GenericRefreshWatcher extends GenericTaskRunner<RunnerProperties> {

	/** SCM client configuration */
	protected final ScmClientProperties config;

	/** SCM refresh of {@link ScmEventPublisher}. */
	protected final ScmEventPublisher publisher;

	/** {@link ScmEventSubscriber} */
	protected final ScmEventSubscriber subscriber;

	/** SCM client instance holder */
	protected final NodeHolder holder;

	/** SCM client reporting retryer */
	protected final Retryer<Boolean> retryer;

	/** SCM client reporting handler */
	protected final ConfigReportingHandler handler;

	/**
	 * Last Update Time
	 */
	protected long lastUpdateTime = 0;

	public GenericRefreshWatcher(ScmClientProperties config, ScmEventPublisher publisher, ScmEventSubscriber subscriber) {
		super(new RunnerProperties(1, 0, 0).withAsyncStartup(true));
		notNullOf(config, "config");
		notNullOf(publisher, "publisher");
		notNullOf(subscriber, "subscriber");
		this.config = config;
		this.publisher = publisher;
		this.subscriber = subscriber;
		this.holder = new NodeHolder(config);
		this.handler = new ConfigReportingHandler();
		this.retryer = RetryerBuilder.<Boolean> newBuilder().retryIfExceptionOfType(Throwable.class)// Exception-retry-source
				.retryIfResult(res -> !Boolean.valueOf(valueOf(res))) // Retrial-condition
				.withWaitStrategy(WaitStrategies.randomWait(config.getRetryReportingRandomMinIntervalMs(), MILLISECONDS,
						config.getRetryReportingRandomMaxIntervalMs(), MILLISECONDS)) // Waiting-interval
				.withStopStrategy(StopStrategies.neverStop()) // stop-retries
				.withRetryListener(new RetryListener() {
					@Override
					public <V> void onRetry(Attempt<V> attempt) {
						// Discard/cleanup after maximum attempt.(if necessary)
						if (config.getRetryReportingDiscardFailThreshold() > 0
								&& attempt.getAttemptNumber() > config.getRetryReportingDiscardFailThreshold()) {
							log.warn("Reporting retries max threshold({}), discarded refresh changed record!!!",
									attempt.getAttemptNumber());
							pollChangedAll();
						}
						// Publishing reporting
						publisher.publishReportingEvent(attempt);
					}
				}).build();

	}

	@Override
	public void run() {
		log.info("Running config reporting handler...");
		while (isActive()) {
			try {
				retryer.call(handler);
			} catch (Exception e) {
				log.error("Failed to reporting.", e);
			}
		}
	}

	/**
	 * New create {@link WatchCommand}
	 * 
	 * @return
	 */
	public WatchCommand createWatchCommand() {
		// Create config watching fetching command
		WatchCommandResult lastRelease = getReleaseConfig(false);
		return new WatchCommand(config.getClusterName(), config.getNamespaces(), lastRelease.getMeta(), holder.getConfigNode());
	}

	/**
	 * Delay refresh portection freq limit.
	 */
	protected void beforeDelayRefreshProtectLimit() {
		long now = currentTimeMillis();
		long intervalMs = now - lastUpdateTime;
		if (intervalMs < config.getRefreshProtectIntervalMs()) {
			log.warn("Refresh too fast? Watch long polling waiting...  lastUpdateTime: {}, now: {}, intervalMs: {}",
					lastUpdateTime, now, intervalMs);
			sleep(config.getRefreshProtectIntervalMs());
		}
	}

	/**
	 * Handling watch result.
	 * 
	 * @param command
	 * @param result
	 */
	protected void handleWatchResult(int command, WatchCommandResult result) {
		switch (command) {
		case WATCH_CHANGED:
			// Extract config result
			notNull(result, ScmException.class, "Watch received config source not available");
			result.validation(true, true);

			// Print configuration sources
			printConfigSources(result);

			// Sets release config source.
			setReleaseConfig(result);

			// Records changed property names.
			addChanged(null);

			lastUpdateTime = currentTimeMillis();

			// Publishing refresh
			publisher.publishRefreshEvent(result);
			break;
		case WATCH_CHECKPOINT:
			// Report refresh changed
			// newChangedReportingCallable();
			publisher.publishCheckpointEvent(this);
			break;
		case WATCH_NOT_MODIFIED: // Next long-polling
			log.trace("Unchanged and continue next long-polling ... ");
			publisher.publishNextEvent(this);
			break;
		default:
			throw new ScmException(format("Error watch unknown protocal command: '%s'", command));
		}
	}

	/**
	 * Resolver cipher configuration source.
	 * 
	 * @param result
	 */
	protected void resolvesCipherSource(WatchCommandResult result) {
		log.debug("Resolver cipher configuration propertySource ...");

		for (ReleasePropertySource ps : result.getPropertySources()) {
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
	 * @param result
	 */
	protected void printConfigSources(WatchCommandResult result) {
		log.info("Fetched from scm config <= group({}), namespace({}), release meta({})", result.getCluster(),
				result.getNamespaces(), result.getMeta());

		if (log.isDebugEnabled()) {
			List<ReleasePropertySource> propertySources = result.getPropertySources();
			if (propertySources != null) {
				int propertyCount = 0;
				for (ReleasePropertySource ps : propertySources) {
					propertyCount += ps.getSource().size();
				}
				log.debug(String.format("Environment has %d property sources with %d properties.", propertySources.size(),
						propertyCount));
			}
		}
	}

	/**
	 * DO reporting changed records
	 * 
	 * @param records
	 * @return
	 */
	protected abstract boolean doReporting(Collection<ChangedRecord> records);

	/** SCM encrypted field identification prefix */
	final public static String CIPHER_PREFIX = "{cipher}";

	/**
	 * {@link ConfigReportingHandler}
	 *
	 * @since
	 */
	class ConfigReportingHandler implements Callable<Boolean> {
		@Override
		public Boolean call() throws Exception {
			Collection<ChangedRecord> records = getChangedQueues();
			boolean result = doReporting(records);
			if (result) { // Success and cleanup
				records = pollChangedAll();
				log.debug("Reporting success and cleaned for records: {}", records);
			}
			return result;
		}
	}

}