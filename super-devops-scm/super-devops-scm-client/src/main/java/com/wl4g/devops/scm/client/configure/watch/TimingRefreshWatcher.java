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
package com.wl4g.devops.scm.client.configure.watch;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseMeta;
import com.wl4g.devops.scm.client.configure.ScmPropertySourceLocator;
import com.wl4g.devops.scm.client.configure.refresh.ScmContextRefresher;

import static com.wl4g.devops.common.constants.SCMDevOpsConstants.URI_S_BASE;
import static com.wl4g.devops.common.constants.SCMDevOpsConstants.URI_S_WATCH_GET;
import static com.wl4g.devops.scm.client.config.ScmClientProperties.*;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Timing refresh watcher
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月1日
 * @since
 */
@EnableScheduling
public class TimingRefreshWatcher extends AbstractRefreshWatcher {

	final public static String PLACEHOLDER_INIT_DELAY = "${" + PREFIX + ".watch.init-delay:90000}";
	final public static String PLACEHOLDER_DELAY = "${" + PREFIX + ".watch.delay:30000}";

	/** Watching completion state. */
	final private AtomicBoolean watchState = new AtomicBoolean(false);

	public TimingRefreshWatcher(ScmContextRefresher refresher, ScmPropertySourceLocator locator) {
		super(refresher, locator);
	}

	@Scheduled(initialDelayString = PLACEHOLDER_INIT_DELAY, fixedDelayString = PLACEHOLDER_DELAY)
	@Override
	public void run() {
		// Long-polling watching.
		createLongPollingWatch();
	}

	private void createLongPollingWatch() {
		if (watchState.compareAndSet(false, true)) {
			if (log.isInfoEnabled()) {
				log.info("Synchronizing refresh config for {} ...", watchState);
			}

			String url = locator.getConfig().getBaseUri() + URI_S_BASE + "/" + URI_S_WATCH_GET;
			ResponseEntity<ReleaseMeta> resp = locator.getRestTemplate().getForEntity(url, ReleaseMeta.class);
			if (log.isDebugEnabled()) {
				log.debug("Watching response for {}", resp);
			}

			if (resp != null) {
				// Update watching state.
				watchState.compareAndSet(true, false);

				switch (resp.getStatusCode()) {
				case OK:
					// Refresh poll changed configuration.
					refresher.refresh();
					break;
				case NOT_MODIFIED:
					// Configuration unchanged, continue long polling watching.
					createLongPollingWatch();
					break;
				default:
					throw new IllegalStateException(
							String.format("Internal error! No support response httpStatus for %s", resp.getStatusCode()));
				}
			}
		} else if (log.isDebugEnabled()) {
			log.debug("Skip the watch request in long-polling!");
		}

	}

}