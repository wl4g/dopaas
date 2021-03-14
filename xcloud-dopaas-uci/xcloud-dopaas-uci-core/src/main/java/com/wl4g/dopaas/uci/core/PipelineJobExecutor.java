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
package com.wl4g.dopaas.uci.core;

import com.wl4g.component.common.task.RunnerProperties;
import com.wl4g.component.support.task.ApplicationTaskRunner;
import com.wl4g.dopaas.uci.config.CiProperties;

/**
 * Pipeline job executor runner.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-13
 * @since
 */
public class PipelineJobExecutor extends ApplicationTaskRunner<RunnerProperties> {

	final protected CiProperties config;

	public PipelineJobExecutor(CiProperties config) {
		super(config.getExecutor());
		this.config = config;
	}

}