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
