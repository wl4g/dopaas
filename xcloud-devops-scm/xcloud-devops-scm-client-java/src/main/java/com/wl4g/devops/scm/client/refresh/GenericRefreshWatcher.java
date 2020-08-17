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

import com.wl4g.components.common.codec.CodecSource;
import com.wl4g.components.common.crypto.symmetric.AES128ECBPKCS5;
import com.wl4g.components.common.task.GenericTaskRunner;
import com.wl4g.components.common.task.RunnerProperties;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.event.ScmEventPublisher;
import com.wl4g.devops.scm.client.utils.NodeHolder;
import com.wl4g.devops.scm.common.command.WatchCommand;
import com.wl4g.devops.scm.common.command.WatchCommandResult;
import com.wl4g.devops.scm.common.command.GenericCommand.ConfigMeta;
import com.wl4g.devops.scm.common.command.WatchCommandResult.ReleasePropertySource;
import com.wl4g.devops.scm.common.exception.ScmException;

import static com.wl4g.components.common.lang.Assert2.notNull;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.lang.ThreadUtils2.sleep;
import static com.wl4g.devops.scm.client.refresh.RefreshConfigHolder.*;
import static com.wl4g.devops.scm.common.config.SCMConstants.*;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

import java.util.List;

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

	/** SCM client instance holder */
	protected final NodeHolder holder;

	/**
	 * Last Update Time
	 */
	protected long lastUpdateTime = 0;

	public GenericRefreshWatcher(ScmClientProperties config, ScmEventPublisher publisher) {
		super(new RunnerProperties(-1, 0, 0)); // disable worker group
		notNullOf(config, "config");
		notNullOf(publisher, "publisher");
		this.config = config;
		this.publisher = publisher;
		this.holder = new NodeHolder(config);
	}

	/**
	 * New create {@link WatchCommand}
	 * 
	 * @return
	 */
	public WatchCommand createWatchCommand() {
		// Create config fetch command
		ConfigMeta meta = getConfigMeta(false);
		return new WatchCommand(config.getClusterName(), config.getNamespaces(), meta, holder.getConfigNode());
	}

	/**
	 * Delay refresh portection freq limit.
	 */
	protected void beforeDelayRefreshProtectLimit() {
		long now = currentTimeMillis();
		long intervalMs = now - lastUpdateTime;
		if (intervalMs < config.getRefreshProtectIntervalMs()) {
			log.warn("Refresh too fast? Watch long polling waiting... , lastUpdateTime: {}, now: {}, intervalMs: {}",
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
			notNull(result, ScmException.class, "Watch fetch config result not available");
			result.validation(true, true);

			// Print configuration sources
			printConfigSources(result);

			// Release changed info.
			setConfigMeta(result.getMeta());

			// Records changed property names.
			addChanged(null);

			lastUpdateTime = currentTimeMillis();
			break;
		case WATCH_CHECKPOINT:
			// Report refresh changed
			backendReport();
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
	 * Backend report changed records
	 */
	protected abstract void backendReport();

	/** SCM encrypted field identification prefix */
	final public static String CIPHER_PREFIX = "{cipher}";

}