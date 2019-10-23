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
package com.wl4g.devops.ci.pipeline.job;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.pipeline.PipelineProvider;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.ci.TaskHistoryDetail;
import com.wl4g.devops.common.bean.share.AppInstance;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract deployments pipeline job.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年05月23日
 * @since
 * @param <P>
 */
public abstract class AbstractPipelineJob<P extends PipelineProvider> implements Runnable {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** Pipeline CICD properties configuration. */
	final protected CiCdProperties config;

	/** Pipeline provider. */
	final protected P provider;

	/** Pipeline application instance. */
	final protected AppInstance instance;

	/** Pipeline application project. */
	final protected Project project;

	/** Pipeline taskDetailId. */
	protected Integer taskDetailId;

	public AbstractPipelineJob(CiCdProperties config, P provider, Project project, AppInstance instance,
			List<TaskHistoryDetail> taskHistoryDetails) {
		notNull(config, "Pipeline config must not be null.");
		notNull(provider, "Pipeline provider must not be null.");
		notNull(project, "Pipeline job project must not be null.");
		notNull(instance, "Pipeline job instance must not be null.");
		notEmpty(taskHistoryDetails, "Pipeline task historyDetails must not be null.");
		this.config = config;
		this.provider = provider;
		this.project = project;
		this.instance = instance;

		// Task detail.
		Optional<TaskHistoryDetail> taskHisyDetail = taskHistoryDetails.stream()
				.filter(detail -> detail.getInstanceId().intValue() == instance.getId().intValue()).findFirst();
		isTrue(taskHisyDetail.isPresent(), "Not found taskDetailId by details.");
		this.taskDetailId = taskHisyDetail.get().getId();
	}

}