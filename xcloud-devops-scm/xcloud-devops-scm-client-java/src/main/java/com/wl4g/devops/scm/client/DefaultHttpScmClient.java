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
package com.wl4g.devops.scm.client;

import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.event.ScmEventListener;
import com.wl4g.devops.scm.client.event.support.ScmEventPublisher;
import com.wl4g.devops.scm.client.event.support.ScmEventSubscriber;
import com.wl4g.devops.scm.client.refresh.LongHttpRefreshWatcher;

/**
 * {@link DefaultHttpScmClient}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-18
 * @since
 */
public class DefaultHttpScmClient extends GenericScmClient {

	public DefaultHttpScmClient(ScmClientProperties config, ScmEventListener... listeners) {
		super(new LongHttpRefreshWatcher(config, new ScmEventPublisher(), new ScmEventSubscriber(listeners)));
	}

	@Override
	public void start() throws Exception {
		log.info("Starting scm client watcher ...");
		watcher.start();
	}

}
