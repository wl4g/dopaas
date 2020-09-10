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
package com.wl4g.devops.ci.tool;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.wl4g.devops.ci.config.CiProperties;

/**
 * Abstract generic basic operator tools.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-12-11
 * @since
 */
public abstract class GenericOperatorTool implements ApplicationRunner, Runnable {

	final private AtomicBoolean running = new AtomicBoolean(false);

	@Autowired
	protected CiProperties config;

	@Autowired
	protected ThreadPoolTaskScheduler taskScheduler;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (running.compareAndSet(false, true)) {
			doStartup(taskScheduler.getScheduledExecutor());
		}
	}

	/**
	 * Do startup operator tools.
	 * 
	 * @param scheduler
	 */
	protected abstract void doStartup(ScheduledExecutorService scheduler);

}