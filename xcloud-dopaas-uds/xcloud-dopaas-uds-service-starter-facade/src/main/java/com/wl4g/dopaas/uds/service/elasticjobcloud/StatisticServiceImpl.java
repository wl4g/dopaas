/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.dopaas.uds.service.elasticjobcloud;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.shardingsphere.elasticjob.cloud.config.CloudJobExecutionType;
import org.apache.shardingsphere.elasticjob.cloud.config.pojo.CloudJobConfigurationPOJO;
import org.apache.shardingsphere.elasticjob.cloud.statistics.StatisticInterval;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.job.JobExecutionTypeStatistics;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.job.JobRegisterStatistics;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.job.JobRunningStatistics;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.task.TaskResultStatistics;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.task.TaskRunningStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.dopaas.uds.service.elasticjobcloud.StatisticService;
import com.wl4g.dopaas.uds.service.elasticjobcloud.repository.StatisticRdbRepository;

/**
 * Statistic manager.
 */
@Service
public final class StatisticServiceImpl implements StatisticService {

	private @Autowired CloudJobConfigServiceImpl configurationService;

	private @Autowired StatisticRdbRepository rdbRepository;

	/**
	 * Get statistic of the recent week.
	 * 
	 * @return task result statistic
	 */
	@Override
	public TaskResultStatistics getTaskResultStatisticsWeekly() {
		if (!rdbRepository.isEnable()) {
			return new TaskResultStatistics(0, 0, StatisticInterval.DAY, new Date());
		}
		return rdbRepository.getSummedTaskResultStatistics(getStatisticTime(StatisticInterval.DAY, -7), StatisticInterval.DAY);
	}

	/**
	 * Get statistic since online.
	 * 
	 * @return task result statistic
	 */
	@Override
	public TaskResultStatistics getTaskResultStatisticsSinceOnline() {
		if (!rdbRepository.isEnable()) {
			return new TaskResultStatistics(0, 0, StatisticInterval.DAY, new Date());
		}
		return rdbRepository.getSummedTaskResultStatistics(getOnlineDate(), StatisticInterval.DAY);
	}

	/**
	 * Get the latest statistic of the specified interval.
	 * 
	 * @param statisticInterval
	 *            statistic interval
	 * @return task result statistic
	 */
	@Override

	public TaskResultStatistics findLatestTaskResultStatistics(final StatisticInterval statisticInterval) {
		if (rdbRepository.isEnable()) {
			Optional<TaskResultStatistics> result = rdbRepository.findLatestTaskResultStatistics(statisticInterval);
			if (result.isPresent()) {
				return result.get();
			}
		}
		return new TaskResultStatistics(0, 0, statisticInterval, new Date());
	}

	/**
	 * Get statistic of the recent day.
	 * 
	 * @return task result statistic
	 */
	@Override

	public List<TaskResultStatistics> findTaskResultStatisticsDaily() {
		if (!rdbRepository.isEnable()) {
			return Collections.emptyList();
		}
		return rdbRepository.findTaskResultStatistics(getStatisticTime(StatisticInterval.HOUR, -24), StatisticInterval.MINUTE);
	}

	/**
	 * Get job execution type statistics.
	 * 
	 * @return Job execution type statistics data object
	 */
	@Override
	public JobExecutionTypeStatistics getJobExecutionTypeStatistics() {
		int transientJobCnt = 0;
		int daemonJobCnt = 0;
		for (CloudJobConfigurationPOJO each : configurationService.loadAll()) {
			if (CloudJobExecutionType.TRANSIENT.equals(each.getJobExecutionType())) {
				transientJobCnt++;
			} else if (CloudJobExecutionType.DAEMON.equals(each.getJobExecutionType())) {
				daemonJobCnt++;
			}
		}
		return new JobExecutionTypeStatistics(transientJobCnt, daemonJobCnt);
	}

	/**
	 * Get the collection of task statistics in the most recent week.
	 * 
	 * @return Collection of running task statistics data objects
	 */
	@Override

	public List<TaskRunningStatistics> findTaskRunningStatisticsWeekly() {
		if (!rdbRepository.isEnable()) {
			return Collections.emptyList();
		}
		return rdbRepository.findTaskRunningStatistics(getStatisticTime(StatisticInterval.DAY, -7));
	}

	/**
	 * Get the collection of job statistics in the most recent week.
	 * 
	 * @return collection of running task statistics data objects
	 */
	@Override
	public List<JobRunningStatistics> findJobRunningStatisticsWeekly() {
		if (!rdbRepository.isEnable()) {
			return Collections.emptyList();
		}
		return rdbRepository.findJobRunningStatistics(getStatisticTime(StatisticInterval.DAY, -7));
	}

	/**
	 * Get running task statistics data collection since online.
	 * 
	 * @return collection of running task statistics data objects
	 */
	@Override
	public List<JobRegisterStatistics> findJobRegisterStatisticsSinceOnline() {
		if (!rdbRepository.isEnable()) {
			return Collections.emptyList();
		}
		return rdbRepository.findJobRegisterStatistics(getOnlineDate());
	}

	private static Date getOnlineDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return formatter.parse("2016-12-16");
		} catch (final ParseException ex) {
			return null;
		}
	}

	/**
	 * Get the statistical time with the interval unit.
	 *
	 * @param interval
	 *            interval
	 * @param offset
	 *            offset
	 * @return Date
	 */
	private static Date getStatisticTime(final StatisticInterval interval, final int offset) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		switch (interval) {
		case DAY:
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.add(Calendar.DATE, offset);
			break;
		case HOUR:
			calendar.set(Calendar.MINUTE, 0);
			calendar.add(Calendar.HOUR_OF_DAY, offset);
			break;
		case MINUTE:
		default:
			calendar.add(Calendar.MINUTE, offset);
			break;
		}
		return calendar.getTime();
	}

}
