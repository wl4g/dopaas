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
package com.wl4g.devops.coss.server.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.wl4g.devops.coss.server.CossServer;
import com.wl4g.devops.coss.server.CossServerBootsstrap;
import com.wl4g.devops.coss.server.NettyCossServer;
import com.wl4g.devops.coss.server.handler.CossActionProvider;
import com.wl4g.devops.coss.server.handler.DefaultCossActionProvider;

/**
 * {@link ChannelServerAutoConfiguration}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月17日
 * @since
 */
@Configuration
public class ChannelServerAutoConfiguration {

	@Bean
	public ChannelServerProperties channelServerProperties() {
		return new ChannelServerProperties();
	}

	@Bean
	@Primary
	public CossServer nettyCossServer(ChannelServerProperties config) {
		return new NettyCossServer(config);
	}

	@Bean
	public CossServerBootsstrap cossServerBootsstrap(ChannelServerProperties config, List<CossServer> servers) {
		return new CossServerBootsstrap(config, servers);
	}

	@Bean
	public CossActionProvider defaultCossActionProvider() {
		return new DefaultCossActionProvider();
	}

}