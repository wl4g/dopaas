package com.wl4g.devops.djob.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.wl4g.devops.djob.core.configure.DefaultElasticJobConfigurer;

/**
 * 任务自动配置
 * 
 * @author yinjihuan
 *
 * @about http://cxytiandi.com/about
 */
@Configuration
@EnableConfigurationProperties(SchedulerProperties.class)
public class DistributedJobAutoConfiguration {

	@Autowired
	private SchedulerProperties zookeeperProperties;

	/**
	 * 初始化Zookeeper注册中心
	 * 
	 * @return
	 */
	@Bean(initMethod = "init")
	public ZookeeperRegistryCenter zookeeperRegistryCenter() {
		ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(zookeeperProperties.getZkServers(),
				zookeeperProperties.getNamespace());
		zkConfig.setBaseSleepTimeMilliseconds(zookeeperProperties.getBaseSleepTimeMilliseconds());
		zkConfig.setConnectionTimeoutMilliseconds(zookeeperProperties.getConnectionTimeoutMilliseconds());
		zkConfig.setDigest(zookeeperProperties.getDigest());
		zkConfig.setMaxRetries(zookeeperProperties.getMaxRetries());
		zkConfig.setMaxSleepTimeMilliseconds(zookeeperProperties.getMaxSleepTimeMilliseconds());
		zkConfig.setSessionTimeoutMilliseconds(zookeeperProperties.getSessionTimeoutMilliseconds());
		return new ZookeeperRegistryCenter(zkConfig);
	}

	@Bean
	public DefaultElasticJobConfigurer jobConfParser() {
		return new DefaultElasticJobConfigurer();
	}

}
