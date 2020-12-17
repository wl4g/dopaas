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
package com.wl4g.devops.umc.config;

import com.wl4g.component.common.task.RunnerProperties;

/**
 * Alarm properties.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class AlarmProperties extends RunnerProperties {
	private static final long serialVersionUID = -3690593536229115411L;

	public AlarmProperties() {
		// No need to start the asynchronous running of the boss.
		setAsyncStartup(false);
		setConcurrency(1); // By default
	}

}