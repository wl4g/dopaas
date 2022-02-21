/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
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
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.dopaas.uds.service.elasticjoblite;

import java.util.Collection;

import org.apache.shardingsphere.elasticjob.infra.pojo.JobConfigurationPOJO;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.JobBriefInfo;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.ServerBriefInfo;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.ShardingInfo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * {@link JobAPIServiceImpl}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-04-29
 * @sine v1.0
 * @see
 */
public class JobAPIServiceImpl implements JobAPIService {

	private @Autowired InternalJobAPIHandler jobAPIHandler;

	// --- Job configuration. ---

	@Override
	public JobConfigurationPOJO getJobConfiguration(String jobName) {
		return jobAPIHandler.getJobConfigurationAPI().getJobConfiguration(jobName);
	}

	@Override
	public void updateJobConfiguration(JobConfigurationPOJO jobConfig) {
		jobAPIHandler.getJobConfigurationAPI().updateJobConfiguration(jobConfig);
	}

	@Override
	public void removeJobConfiguration(String jobName) {
		jobAPIHandler.getJobConfigurationAPI().removeJobConfiguration(jobName);
	}

	// --- Job Statistics. ---

	@Override
	public int getJobsTotalCount() {
		return jobAPIHandler.getJobStatisticsAPI().getJobsTotalCount();
	}

	@Override
	public Collection<JobBriefInfo> getAllJobsBriefInfo() {
		return jobAPIHandler.getJobStatisticsAPI().getAllJobsBriefInfo();
	}

	@Override
	public JobBriefInfo getJobBriefInfo(String jobName) {
		return jobAPIHandler.getJobStatisticsAPI().getJobBriefInfo(jobName);
	}

	@Override
	public Collection<JobBriefInfo> getJobsBriefInfo(String ip) {
		return jobAPIHandler.getJobStatisticsAPI().getJobsBriefInfo(ip);
	}

	// --- Job operators. ---

	@Override
	public void triggerJob(String jobName) {
		jobAPIHandler.getJobOperatorAPI().trigger(jobName);
	}

	@Override
	public void disableJob(String jobName, String serverIp) {
		jobAPIHandler.getJobOperatorAPI().disable(jobName, serverIp);
	}

	@Override
	public void enableJob(String jobName, String serverIp) {
		jobAPIHandler.getJobOperatorAPI().enable(jobName, serverIp);
	}

	@Override
	public void shutdownJob(String jobName, String serverIp) {
		jobAPIHandler.getJobOperatorAPI().shutdown(jobName, serverIp);
	}

	@Override
	public void removeJob(String jobName, String serverIp) {
		jobAPIHandler.getJobOperatorAPI().remove(jobName, serverIp);
	}

	// --- Sharding operators. ---

	@Override
	public void disableSharding(String jobName, String item) {
		jobAPIHandler.getShardingOperateAPI().disable(jobName, item);
	}

	@Override
	public void enableSharding(String jobName, String item) {
		jobAPIHandler.getShardingOperateAPI().enable(jobName, item);
	}

	// --- Server statistics. ---

	@Override
	public int getServersTotalCount() {
		return jobAPIHandler.getServerStatisticsAPI().getServersTotalCount();
	}

	@Override
	public Collection<ServerBriefInfo> getAllServersBriefInfo() {
		return jobAPIHandler.getServerStatisticsAPI().getAllServersBriefInfo();
	}

	// --- Sharding statistics. ---

	@Override
	public Collection<ShardingInfo> getShardingInfo(String jobName) {
		return jobAPIHandler.getShardingStatisticsAPI().getShardingInfo(jobName);
	}

}
