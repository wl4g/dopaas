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

package com.wl4g.dopaas.uds.elasticjobcloud.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.shardingsphere.elasticjob.cloud.config.CloudJobExecutionType;
import org.apache.shardingsphere.elasticjob.cloud.config.pojo.CloudJobConfigurationPOJO;
import org.apache.shardingsphere.elasticjob.cloud.statistics.StatisticInterval;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.job.JobExecutionTypeStatistics;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.job.JobRegisterStatistics;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.job.JobRunningStatistics;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.task.TaskResultStatistics;
import org.apache.shardingsphere.elasticjob.cloud.statistics.type.task.TaskRunningStatistics;
import org.apache.shardingsphere.elasticjob.infra.context.TaskContext;
import org.apache.shardingsphere.elasticjob.infra.exception.JobSystemException;
import org.apache.shardingsphere.elasticjob.tracing.event.JobExecutionEvent;
import org.apache.shardingsphere.elasticjob.tracing.event.JobStatusTraceEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.web.BaseController;
import com.wl4g.dopaas.uds.elasticjobcloud.service.FacadeService;
import com.wl4g.dopaas.uds.elasticjobcloud.service.job.CloudJobConfigurationService;
import com.wl4g.dopaas.uds.elasticjobcloud.service.producer.ProducerManager;
import com.wl4g.dopaas.uds.elasticjobcloud.service.state.failover.FailoverTaskInfo;
import com.wl4g.dopaas.uds.elasticjobcloud.service.statistics.StatisticManager;
import com.wl4g.dopaas.uds.elasticjobcloud.web.model.JobEventRdbSearch;

/**
 * Cloud job restful api.
 */
@RestController
@RequestMapping("/api/job")
public final class CloudJobController extends BaseController {

	@Autowired
	private JobEventRdbSearch jobEventRdbSearch;

	@Autowired
	private ProducerManager producerManager;

	@Autowired
	private CloudJobConfigurationService jobConfigService;

	@Autowired
	private FacadeService facadeService;

	@Autowired
	private StatisticManager statisticManager;

	/**
	 * Register cloud job.
	 * 
	 * @param cloudJobConfig
	 *            cloud job configuration
	 */
	@PostMapping("/register")
	public RespBase<Boolean> register(@RequestBody final CloudJobConfigurationPOJO cloudJobConfig) {
		RespBase<Boolean> resp = RespBase.create();
		producerManager.register(cloudJobConfig);
		return resp.withData(Boolean.TRUE);
	}

	/**
	 * Update cloud job.
	 * 
	 * @param cloudJobConfig
	 *            cloud job configuration
	 */
	@PutMapping("/update")
	public RespBase<Boolean> update(@RequestBody final CloudJobConfigurationPOJO cloudJobConfig) {
		RespBase<Boolean> resp = RespBase.create();
		producerManager.update(cloudJobConfig);
		return resp.withData(Boolean.TRUE);
	}

	/**
	 * Deregister cloud job.
	 * 
	 * @param jobName
	 *            job name
	 */
	@DeleteMapping("/{jobName}/deregister")
	public RespBase<Boolean> deregister(@PathVariable final String jobName) {
		RespBase<Boolean> resp = RespBase.create();
		producerManager.deregister(jobName);
		return resp.withData(Boolean.TRUE);
	}

	/**
	 * Check whether the cloud job is disabled or not.
	 * 
	 * @param jobName
	 *            job name
	 * @return true is disabled, otherwise not
	 */
	@GetMapping("/{jobName}/disable")
	public RespBase<Boolean> isDisabled(@PathVariable("jobName") final String jobName) {
		RespBase<Boolean> resp = RespBase.create();
		boolean result = facadeService.isJobDisabled(jobName);
		return resp.withData(result);
	}

	/**
	 * Enable cloud job.
	 * 
	 * @param jobName
	 *            job name
	 */
	@PostMapping("/{jobName}/enable")
	public RespBase<Boolean> enable(@PathVariable("jobName") final String jobName) {
		RespBase<Boolean> resp = RespBase.create();
		Optional<CloudJobConfigurationPOJO> configOptional = jobConfigService.load(jobName);
		if (configOptional.isPresent()) {
			facadeService.enableJob(jobName);
		}
		return resp.withData(Boolean.TRUE);
	}

	/**
	 * Disable cloud job.
	 * 
	 * @param jobName
	 *            job name
	 */
	@PostMapping("/{jobName}/disable")
	public RespBase<Boolean> disable(@PathVariable("jobName") final String jobName) {
		RespBase<Boolean> resp = RespBase.create();
		if (jobConfigService.load(jobName).isPresent()) {
			facadeService.disableJob(jobName);
		}
		return resp.withData(Boolean.TRUE);
	}

	/**
	 * Trigger job once.
	 * 
	 * @param jobName
	 *            job name
	 */
	@PostMapping("/trigger")
	public RespBase<Boolean> trigger(@RequestBody final String jobName) {
		RespBase<Boolean> resp = RespBase.create();
		Optional<CloudJobConfigurationPOJO> config = jobConfigService.load(jobName);
		if (config.isPresent() && CloudJobExecutionType.DAEMON == config.get().getJobExecutionType()) {
			throw new JobSystemException("Daemon job '%s' cannot support trigger.", jobName);
		}
		facadeService.addTransient(jobName);
		return resp.withData(Boolean.TRUE);
	}

	/**
	 * Query job detail.
	 * 
	 * @param jobName
	 *            job name
	 * @return the job detail
	 */
	@GetMapping("/jobs/{jobName}")
	public RespBase<CloudJobConfigurationPOJO> detail(@PathVariable("jobName") final String jobName) {
		RespBase<CloudJobConfigurationPOJO> resp = RespBase.create();
		Optional<CloudJobConfigurationPOJO> cloudJobConfig = jobConfigService.load(jobName);
		return resp.withData(cloudJobConfig.orElse(null));
	}

	/**
	 * Find all jobs.
	 * 
	 * @return all jobs
	 */
	@GetMapping("/jobs")
	public RespBase<Collection<CloudJobConfigurationPOJO>> findAllJobs() {
		RespBase<Collection<CloudJobConfigurationPOJO>> resp = RespBase.create();
		return resp.withData(jobConfigService.loadAll());
	}

	/**
	 * Find all running tasks.
	 * 
	 * @return all running tasks
	 */
	@GetMapping("tasks/running")
	public RespBase<Collection<TaskContext>> findAllRunningTasks() {
		RespBase<Collection<TaskContext>> resp = RespBase.create();
		List<TaskContext> result = new LinkedList<>();
		for (Set<TaskContext> each : facadeService.getAllRunningTasks().values()) {
			result.addAll(each);
		}
		return resp.withData(result);
	}

	/**
	 * Find all ready tasks.
	 * 
	 * @return collection of all ready tasks
	 */
	@GetMapping("tasks/ready")
	public RespBase<Collection<Map<String, String>>> findAllReadyTasks() {
		RespBase<Collection<Map<String, String>>> resp = RespBase.create();
		Map<String, Integer> readyTasks = facadeService.getAllReadyTasks();
		List<Map<String, String>> result = new ArrayList<>(readyTasks.size());
		for (Entry<String, Integer> each : readyTasks.entrySet()) {
			Map<String, String> oneTask = new HashMap<>(2, 1);
			oneTask.put("jobName", each.getKey());
			oneTask.put("times", String.valueOf(each.getValue()));
			result.add(oneTask);
		}
		return resp.withData(result);
	}

	/**
	 * Find all failover tasks.
	 * 
	 * @return collection of all the failover tasks
	 */
	@GetMapping("tasks/failover")
	public RespBase<Collection<FailoverTaskInfo>> findAllFailoverTasks() {
		RespBase<Collection<FailoverTaskInfo>> resp = RespBase.create();
		List<FailoverTaskInfo> result = new LinkedList<>();
		for (Collection<FailoverTaskInfo> each : facadeService.getAllFailoverTasks().values()) {
			result.addAll(each);
		}
		return resp.withData(result);
	}

	/**
	 * Find job execution events.
	 * 
	 * @param requestParams
	 *            request params
	 * @return job execution event
	 * @throws ParseException
	 *             parse exception
	 */
	@PostMapping("/events/executions")
	public RespBase<JobEventRdbSearch.Result<JobExecutionEvent>> findJobExecutionEvents(
			@RequestParam final MultiValueMap<String, String> requestParams) throws ParseException {
		RespBase<JobEventRdbSearch.Result<JobExecutionEvent>> resp = RespBase.create();
		if (!jobEventRdbSearch.isEnable()) {
			return resp.withData(new JobEventRdbSearch.Result<>(0, Collections.<JobExecutionEvent> emptyList()));
		}
		return resp.withData(jobEventRdbSearch
				.findJobExecutionEvents(buildCondition(requestParams, new String[] { "jobName", "taskId", "ip", "isSuccess" })));
	}

	/**
	 * Find job status trace events.
	 * 
	 * @param requestParams
	 *            request params
	 * @return job status trace event
	 * @throws ParseException
	 *             parse exception
	 */
	@PostMapping("/events/statusTraces")
	public RespBase<JobEventRdbSearch.Result<JobStatusTraceEvent>> findJobStatusTraceEvents(
			@RequestParam final MultiValueMap<String, String> requestParams) throws ParseException {
		RespBase<JobEventRdbSearch.Result<JobStatusTraceEvent>> resp = RespBase.create();
		if (!jobEventRdbSearch.isEnable()) {
			return resp.withData(new JobEventRdbSearch.Result<>(0, Collections.<JobStatusTraceEvent> emptyList()));
		}
		return resp.withData(jobEventRdbSearch.findJobStatusTraceEvents(buildCondition(requestParams,
				new String[] { "jobName", "taskId", "slaveId", "source", "executionType", "state" })));
	}

	private JobEventRdbSearch.Condition buildCondition(final MultiValueMap<String, String> requestParams, final String[] params)
			throws ParseException {
		int perPage = 10;
		int page = 1;
		if (!Strings.isNullOrEmpty(requestParams.getFirst("per_page"))) {
			perPage = Integer.parseInt(requestParams.getFirst("per_page"));
		}
		if (!Strings.isNullOrEmpty(requestParams.getFirst("page"))) {
			page = Integer.parseInt(requestParams.getFirst("page"));
		}
		String sort = requestParams.getFirst("sort");
		String order = requestParams.getFirst("order");
		Date startTime = null;
		Date endTime = null;
		Map<String, Object> fields = getQueryParameters(requestParams, params);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (!Strings.isNullOrEmpty(requestParams.getFirst("startTime"))) {
			startTime = simpleDateFormat.parse(requestParams.getFirst("startTime"));
		}
		if (!Strings.isNullOrEmpty(requestParams.getFirst("endTime"))) {
			endTime = simpleDateFormat.parse(requestParams.getFirst("endTime"));
		}
		return new JobEventRdbSearch.Condition(perPage, page, sort, order, startTime, endTime, fields);
	}

	private Map<String, Object> getQueryParameters(final MultiValueMap<String, String> requestParams, final String[] params) {
		final Map<String, Object> result = new HashMap<>();
		for (String each : params) {
			if (!Strings.isNullOrEmpty(requestParams.getFirst(each))) {
				result.put(each, requestParams.getFirst(each));
			}
		}
		return result;
	}

	/**
	 * Find task result statistics.
	 * 
	 * @param since
	 *            time span
	 * @return task result statistics
	 */
	@GetMapping("/statistics/tasks/results")
	public RespBase<List<TaskResultStatistics>> findTaskResultStatistics(
			@RequestParam(value = "since", required = false) final String since) {
		RespBase<List<TaskResultStatistics>> resp = RespBase.<List<TaskResultStatistics>> create();
		if ("last24hours".equals(since)) {
			return resp.withData(statisticManager.findTaskResultStatisticsDaily());
		} else {
			return resp.withData(Collections.emptyList());
		}
	}

	/**
	 * Get task result statistics.
	 * 
	 * @param period
	 *            time period
	 * @return task result statistics
	 */
	@GetMapping("/statistics/tasks/results/{period}")
	public RespBase<TaskResultStatistics> getTaskResultStatistics(
			@PathVariable(value = "period", required = false) final String period) {
		TaskResultStatistics taskResultStatistics;
		switch (period) {
		case "online":
			taskResultStatistics = statisticManager.getTaskResultStatisticsSinceOnline();
			break;
		case "lastWeek":
			taskResultStatistics = statisticManager.getTaskResultStatisticsWeekly();
			break;
		case "lastHour":
			taskResultStatistics = statisticManager.findLatestTaskResultStatistics(StatisticInterval.HOUR);
			break;
		case "lastMinute":
			taskResultStatistics = statisticManager.findLatestTaskResultStatistics(StatisticInterval.MINUTE);
			break;
		default:
			taskResultStatistics = new TaskResultStatistics(0, 0, StatisticInterval.DAY, new Date());
		}
		return RespBase.<TaskResultStatistics> create().withData(taskResultStatistics);
	}

	/**
	 * Find task running statistics.
	 * 
	 * @param since
	 *            time span
	 * @return task result statistics
	 */
	@GetMapping("/statistics/tasks/running")
	public RespBase<List<TaskRunningStatistics>> findTaskRunningStatistics(
			@RequestParam(value = "since", required = false) final String since) {
		if ("lastWeek".equals(since)) {
			return RespBase.<List<TaskRunningStatistics>> create().withData(statisticManager.findTaskRunningStatisticsWeekly());
		} else {
			return RespBase.<List<TaskRunningStatistics>> create().withData(Collections.emptyList());
		}
	}

	/**
	 * Get job execution type statistics.
	 * 
	 * @return job execution statistics
	 */
	@GetMapping("/statistics/jobs/executionType")
	public RespBase<JobExecutionTypeStatistics> getJobExecutionTypeStatistics() {
		return RespBase.<JobExecutionTypeStatistics> create().withData(statisticManager.getJobExecutionTypeStatistics());
	}

	/**
	 * Find job running statistics in the recent week.
	 * 
	 * @param since
	 *            time span
	 * @return collection of job running statistics in the recent week
	 */
	@GetMapping("/statistics/jobs/running")
	public RespBase<List<JobRunningStatistics>> findJobRunningStatistics(
			@RequestParam(value = "since", required = false) final String since) {
		if ("lastWeek".equals(since)) {
			return RespBase.<List<JobRunningStatistics>> create().withData(statisticManager.findJobRunningStatisticsWeekly());
		} else {
			return RespBase.<List<JobRunningStatistics>> create().withData(Collections.emptyList());
		}
	}

	/**
	 * Find job register statistics.
	 * 
	 * @return collection of job register statistics since online
	 */
	@GetMapping("/statistics/jobs/register")
	public RespBase<List<JobRegisterStatistics>> findJobRegisterStatistics() {
		return RespBase.<List<JobRegisterStatistics>> create().withData(statisticManager.findJobRegisterStatisticsSinceOnline());
	}
}
