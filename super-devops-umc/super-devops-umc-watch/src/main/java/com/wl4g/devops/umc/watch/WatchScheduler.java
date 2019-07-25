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
package com.wl4g.devops.umc.watch;

import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;

/**
 * Indicators watching scanner job scheduler.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月4日
 * @since
 */
public class WatchScheduler extends SpringJobScheduler {

	public WatchScheduler(ElasticJob elasticJob, CoordinatorRegistryCenter regCenter, LiteJobConfiguration jobConfig,
			JobEventConfiguration jobEventConfig, ElasticJobListener... elasticJobListeners) {
		super(elasticJob, regCenter, jobConfig, jobEventConfig, elasticJobListeners);
	}

}