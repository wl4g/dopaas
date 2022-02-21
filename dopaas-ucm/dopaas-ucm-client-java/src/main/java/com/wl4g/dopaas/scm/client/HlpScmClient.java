/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.scm.client;

import com.wl4g.dopaas.scm.client.config.ScmClientProperties;
import com.wl4g.dopaas.scm.client.event.ConfigEventListener;
import com.wl4g.dopaas.scm.client.repository.RefreshRecordsRepository;
import com.wl4g.dopaas.scm.client.watch.LongPollingRefreshWatcher;

/**
 * {@link HlpScmClient}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-18
 * @since
 */
class HlpScmClient extends GenericScmClient {

	public HlpScmClient(ScmClientProperties<?> config, RefreshRecordsRepository repository, ConfigEventListener... listeners) {
		super(new LongPollingRefreshWatcher(config, repository, listeners));
	}

	@Override
	public void start() throws Exception {
		log.info("Starting scm hlp client watcher ...");
		watcher.start();
	}

}