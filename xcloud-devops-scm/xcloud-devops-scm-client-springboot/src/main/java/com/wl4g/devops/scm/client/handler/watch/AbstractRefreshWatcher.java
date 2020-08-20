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
package com.wl4g.devops.scm.client.handler.watch;

import com.wl4g.components.core.bean.scm.model.GetRelease;
import com.wl4g.components.core.utils.bean.BeanMapConvert;
import com.wl4g.components.common.task.RunnerProperties;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.handler.locator.ScmPropertySourceLocator;
import com.wl4g.devops.scm.client.handler.refresh.ScmContextRefresher;
import com.wl4g.components.support.task.ApplicationTaskRunner;

import static com.wl4g.devops.scm.common.config.SCMConstants.URI_S_BASE;
import static com.wl4g.devops.scm.common.config.SCMConstants.URI_S_SOURCE_WATCH;

import org.springframework.util.Assert;

/**
 * Abstract refresh watcher.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年10月20日
 * @since
 * @see {@link org.springframework.cloud.zookeeper.config.ConfigWatcher
 *      ConfigWatcher}
 */
public abstract class AbstractRefreshWatcher extends ApplicationTaskRunner<RunnerProperties> {

	/** SCM client configuration */
	final protected ScmClientProperties config;

	/** SCM context refresher. */
	final protected ScmContextRefresher refresher;

	/** SCM property sources remote locator. */
	final protected ScmPropertySourceLocator locator;

	public AbstractRefreshWatcher(ScmClientProperties config, ScmContextRefresher refresher, ScmPropertySourceLocator locator) {
		super(new RunnerProperties(-1, 0, 0)); // disable worker group
		Assert.notNull(config, "Config must not be null");
		Assert.notNull(refresher, "Refresher must not be null");
		Assert.notNull(locator, "Locator must not be null");
		this.config = config;
		this.refresher = refresher;
		this.locator = locator;
	}

	protected String getWatchingUrl(boolean validate) {
		String uri = locator.getConfig().getBaseUri() + URI_S_BASE + "/" + URI_S_SOURCE_WATCH;

		// Create releaseGet
		WatchCommand get = new WatchCommand(locator.getInfo().getAppName(), config.getProfiles(), null,
				locator.getInfo().getConfigNode());
		return (uri + "?" + new BeanMapConvert(get).toUriParmaters());
	}

}