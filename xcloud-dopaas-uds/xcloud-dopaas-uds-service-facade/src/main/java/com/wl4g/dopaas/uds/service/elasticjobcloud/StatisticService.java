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
package com.wl4g.dopaas.uds.service.elasticjobcloud;

import java.util.List;

import org.apache.shardingsphere.elasticjob.cloud.statistics.StatisticInterval;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.job.JobExecutionTypeStatistics;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.job.JobRegisterStatistics;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.job.JobRunningStatistics;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.task.TaskResultStatistics;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.task.TaskRunningStatistics;

/**
 * {@link StatisticService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-03-22
 * @sine v1.0
 * @see
 */
public interface StatisticService {

	TaskResultStatistics getTaskResultStatisticsWeekly();

	/**
	 * Get statistic since online.
	 * 
	 * @return task result statistic
	 */
	TaskResultStatistics getTaskResultStatisticsSinceOnline();

	/**
	 * Get the latest statistic of the specified interval.
	 * 
	 * @param statisticInterval
	 *            statistic interval
	 * @return task result statistic
	 */
	TaskResultStatistics findLatestTaskResultStatistics(final StatisticInterval statisticInterval);

	/**
	 * Get statistic of the recent day.
	 * 
	 * @return task result statistic
	 */
	List<TaskResultStatistics> findTaskResultStatisticsDaily();

	/**
	 * Get job execution type statistics.
	 * 
	 * @return Job execution type statistics data object
	 */
	JobExecutionTypeStatistics getJobExecutionTypeStatistics();

	/**
	 * Get the collection of task statistics in the most recent week.
	 * 
	 * @return Collection of running task statistics data objects
	 */
	List<TaskRunningStatistics> findTaskRunningStatisticsWeekly();

	/**
	 * Get the collection of job statistics in the most recent week.
	 * 
	 * @return collection of running task statistics data objects
	 */
	List<JobRunningStatistics> findJobRunningStatisticsWeekly();

	/**
	 * Get running task statistics data collection since online.
	 * 
	 * @return collection of running task statistics data objects
	 */
	List<JobRegisterStatistics> findJobRegisterStatisticsSinceOnline();

}
