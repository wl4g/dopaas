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
package com.wl4g.dopaas.umc.watch;

import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.job.util.AopTargetUtils;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.google.common.base.Optional;

/**
 * Indicators watching scanner job scheduler.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月4日
 * @since
 * @see {@link com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler}
 */
public class WatchScheduler extends JobScheduler {

	private final ElasticJob elasticJob;

	public WatchScheduler(final ElasticJob elasticJob, final CoordinatorRegistryCenter regCenter,
			final LiteJobConfiguration jobConfig, final ElasticJobListener... elasticJobListeners) {
		super(regCenter, jobConfig, getTargetElasticJobListeners(elasticJobListeners));
		this.elasticJob = elasticJob;
	}

	public WatchScheduler(final ElasticJob elasticJob, final CoordinatorRegistryCenter regCenter,
			final LiteJobConfiguration jobConfig, final JobEventConfiguration jobEventConfig,
			final ElasticJobListener... elasticJobListeners) {
		super(regCenter, jobConfig, jobEventConfig, getTargetElasticJobListeners(elasticJobListeners));
		this.elasticJob = elasticJob;
	}

	private static ElasticJobListener[] getTargetElasticJobListeners(final ElasticJobListener[] elasticJobListeners) {
		final ElasticJobListener[] result = new ElasticJobListener[elasticJobListeners.length];
		for (int i = 0; i < elasticJobListeners.length; i++) {
			result[i] = (ElasticJobListener) AopTargetUtils.getTarget(elasticJobListeners[i]);
		}
		return result;
	}

	@Override
	protected Optional<ElasticJob> createElasticJobInstance() {
		return Optional.fromNullable(elasticJob);
	}

}