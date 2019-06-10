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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.wl4g.devops.scm.client.configure.ScmPropertySourceLocator;
import com.wl4g.devops.scm.client.configure.refresh.ScmContextRefresher;

/**
 * Abstract refresh watcher.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年10月20日
 * @since
 * @see {@link org.springframework.cloud.zookeeper.config.ConfigWatcher
 *      ConfigWatcher}
 */
public abstract class AbstractRefreshWatcher implements Runnable {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** Scm context refresher. */
	final protected ScmContextRefresher refresher;

	/** Scm property sources remote locator. */
	final protected ScmPropertySourceLocator locator;

	public AbstractRefreshWatcher(ScmContextRefresher refresher, ScmPropertySourceLocator locator) {
		Assert.notNull(refresher, "Refresher must not be null");
		Assert.notNull(locator, "locator must not be null");
		this.refresher = refresher;
		this.locator = locator;
	}

}