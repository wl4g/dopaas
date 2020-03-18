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
package com.wl4g.devops.djob.core.configure;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties.JobPropertiesEnum;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.wl4g.devops.djob.core.scheduler.ElasticJobManager;
import com.wl4g.devops.djob.core.annotation.ElasticJobConf;
import com.wl4g.devops.djob.core.job.JobAttributeDefine;
import com.wl4g.devops.djob.core.job.JobTypeDefine;

import static org.springframework.core.annotation.AnnotationUtils.*;
import static com.wl4g.devops.djob.core.job.JobAttributeDefine.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Parsing task information initialization from annotations.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月12日
 * @since
 */
public class DefaultElasticJobConfigurer extends AbstractDjobConfigurer {

	@Autowired
	private ZookeeperRegistryCenter zkRegistryCenter;

	@Autowired(required = false)
	private ElasticJobManager elasticJobManager;

	private String prefix = "elastic.job.";

	@Override
	public void doConfigurer() throws Exception {
		for (Object confBean : actx.getBeansWithAnnotation(ElasticJobConf.class).values()) {
			Class<?> cls = confBean.getClass();
			// 解决CGLIB代理问题
			String jobTypeName = cls.getInterfaces()[0].getSimpleName();
			if (!JobTypeDefine.getJobTypes().contains(jobTypeName)) {
				jobTypeName = cls.getSuperclass().getInterfaces()[0].getSimpleName();
				cls = cls.getSuperclass();
			}
			ElasticJobConf conf = findAnnotation(cls, ElasticJobConf.class);

			String jobClass = cls.getName();
			String jobName = conf.name();
			String cron = getEnvironmentString(jobName, CRON, conf.cron());
			String shardingItemParameters = getEnvironmentString(jobName, SHARDING_ITEM_PARAMETERS,
					conf.shardingItemParameters());
			String description = getEnvironmentString(jobName, DESCRIPTION, conf.description());
			String jobParameter = getEnvironmentString(jobName, JOB_PARAMETER, conf.jobParameter());
			String jobExceptionHandler = getEnvironmentString(jobName, JOB_EXCEPTION_HANDLER, conf.jobExceptionHandler());
			String executorServiceHandler = getEnvironmentString(jobName, EXECUTOR_SERVICE_HANDLER,
					conf.executorServiceHandler());

			String jobShardingStrategyClass = getEnvironmentString(jobName, JOB_SHARDING_STRATEGY_CLASS,
					conf.jobShardingStrategyClass());
			String eventTraceRdbDataSource = getEnvironmentString(jobName, EVENT_TRACE_RDB_DATA_SOURCE,
					conf.eventTraceRdbDataSource());
			String scriptCommandLine = getEnvironmentString(jobName, SCRIPT_COMMAND_LINE, conf.scriptCommandLine());

			boolean failover = getEnvironmentBoolean(jobName, FAILOVER, conf.failover());
			boolean misfire = getEnvironmentBoolean(jobName, MISFIRE, conf.misfire());
			boolean overwrite = getEnvironmentBoolean(jobName, OVERWRITE, conf.overwrite());
			boolean disabled = getEnvironmentBoolean(jobName, DISABLED, conf.disabled());
			boolean monitorExecution = getEnvironmentBoolean(jobName, MONITOR_EXECUTION, conf.monitorExecution());
			boolean streamingProcess = getEnvironmentBoolean(jobName, STREAMING_PROCESS, conf.streamingProcess());

			int shardingTotalCount = getEnvironmentInt(jobName, SHARDING_TOTAL_COUNT, conf.shardingTotalCount());
			int monitorPort = getEnvironmentInt(jobName, MONITOR_PORT, conf.monitorPort());
			int maxTimeDiffSeconds = getEnvironmentInt(jobName, MAX_TIME_DIFF_SECONDS, conf.maxTimeDiffSeconds());
			int reconcileIntervalMinutes = getEnvironmentInt(jobName, RECONCILE_INTERVAL_MINUTES,
					conf.reconcileIntervalMinutes());

			// 核心配置
			JobCoreConfiguration coreConfig = JobCoreConfiguration.newBuilder(jobName, cron, shardingTotalCount)
					.shardingItemParameters(shardingItemParameters).description(description).failover(failover)
					.jobParameter(jobParameter).misfire(misfire)
					.jobProperties(JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), jobExceptionHandler)
					.jobProperties(JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), executorServiceHandler).build();

			// 不同类型的任务配置处理
			LiteJobConfiguration jobConfig = null;
			JobTypeConfiguration typeConfig = null;
			if (jobTypeName.equals("SimpleJob")) {
				typeConfig = new SimpleJobConfiguration(coreConfig, jobClass);
			} else if (jobTypeName.equals("DataflowJob")) {
				typeConfig = new DataflowJobConfiguration(coreConfig, jobClass, streamingProcess);
			} else if (jobTypeName.equals("ScriptJob")) {
				typeConfig = new ScriptJobConfiguration(coreConfig, scriptCommandLine);
			}

			jobConfig = LiteJobConfiguration.newBuilder(typeConfig).overwrite(overwrite).disabled(disabled)
					.monitorPort(monitorPort).monitorExecution(monitorExecution).maxTimeDiffSeconds(maxTimeDiffSeconds)
					.jobShardingStrategyClass(jobShardingStrategyClass).reconcileIntervalMinutes(reconcileIntervalMinutes)
					.build();

			List<BeanDefinition> elasticJobListeners = getTargetElasticJobListeners(conf);

			// 构建SpringJobScheduler对象来初始化任务
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
			factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
			if ("ScriptJob".equals(jobTypeName)) {
				factory.addConstructorArgValue(null);
			} else {
				factory.addConstructorArgValue(confBean);
			}
			factory.addConstructorArgValue(zkRegistryCenter);
			factory.addConstructorArgValue(jobConfig);

			// 任务执行日志数据源，以名称获取
			if (!isBlank(eventTraceRdbDataSource)) {
				BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(JobEventRdbConfiguration.class);
				rdbFactory.addConstructorArgReference(eventTraceRdbDataSource);
				factory.addConstructorArgValue(rdbFactory.getBeanDefinition());
			}
			factory.addConstructorArgValue(elasticJobListeners);

			// Register elastic job scheduler.
			((SpringJobScheduler) registerBean(factory, jobName + "SpringJobScheduler")).init();
			if (log.isInfoEnabled()) {
				log.info("【" + jobName + "】\t" + jobClass + "\tinit success");
			}

		}

		// 开启任务监听,当有任务添加时，监听zk中的数据增加，自动在其他节点也初始化该任务
		if (elasticJobManager != null) {
			elasticJobManager.monitorJobRegister();
		}

	}

	private List<BeanDefinition> getTargetElasticJobListeners(ElasticJobConf conf) {
		List<BeanDefinition> result = new ManagedList<BeanDefinition>(2);
		String listeners = getEnvironmentString(conf.name(), LISTENER, conf.listener());
		if (!isBlank(listeners)) {
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
			factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
			result.add(factory.getBeanDefinition());
		}

		String distributedListeners = getEnvironmentString(conf.name(), DISTRIBUTED_LISTENER, conf.distributedListener());
		long startedTimeoutMilliseconds = getEnvironmentLong(conf.name(), DISTRIBUTED_LISTENER_STARTED_TIMEOUT_MILLISECONDS,
				conf.startedTimeoutMilliseconds());
		long completedTimeoutMilliseconds = getEnvironmentLong(conf.name(), DISTRIBUTED_LISTENER_COMPLETED_TIMEOUT_MILLISECONDS,
				conf.completedTimeoutMilliseconds());

		if (!isBlank(distributedListeners)) {
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListeners);
			factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
			factory.addConstructorArgValue(startedTimeoutMilliseconds);
			factory.addConstructorArgValue(completedTimeoutMilliseconds);
			result.add(factory.getBeanDefinition());
		}
		return result;
	}

	/**
	 * Get the task property value in the configuration. Environment does not
	 * use the value in the annotation
	 * 
	 * @param jobName
	 *            Job name
	 * @param attrDefine
	 *            Attribute name definition
	 * @param defaultValue
	 *            default value
	 * @return
	 */
	private String getEnvironmentString(String jobName, JobAttributeDefine attrDefine, String defaultValue) {
		String key = prefix + jobName + "." + attrDefine.getAttributeName();
		String value = environment.getProperty(key);
		if (!isBlank(value)) {
			return value;
		}
		return defaultValue;
	}

	private int getEnvironmentInt(String jobName, JobAttributeDefine attrDefine, int defaultValue) {
		String key = prefix + jobName + "." + attrDefine.getAttributeName();
		String value = environment.getProperty(key);
		if (!isBlank(value)) {
			return Integer.valueOf(value);
		}
		return defaultValue;
	}

	private long getEnvironmentLong(String jobName, JobAttributeDefine attrDefine, long defaultValue) {
		String key = prefix + jobName + "." + attrDefine.getAttributeName();
		String value = environment.getProperty(key);
		if (!isBlank(value)) {
			return Long.valueOf(value);
		}
		return defaultValue;
	}

	private boolean getEnvironmentBoolean(String jobName, JobAttributeDefine attrDefine, boolean defaultValue) {
		String key = prefix + jobName + "." + attrDefine.getAttributeName();
		String value = environment.getProperty(key);
		if (!isBlank(value)) {
			return Boolean.valueOf(value);
		}
		return defaultValue;
	}

}