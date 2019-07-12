package com.wl4g.devops.umc.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import static com.dangdang.ddframe.job.lite.config.LiteJobConfiguration.*;
import static com.dangdang.ddframe.job.config.JobCoreConfiguration.*;

import com.wl4g.devops.common.annotation.Unused;
import com.wl4g.devops.umc.fetch.ServiceIndicatorsMetaFetcher;
import com.wl4g.devops.umc.fetch.IndicatorsMetaFetcher;
import com.wl4g.devops.umc.fetch.IndicatorsMetaInfo;
import com.wl4g.devops.umc.watch.ServiceIndicatorsStateWatcher;
import com.wl4g.devops.umc.watch.WatchJobListener;
import com.wl4g.devops.umc.watch.WatchScheduler;

/**
 * UMC watching auto configuration.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月4日
 * @since
 */
public class UmcWatchAutoConfiguration {

	final public static String KEY_WATCH_PREFIX = "spring.cloud.devops.umc.watch";

	@Bean
	@ConfigurationProperties(prefix = KEY_WATCH_PREFIX)
	public WatchProperties watchProperties() {
		return new WatchProperties();
	}

	//
	// Elastic job
	//

	@Bean(initMethod = "init")
	public ZookeeperRegistryCenter regCenter(WatchProperties config) {
		return new ZookeeperRegistryCenter(new ZookeeperConfiguration(config.getZkServers(), config.getNamespace()));
	}

	@Bean
	public ServiceIndicatorsStateWatcher applicationIndicatorsStateWatcher() {
		return new ServiceIndicatorsStateWatcher();
	}

	@Bean
	public WatchJobListener watchJobListener() {
		return new WatchJobListener();
	}

	@Bean
	public JobEventConfiguration jobEventConfiguration(DataSource dataSource) {
		return new JobEventRdbConfiguration(dataSource);
	}

	@Bean(initMethod = "init")
	public JobScheduler watchScheduler(WatchProperties config, JobEventConfiguration eventConfig,
			ServiceIndicatorsStateWatcher job, ZookeeperRegistryCenter regCenter) {
		LiteJobConfiguration jobConfig = getDataflowLiteJobConfiguration(job.getClass(), config.getCron(), config.getTotalCount(),
				config.getItemParams());
		return new WatchScheduler(job, regCenter, jobConfig, eventConfig, watchJobListener());
	}

	@Unused
	private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron,
			final int shardingTotalCount, final String shardingItemParameters) {
		return LiteJobConfiguration
				.newBuilder(new SimpleJobConfiguration(newBuilder(jobClass.getName(), cron, shardingTotalCount)
						.shardingItemParameters(shardingItemParameters).build(), jobClass.getCanonicalName()))
				.overwrite(true).build();
	}

	private LiteJobConfiguration getDataflowLiteJobConfiguration(Class<? extends DataflowJob<IndicatorsMetaInfo>> jobClass,
			String cron, int shardingTotalCount, String shardingItemParameters) {
		return newBuilder(new DataflowJobConfiguration(
				newBuilder(jobClass.getName(), cron, shardingTotalCount).shardingItemParameters(shardingItemParameters).build(),
				jobClass.getCanonicalName(), true)).overwrite(true).build();
	}

	//
	// Fetcher
	//

	@Bean
	@ConditionalOnMissingBean(IndicatorsMetaFetcher.class)
	public IndicatorsMetaFetcher serviceIndicatorsMetaFetcher() {
		return new ServiceIndicatorsMetaFetcher();
	}

}
