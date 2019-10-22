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
package com.wl4g.devops.ci.pipeline.handler;

import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.share.AppInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract deployments task.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月25日
 * @since
 */
public abstract class AbstractPipelineHandler implements Runnable {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	final protected AppInstance instance;
	final protected Project project;

	public AbstractPipelineHandler(AppInstance instance, Project project) {
		this.instance = instance;
		this.project = project;
	}

}