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
import com.wl4g.devops.ci.pipeline.PipelineProvider;
import com.wl4g.devops.ci.pipeline.model.DefaultPipelineInfo;
import com.wl4g.devops.ci.pipeline.model.PipelineInfo;
import com.wl4g.devops.ci.service.TaskHistoryService;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.common.bean.share.AppCluster;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmContact;
import com.wl4g.devops.common.utils.io.FileIOUtils;
import com.wl4g.devops.common.utils.io.FileIOUtils.ReadResult;
import com.wl4g.devops.dao.ci.*;
import com.wl4g.devops.dao.scm.AppClusterDao;
import com.wl4g.devops.dao.umc.AlarmContactDao;
import com.wl4g.devops.support.beans.prototype.DelegateAliasPrototypeBeanFactory;
import com.wl4g.devops.support.notification.mail.MailSenderTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;
import static com.wl4g.devops.common.utils.lang.Collections2.safeList;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

/**
 * CI/CD Service implements
 *
 * @author vjay
 * @date 2019-05-16 14:50:00
 */
public class DefaultPipeline implements Pipeline {
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
	public void startup(Integer taskId) {
		notNull(taskId, "Pipeline job taskId must not be null");
		if (log.isInfoEnabled()) {
			log.info("On creating pipeline job. taskId:{}", taskId);
		}

		// Obtain task details.
		List<String> instanceIds = safeList(taskDetailDao.selectByTaskId(taskId)).stream()
				.map(detail -> String.valueOf(detail.getInstanceId())).collect(toList());
		notEmpty(instanceIds, "InstanceIds is empty, please check configure.");

		// Obtain task.
		Task task = taskDao.selectByPrimaryKey(taskId);
		notNull(task, String.format("Not found task of %s", taskId));
		notNull(task.getAppClusterId(), "Task clusterId must not be null.");
		AppCluster appCluster = appClusterDao.getAppGroup(task.getAppClusterId());
		notNull(appCluster, "not found this app");

		List<AppInstance> instances = new ArrayList<>();
		for (String instanceId : instanceIds) {
			AppInstance instance = appClusterDao.getAppInstance(instanceId);
			instances.add(instance);
		}

		// Obtain task project.
		Project project = projectDao.getByAppClusterId(appCluster.getId());
		// Obtain task build commands.
		List<TaskBuildCommand> taskBuildCmds = taskBuildCmdDao.selectByTaskId(taskId);

		// Obtain task history.
		TaskHistory taskHisy = taskHistoryService.createTaskHistory(project, instances, TASK_TYPE_MANUAL, TASK_STATUS_CREATE,
				task.getBranchName(), null, null, task.getBuildCommand(), task.getPreCommand(), task.getPostCommand(),
				task.getTarType(), task.getContactGroupId(), taskBuildCmds);

		// Execution pipeline job.
		doExecute0(taskHisy.getId(), getPipelineProvider(taskHisy));
	}

	@Override
	public void rollback(Integer taskId) {
		if (log.isInfoEnabled()) {
			log.info("into PipelineCoreProcessorImpl.rollback prarms::" + "taskId = {} ", taskId);
		}
		Assert.notNull(taskId, "Rollback taskId must not be null.");

		// Task
		TaskHistory backupTaskHisy = taskHistoryService.getById(taskId);
		Assert.notNull(backupTaskHisy, String.format("Not found pipeline task history for taskId:%s", taskId));
		// Details
		List<TaskHistoryDetail> taskHistoryDetails = taskHistoryService.getDetailByTaskId(taskId);
		Assert.notEmpty(taskHistoryDetails, "taskHistoryDetails find empty list");
		// Project.
		Project project = projectDao.selectByPrimaryKey(backupTaskHisy.getProjectId());
		Assert.notNull(project, String.format("Not found project history for projectId:%s", backupTaskHisy.getProjectId()));
		// Instance.
		List<AppInstance> instances = new ArrayList<>();
		for (TaskHistoryDetail taskHistoryDetail : taskHistoryDetails) {
			AppInstance instance = appClusterDao.getAppInstance(taskHistoryDetail.getInstanceId().toString());
			instances.add(instance);
		}

		// Roll-back.
		List<TaskBuildCommand> commands = taskBuildCmdDao.selectByTaskId(taskId);
		TaskHistory rollbackTaskHisy = taskHistoryService.createTaskHistory(project, instances, TASK_TYPE_ROLLBACK,
				TASK_STATUS_CREATE, backupTaskHisy.getBranchName(), null, taskId, backupTaskHisy.getBuildCommand(),
				backupTaskHisy.getPreCommand(), backupTaskHisy.getPostCommand(), backupTaskHisy.getTarType(),
				backupTaskHisy.getContactGroupId(), commands);

		// Do roll-back pipeline job.
		doRollback(rollbackTaskHisy.getId(), getPipelineProvider(rollbackTaskHisy));
	}

	@Override
	public void hook(String projectName, String branchName, String url) {
		if (log.isInfoEnabled()) {
			log.info("On hook pipeline job. project:{}, branch:{}, url:{}", projectName, branchName, url);
		}

		// Obtain project.
		Project project = projectDao.getByProjectName(projectName);
		if (isNull(project)) {
			log.info("Skip hook pipeline job, becuase project not exist, project:{}, branch:{}, url:{}", projectName, branchName,
					url);
			return;
		}
		// Obtain hook triggers.
		Trigger trigger = triggerDao.getTriggerByAppClusterIdAndBranch(project.getAppClusterId(), branchName);
		if (isNull(trigger)) {
			log.info("Skip hook pipeline job, becuase trigger not exist, project:{}, clusterId:{}, branch:{}, url:{}",
					projectName, project.getAppClusterId(), branchName, url);
			return;
		}

		// Obtain pipeline task & details instances.
		Task task = taskDao.selectByPrimaryKey(trigger.getTaskId());
		notNull(task, "Hook pipeline task must not be null.");
		List<AppInstance> instances = safeList(taskDetailDao.selectByTaskId(task.getId())).stream()
				.map(detail -> appClusterDao.getAppInstance(detail.getInstanceId().toString())).collect(toList());
		notEmpty(instances, "Hook pipeline task instances is empty, please complete the configure.");

		// Create task history(NEW).
		String sha = null;
		List<TaskBuildCommand> taskBuildCmds = taskBuildCmdDao.selectByTaskId(task.getId());
		TaskHistory taskHisy = taskHistoryService.createTaskHistory(project, instances, TASK_TYPE_TRIGGER, TASK_STATUS_CREATE,
				branchName, sha, null, task.getBuildCommand(), task.getPreCommand(), task.getPostCommand(), task.getTarType(),
				task.getContactGroupId(), taskBuildCmds);

		// Execution pipeline job.
		doExecute0(taskHisy.getId(), getPipelineProvider(taskHisy));
	}

	@Override
	public ReadResult logfile(Integer taskHisId, Integer index, Integer size) {
		if (Objects.isNull(index)) {
			index = 0;
		}
		if (Objects.isNull(size)) {
			size = 100;
		}
		String logPath = config.getJobLog(taskHisId).getAbsolutePath();
		return FileIOUtils.seekReadLines(logPath, index, size, line -> {
			return line.equalsIgnoreCase("EOF"); // End if 'EOF'
		});
	}

	/**
	 * Execution pipeline job.
	 *
	 * @param taskId
	 * @param provider
	 */
	protected void doExecute0(Integer taskId, PipelineProvider provider) {
		notNull(taskId, "Pipeline taskId must not be null");
		notNull(provider, "Pipeline provider must not be null");
		if (log.isInfoEnabled()) {
			log.info("Startup pipeline job for taskId: {}, provider: {}", taskId, provider.getClass().getSimpleName());
		}
		// Setup status to running.
		taskHistoryService.updateStatus(taskId, TASK_STATUS_RUNNING);

		// Starting pipeline job.
		jobExecutor.getWorker().execute(() -> {
			try {
				provider.execute();
				if (log.isInfoEnabled()) {
					log.info("Pipeline job exec successful for taskId: {}, provider: {}", taskId,
							provider.getClass().getSimpleName());
				}
				// Setup status to success.
				taskHistoryService.updateStatusAndResultAndSha(taskId, TASK_STATUS_SUCCESS, null, provider.getShaGit(),
						provider.getShaLocal());

				// Post successful process.
				postPipelineExecuteSuccess(taskId, provider);
			} catch (Throwable e) {
				log.error(String.format("Failed to pipeline job for taskId: %s, provider: %s", taskId,
						provider.getClass().getSimpleName()), e);
				// Setup status to failure.
				taskHistoryService.updateStatusAndResult(taskId, TASK_STATUS_STOP, e.getMessage());

				// Post failure process.
				postPipelineExecuteFailure(taskId, provider, e);
			} finally {
				// Force mark end EOF.
				FileIOUtils.writeFile(config.getJobLog(taskId).getAbsoluteFile(), LOG_FILE_END);
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
		notificationResult(provider.getPipelineInfo().getTaskHistory().getContactGroupId(), "Task Build Success taskId=" + taskId
				+ " projectName=" + provider.getPipelineInfo().getProject().getProjectName() + " time=" + (new Date()));
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
		notificationResult(provider.getPipelineInfo().getTaskHistory().getContactGroupId(), "Task Build Fail taskId=" + taskId
				+ " projectName=" + provider.getPipelineInfo().getProject().getProjectName() + " time=" + (new Date()) + "\n");
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
	private PipelineProvider getPipelineProvider(TaskHistory taskHisy) {
		notNull(taskHisy, "TaskHistory can not be null");

		Project project = projectDao.selectByPrimaryKey(taskHisy.getProjectId());
		notNull(project, "Project can not be null");

		AppCluster appCluster = appClusterDao.getAppGroup(project.getAppClusterId());
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
				.map(detail -> appClusterDao.getAppInstance(String.valueOf(detail.getInstanceId()))).collect(toList());

		PipelineInfo info = new DefaultPipelineInfo();
		info.setProject(project);
		info.setTarType(taskHisy.getTarType());
		info.setPath(config.getProjectDir(project.getProjectName()).getAbsolutePath());
		info.setBranch(taskHisy.getBranchName());
		info.setAlias(appCluster.getName());
		info.setInstances(instances);
		info.setTaskHistory(taskHisy);
		info.setRefTaskHistory(refTaskHisy);
		info.setTaskHistoryDetails(taskHisyDetails);

		return beanFactory.getPrototypeBean(info.getTarType(), info);
	}

	/**
	 * Do roll-back pipeline job.
	 *
	 * @param taskId
	 * @param provider
	 */
	private void doRollback(Integer taskId, PipelineProvider provider) {
		notNull(taskId, "TaskId must not be null.");
		if (log.isInfoEnabled()) {
			log.info("Rollback pipeline job for taskId: {}", taskId);
		}
		// Update status to running.
		taskHistoryService.updateStatus(taskId, TASK_STATUS_RUNNING);

		// Submit roll-back job.
		jobExecutor.getWorker().execute(() -> {
			try {
				provider.rollback();

				taskHistoryService.updateStatusAndResultAndSha(taskId, TASK_STATUS_SUCCESS, null, provider.getShaGit(),
						provider.getShaLocal());

			} catch (Exception e) {
				taskHistoryService.updateStatusAndResult(taskId, TASK_STATUS_FAIL, e.getMessage());
				e.printStackTrace();
			}
		});
	}

}