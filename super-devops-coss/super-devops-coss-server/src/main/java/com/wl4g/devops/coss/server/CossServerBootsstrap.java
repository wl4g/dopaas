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
package com.wl4g.devops.coss.server;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notEmptyOf;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.coss.server.config.ChannelServerProperties;

/**
 * {@link CossServerBootsstrap}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月17日
 * @since
 */
public class CossServerBootsstrap implements ApplicationRunner {

	final protected SmartLogger log = getLogger(getClass());

	/** {@link CossServer} list. */
	final protected List<CossServer> servers;

	/** {@link ChannelServerProperties} */
	final protected ChannelServerProperties config;

	public CossServerBootsstrap(ChannelServerProperties config, List<CossServer> servers) {
		notNullOf(config, "config");
		notEmptyOf(servers, "servers");
		this.servers = servers;
		this.config = config;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// Startup all servers.
		doStartupServers();
	}

	/**
	 * Do startup servers.
	 */
	private void doStartupServers() {
		for (CossServer server : servers) {
			server.start();
		}

	}

}