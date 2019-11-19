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
package com.wl4g.devops.ci.core;

import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.core.context.DefaultPipelineContext;
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.core.param.HookParameter;
import com.wl4g.devops.ci.core.param.NewParameter;
import com.wl4g.devops.ci.core.param.RollbackParameter;
import com.wl4g.devops.ci.pipeline.PipelineProvider;
import com.wl4g.devops.ci.service.TaskHistoryService;
import com.wl4g.devops.ci.utils.LogHolder;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.common.bean.share.AppCluster;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmContact;
import com.wl4g.devops.common.utils.io.FileIOUtils.ReadResult;
import com.wl4g.devops.dao.ci.*;
import com.wl4g.devops.dao.share.AppClusterDao;
import com.wl4g.devops.dao.share.AppInstanceDao;
import com.wl4g.devops.dao.umc.AlarmContactDao;
import com.wl4g.devops.support.beans.prototype.DelegateAliasPrototypeBeanFactory;
import com.wl4g.devops.support.notification.mail.MailSenderTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.wl4g.devops.common.utils.io.FileIOUtils.*;
import static com.wl4g.devops.ci.utils.LogHolder.cleanupDefault;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;
import static com.wl4g.devops.common.utils.Exceptions.getStackTraceAsString;
import static com.wl4g.devops.common.utils.lang.Collections2.safeList;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

/**
 * Default CI/CD pipeline management implements.
 *
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version v1.0.0 2019-11-01
 * @since
 */
public class DefaultPipelineManager implements PipelineManager {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected CiCdProperties config;
	@Autowired
	protected DelegateAliasPrototypeBeanFactory beanFactory;
	@Autowired
	protected PipelineJobExecutor jobExecutor;
	@Autowired
	protected MailSenderTemplate mailSender;

	@Autowired
	protected AppInstanceDao appInstanceDao;
	@Autowired
	protected AppClusterDao appClusterDao;
	@Autowired
	protected TriggerDao triggerDao;
	@Autowired
	protected ProjectDao projectDao;
	@Autowired
	protected TaskHistoryService taskHistoryService;
	@Autowired
	protected TaskDao taskDao;
	@Autowired
	protected TaskDetailDao taskDetailDao;
	@Autowired
	protected AlarmContactDao alarmContactDao;
	@Autowired
	protected TaskBuildCommandDao taskBuildCmdDao;

	@Override
	public void newPipeline(NewParameter param) {
		if (log.isInfoEnabled()) {
			log.info("New pipeline job for: {}", param);
		}

		// Obtain task details.
		List<String> instanceIds = safeList(taskDetailDao.selectByTaskId(param.getTaskId())).stream()
				.map(detail -> String.valueOf(detail.getInstanceId())).collect(toList());
		notEmpty(instanceIds, "InstanceIds is empty, please check configure.");

		// Obtain task.
		Task task = taskDao.selectByPrimaryKey(param.getTaskId());
		notNull(task, String.format("Not found task of %s", param.getTaskId()));
		notNull(task.getAppClusterId(), "Task clusterId must not be null.");
		AppCluster appCluster = appClusterDao.selectByPrimaryKey(task.getAppClusterId());
		notNull(appCluster, "not found this app");

		List<AppInstance> instances = new ArrayList<>();
		for (String instanceId : instanceIds) {
			AppInstance instance = appInstanceDao.selectByPrimaryKey(Integer.valueOf(instanceId));
			instances.add(instance);
		}

		// Obtain task project.
		Project project = projectDao.getByAppClusterId(appCluster.getId());
		// Obtain task build commands.
		List<TaskBuildCommand> taskBuildCmds = taskBuildCmdDao.selectByTaskId(param.getTaskId());

		// Obtain task history.
		TaskHistory taskHisy = taskHistoryService.createTaskHistory(project, instances, TASK_TYPE_MANUAL, TASK_STATUS_CREATE,
				task.getBranchName(), null, null, task.getBuildCommand(), task.getPreCommand(), task.getPostCommand(),
				task.getTarType(), task.getContactGroupId(), taskBuildCmds, param.getTaskTraceId(), param.getTaskTraceType(),
				param.getRemark());

		// Execution pipeline job.
		doExecutePipeline(taskHisy.getId(), getPipelineProvider(taskHisy));
	}

	@Override
	public void rollbackPipeline(RollbackParameter param) {
		if (log.isInfoEnabled()) {
			log.info("On rollback pipeline job for: {}", param);
		}

		// Task
		TaskHistory bakTaskHisy = taskHistoryService.getById(param.getTaskId());
		notNull(bakTaskHisy, String.format("Not found pipeline task history for taskId:%s", param.getTaskId()));

		// Details
		List<TaskHistoryDetail> taskHistoryDetails = taskHistoryService.getDetailByTaskId(param.getTaskId());
		notEmpty(taskHistoryDetails, "taskHistoryDetails find empty list");

		// Project.
		Project project = projectDao.selectByPrimaryKey(bakTaskHisy.getProjectId());
		notNull(project, String.format("Not found project history for projectId:%s", bakTaskHisy.getProjectId()));

		// Instance.
		List<AppInstance> instances = new ArrayList<>();
		for (TaskHistoryDetail taskHistoryDetail : taskHistoryDetails) {
			AppInstance instance = appInstanceDao.selectByPrimaryKey(taskHistoryDetail.getInstanceId());
			instances.add(instance);
		}

		// Roll-back.
		List<TaskBuildCommand> commands = taskBuildCmdDao.selectByTaskId(param.getTaskId());
		TaskHistory rollbackTaskHisy = taskHistoryService.createTaskHistory(project, instances, TASK_TYPE_ROLLBACK,
				TASK_STATUS_CREATE, bakTaskHisy.getBranchName(), null, param.getTaskId(), bakTaskHisy.getBuildCommand(),
				bakTaskHisy.getPreCommand(), bakTaskHisy.getPostCommand(), bakTaskHisy.getTarType(),
				bakTaskHisy.getContactGroupId(), commands, null, null, null);

		// Do roll-back pipeline job.
		doRollbackPipeline(rollbackTaskHisy.getId(), getPipelineProvider(rollbackTaskHisy));
	}

	@Override
	public void hookPipeline(HookParameter param) {
		if (log.isInfoEnabled()) {
			log.info("On hook pipeline job for: {}", param);
		}

		// Obtain project.
		Project project = projectDao.getByProjectName(param.getProjectName());
		if (isNull(project)) {
			log.info("Skip hook pipeline job, becuase project not exist, project:{}, branch:{}, url:{}", param.getProjectName(),
					param.getBranchName());
			return;
		}
		// Obtain hook triggers.
		Trigger trigger = triggerDao.getTriggerByAppClusterIdAndBranch(project.getAppClusterId(), param.getBranchName());
		if (isNull(trigger)) {
			log.info("Skip hook pipeline job, becuase trigger not exist, project:{}, clusterId:{}, branch:{}",
					param.getProjectName(), project.getAppClusterId(), param.getBranchName());
			return;
		}

		// Obtain pipeline task & details instances.
		Task task = taskDao.selectByPrimaryKey(trigger.getTaskId());
		notNull(task, "Hook pipeline task must not be null.");
		List<AppInstance> instances = safeList(taskDetailDao.selectByTaskId(task.getId())).stream()
				.map(detail -> appInstanceDao.selectByPrimaryKey(detail.getInstanceId())).collect(toList());
		notEmpty(instances, "Hook pipeline task instances is empty, please complete the configure.");

		// Create task history(NEW).
		String sha = null;
		List<TaskBuildCommand> taskBuildCmds = taskBuildCmdDao.selectByTaskId(task.getId());
		TaskHistory taskHisy = taskHistoryService.createTaskHistory(project, instances, TASK_TYPE_TRIGGER, TASK_STATUS_CREATE,
				param.getBranchName(), sha, null, task.getBuildCommand(), task.getPreCommand(), task.getPostCommand(),
				task.getTarType(), task.getContactGroupId(), taskBuildCmds, null, null, null);

		// Execution pipeline job.
		doExecutePipeline(taskHisy.getId(), getPipelineProvider(taskHisy));
	}

	@Override
	public ReadResult logfile(Integer taskHisId, Integer index, Integer size) {
		if (isNull(index)) {
			index = 0;
		}
		if (isNull(size)) {
			size = 100;
		}
		String logPath = config.getJobLog(taskHisId).getAbsolutePath();
		// End if 'EOF'
		return seekReadLines(logPath, index, size, line -> trimToEmpty(line).equalsIgnoreCase(LOG_FILE_END));
	}

	/**
	 * Execution new pipeline job.
	 *
	 * @param taskId
	 * @param provider
	 */
	protected void doExecutePipeline(Integer taskId, PipelineProvider provider) {
		notNull(taskId, "Pipeline taskId must not be null");
		notNull(provider, "Pipeline provider must not be null");
		if (log.isInfoEnabled()) {
			log.info("Starting pipeline job for taskId: {}, provider: {}", taskId, provider.getClass().getSimpleName());
		}

		// Setup status to running.
		taskHistoryService.updateStatus(taskId, TASK_STATUS_RUNNING);
		log.info("Updated pipeline job status to {} for {}", TASK_STATUS_RUNNING, taskId);

		// Starting pipeline job.
		jobExecutor.getWorker().execute(() -> {
			try {
				// Log file start EOF.
				writeALineFile(config.getJobLog(taskId).getAbsoluteFile(), LOG_FILE_START);

				// Execution pipeline.
				provider.execute();

				// Pipeline success.
				log.info(String.format("Pipeline job successful for taskId: %s, provider: %s", taskId,
						provider.getClass().getSimpleName()));

				// Setup status to success.
				taskHistoryService.updateStatusAndResultAndSha(taskId, TASK_STATUS_SUCCESS, null, provider.getSourceFingerprint(),
						provider.getAssetsFingerprint());
				log.info("Updated pipeline job status to {} for {}", TASK_STATUS_SUCCESS, taskId);

				// Successful process.
				log.info("Process pipeline job success properties for taskId: {}, provider: {}", taskId,
						provider.getClass().getSimpleName());
				postPipelineExecuteSuccess(taskId, provider);
			} catch (Throwable e) {
				log.error(String.format("Failed to pipeline job for taskId: %s, provider: %s", taskId,
						provider.getClass().getSimpleName()), e);

				// Setup status to failure.
				taskHistoryService.updateStatusAndResult(taskId, TASK_STATUS_STOP, getStackTraceAsString(e));
				log.info("Updated pipeline job status to {} for {}", TASK_STATUS_STOP, taskId);

				// Failure process.
				log.info("Process pipeline job failure properties for taskId: {}, provider: {}", taskId,
						provider.getClass().getSimpleName());
				postPipelineExecuteFailure(taskId, provider, e);
			} finally {
				cleanupDefault(); // Help GC

				// Log file end EOF.
				writeBLineFile(config.getJobLog(taskId).getAbsoluteFile(), LOG_FILE_END);
				log.info("Completed for pipeline taskId: {}", taskId);
			}
		});
	}

	/**
	 * Post pipeline job execution successful properties process.
	 * 
	 * @param taskId
	 * @param provider
	 */
	protected void postPipelineExecuteSuccess(Integer taskId, PipelineProvider provider) {
		// Successful execute job notification.
		notificationResult(provider.getContext().getTaskHistory().getContactGroupId(), "Task Build Success taskId=" + taskId
				+ " projectName=" + provider.getContext().getProject().getProjectName() + " time=" + (new Date()));
	}

	/**
	 * Post pipeline job execution failure properties process.
	 * 
	 * @param taskId
	 * @param provider
	 * @param e
	 */
	protected void postPipelineExecuteFailure(Integer taskId, PipelineProvider provider, Throwable e) {
		// Failure execute job notification.
		notificationResult(provider.getContext().getTaskHistory().getContactGroupId(), "Task Build Fail taskId=" + taskId
				+ " projectName=" + provider.getContext().getProject().getProjectName() + " time=" + (new Date()) + "\n");
	}

	/**
	 * Notification pipeline execution result.
	 * 
	 * @param contactGroupId
	 * @param message
	 */
	protected void notificationResult(Integer contactGroupId, String message) {
		List<AlarmContact> contactByGroupIds = alarmContactDao.getContactByGroupIds(asList(contactGroupId));
		for (AlarmContact contact : contactByGroupIds) {
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setSubject("CI Build Report");
			msg.setTo(contact.getEmail());
			msg.setText(message);
			msg.setSentDate(new Date());
			mailSender.send(msg);
		}
	}

	/**
	 * Get task pipeline provider.
	 * 
	 * @param taskHisy
	 * @return
	 */
	protected PipelineProvider getPipelineProvider(TaskHistory taskHisy) {
		notNull(taskHisy, "TaskHistory can not be null");

		Project project = projectDao.selectByPrimaryKey(taskHisy.getProjectId());
		notNull(project, "Project can not be null");

		AppCluster appCluster = appClusterDao.selectByPrimaryKey(project.getAppClusterId());
		notNull(appCluster, "AppCluster can not be null");
		project.setGroupName(appCluster.getName());

		List<TaskHistoryDetail> taskHisyDetails = taskHistoryService.getDetailByTaskId(taskHisy.getId());
		notNull(taskHisyDetails, "taskHistoryDetails can not be null");

		TaskHistory refTaskHisy = null;
		if (nonNull(taskHisy.getRefId())) {
			refTaskHisy = taskHistoryService.getById(taskHisy.getRefId());
		}

		// Obtain instances.
		List<AppInstance> instances = safeList(taskHisyDetails).stream()
				.map(detail -> appInstanceDao.selectByPrimaryKey(detail.getInstanceId())).collect(toList());

		// New pipeline context.
		String projectSourceDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();
		PipelineContext context = new DefaultPipelineContext(project, projectSourceDir, appCluster, instances, taskHisy,
				refTaskHisy, taskHisyDetails);

		// Get prototype provider.
		return beanFactory.getPrototypeBean(context.getTaskHistory().getTarType(), context);
	}

	/**
	 * Execution roll-back pipeline job.
	 *
	 * @param taskId
	 * @param provider
	 */
	protected void doRollbackPipeline(Integer taskId, PipelineProvider provider) {
		notNull(taskId, "TaskId must not be null.");
		if (log.isInfoEnabled()) {
			log.info("Starting rollback pipeline job for taskId: {}, provider: {}", taskId, provider.getClass().getSimpleName());
		}

		// Update status to running.
		taskHistoryService.updateStatus(taskId, TASK_STATUS_RUNNING);
		log.info("Updated rollback pipeline job status to {} for {}", TASK_STATUS_RUNNING, taskId);

		// Submit roll-back job.
		jobExecutor.getWorker().execute(() -> {
			try {
				// Log file start EOF.
				writeALineFile(config.getJobLog(taskId).getAbsoluteFile(), LOG_FILE_START);

				// Execution roll-back pipeline.
				provider.rollback();

				// Success.
				log.info(String.format("Rollback pipeline job successful for taskId: %s, provider: %s", taskId,
						provider.getClass().getSimpleName()));

				taskHistoryService.updateStatusAndResultAndSha(taskId, TASK_STATUS_SUCCESS, null, provider.getSourceFingerprint(),
						provider.getAssetsFingerprint());
				log.info("Updated rollback pipeline job status to {} for {}", TASK_STATUS_SUCCESS, taskId);
			} catch (Exception e) {
				log.error(String.format("Failed to rollback pipeline job for taskId: %s, provider: %s", taskId,
						provider.getClass().getSimpleName()), e);

				taskHistoryService.updateStatusAndResult(taskId, TASK_STATUS_FAIL, e.getMessage());
				log.info("Updated rollback pipeline job status to {} for {}", TASK_STATUS_FAIL, taskId);
			} finally {
				LogHolder.cleanupDefault(); // Help GC

				// Log file end EOF.
				writeBLineFile(config.getJobLog(taskId).getAbsoluteFile(), LOG_FILE_END);
				log.info("Completed for rollback pipeline taskId: {}", taskId);
			}
		});
	}

}