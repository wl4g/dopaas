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

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;

import java.util.concurrent.atomic.AtomicBoolean;

import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.coss.server.config.ChannelServerProperties;

/**
 * {@link CossServer}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月17日
 * @since
 */
public abstract class CossServer {

	final protected SmartLogger log = getLogger(getClass());

	/** Started flag */
	final private AtomicBoolean running = new AtomicBoolean(false);

	/** {@link ChannelServerProperties} */
	final protected ChannelServerProperties config;

	public CossServer(ChannelServerProperties config) {
		notNullOf(config, "config");
		this.config = config;
	}

	/**
	 * Start chanel bind.
	 */
	public void start() {
		if (running.compareAndSet(false, true)) {
			new Thread(() -> doStartBind()).start();
		} else {
			log.info("Skip, already bind channel coss server on ({}:{}).", config.getInetHost(), config.getInetPort());
		}
	}

	/**
	 * Do start bind
	 */
	protected abstract void doStartBind();

}