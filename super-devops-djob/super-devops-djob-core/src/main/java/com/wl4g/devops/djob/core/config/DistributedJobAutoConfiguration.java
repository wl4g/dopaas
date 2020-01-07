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