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
import com.wl4g.devops.common.bean.share.Environment;
import com.wl4g.devops.common.bean.umc.AlarmContact;
import com.wl4g.devops.common.utils.io.FileIOUtils;
import com.wl4g.devops.dao.ci.*;
import com.wl4g.devops.dao.scm.AppClusterDao;
import com.wl4g.devops.dao.umc.AlarmContactDao;
import com.wl4g.devops.support.beans.DelegateAliasPrototypeBeanFactory;
import com.wl4g.devops.support.ms.mail.MailSenderTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.wl4g.devops.ci.pipeline.PipelineProvider.PipelineType.*;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;
import static java.util.Arrays.asList;

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
	protected MailSenderTemplate mailSender;
	@Autowired
	protected PipelineJobExecutor jobExecutor;

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
	protected TaskBuildCommandDao taskBuildCommandDao;

	@Override
	public List<AppCluster> grouplist() {
		return appClusterDao.grouplist();
	}

	@Override
	public List<Environment> environmentlist(String clusterId) {
		return appClusterDao.environmentlist(clusterId);
	}

	@Override
	public List<AppInstance> instancelist(AppInstance appInstance) {
		return appClusterDao.instancelist(appInstance);
	}

	/**
	 * Create Task History
	 *
	 * @param taskId
	 */
	@Override
	public void createTask(Integer taskId) {
		log.debug("into PipelineCoreProcessorImpl.createTask prarms::" + "taskId = {} ", taskId);
		Assert.notNull(taskId, "taskId is null");
		Task task = taskDao.selectByPrimaryKey(taskId);
		Assert.notNull(task, "task is null");
		List<TaskDetail> taskDetails = taskDetailDao.selectByTaskId(taskId);
		List<String> instanceStrs = new ArrayList<>();
		for (TaskDetail taskDetail : taskDetails) {
			instanceStrs.add(String.valueOf(taskDetail.getInstanceId()));
		}
		Assert.notNull(task.getAppClusterId(), "clusterId is null");
		AppCluster appCluster = appClusterDao.getAppGroup(task.getAppClusterId());

		Assert.notNull(appCluster, "not found this app");
		Project project = projectDao.getByAppClusterId(appCluster.getId());

		Assert.notEmpty(instanceStrs, "instanceIds find empty list,Please check the instanceId");
		List<AppInstance> instances = new ArrayList<>();
		for (String instanceId : instanceStrs) {
			AppInstance instance = appClusterDao.getAppInstance(instanceId);
			instances.add(instance);
		}
		List<TaskBuildCommand> taskBuildCommands = taskBuildCommandDao.selectByTaskId(taskId);
		TaskHistory taskHistory = taskHistoryService.createTaskHistory(project, instances, TASK_TYPE_MANUAL, TASK_STATUS_CREATE,
				task.getBranchName(), null, null, task.getPreCommand(), task.getPostCommand(), task.getTarType(),
				task.getContactGroupId(), taskBuildCommands);
		PipelineProvider provider = getPipelineProvider(taskHistory);
		// execute
		execute(taskHistory.getId(), provider);
	}

	/**
	 * Hook -- for gitlab hook
	 *
	 * @param projectName
	 * @param branchName
	 * @param url
	 */
	public void hook(String projectName, String branchName, String url) {
		log.info("into PipelineCoreProcessorImpl.hook prarms::" + "projectName = {} , branchName = {} , url = {} ", projectName,
				branchName, url);
		Project project = projectDao.getByProjectName(projectName);
		if (Objects.isNull(project)) {
			return;
		}
		Trigger trigger = triggerDao.getTriggerByAppClusterIdAndBranch(project.getAppClusterId(), branchName);
		if (Objects.isNull(trigger)) {
			return;
		}

		Task task = taskDao.selectByPrimaryKey(trigger.getTaskId());
		Assert.notNull(task, "task not found");
		List<TaskDetail> taskDetails = taskDetailDao.selectByTaskId(task.getId());

		List<AppInstance> instances = new ArrayList<>();
		for (TaskDetail taskDetail : taskDetails) {
			AppInstance instance = appClusterDao.getAppInstance(taskDetail.getInstanceId().toString());
			instances.add(instance);
		}
		Assert.notEmpty(instances, "instances not found, please config first");

		// get sha
		String sha = null;
		List<TaskBuildCommand> taskBuildCommands = taskBuildCommandDao.selectByTaskId(task.getId());

		// Print to client
		// ShellContextHolder.printfQuietly("taskHistory begin");
		TaskHistory taskHistory = taskHistoryService.createTaskHistory(project, instances, TASK_TYPE_TRIGGER, TASK_STATUS_CREATE,
				branchName, sha, null, task.getPreCommand(), task.getPostCommand(), task.getTarType(), task.getContactGroupId(),
				taskBuildCommands);
		PipelineProvider provider = getPipelineProvider(taskHistory);
		// execute
		execute(taskHistory.getId(), provider);
	}

	/**
	 * Execute task
	 *
	 * @param taskId
	 * @param provider
	 */
	private void execute(Integer taskId, PipelineProvider provider) {
		if (log.isInfoEnabled()) {
			log.info("Pipeline job for taskId: {}", taskId);
		}
		// Update status to running.
		taskHistoryService.updateStatus(taskId, TASK_STATUS_RUNNING);

		// Submit pipeline job.
		jobExecutor.getWorker().execute(() -> {
			try {
				provider.execute();
				if (provider.getTaskResult().isSuccess()) {
					// update task--success
					log.info("task succcess taskId={}", taskId);
					taskHistoryService.updateStatusAndResultAndSha(taskId, TASK_STATUS_SUCCESS,
							provider.getTaskResult().getStringBuffer().toString(), provider.getShaGit(), provider.getShaLocal());

					sendMailByContactGroupId(provider.getPipelineInfo().getTaskHistory().getContactGroupId(),
							"Task Build Success taskId=" + taskId + " projectName="
									+ provider.getPipelineInfo().getProject().getProjectName() + " time=" + (new Date()));
				} else {
					// update task--fail
					log.info("task fail taskId={}", taskId);
					taskHistoryService.updateStatusAndResult(taskId, TASK_STATUS_FAIL,
							provider.getTaskResult().getStringBuffer().toString());

					sendMailByContactGroupId(provider.getPipelineInfo().getTaskHistory().getContactGroupId(),
							"Task Build Fail taskId=" + taskId + " projectName="
									+ provider.getPipelineInfo().getProject().getProjectName() + " time=" + (new Date()) + "\n");
				}
			} catch (Exception e) {
				// update task--fail
				log.info("task fail taskId={}", taskId);
				taskHistoryService.updateStatusAndResult(taskId, TASK_STATUS_FAIL,
						provider.getTaskResult().getStringBuffer().toString() + e.getMessage());
				e.printStackTrace();
				sendMailByContactGroupId(provider.getPipelineInfo().getTaskHistory().getContactGroupId(),
						"Task Build Fail taskId=" + taskId + " projectName="
								+ provider.getPipelineInfo().getProject().getProjectName() + " time=" + (new Date()) + "\n");
			}
		});

	}

	private void sendMailByContactGroupId(Integer contactGroupId, String text) {
		List<AlarmContact> contactByGroupIds = alarmContactDao.getContactByGroupIds(asList(contactGroupId));
		for (AlarmContact contact : contactByGroupIds) {
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setSubject("CI Build Report");
			msg.setTo(contact.getEmail());
			msg.setText(text);
			msg.setSentDate(new Date());
			mailSender.send(msg);
		}
	}

	/**
	 * Create roll-back Task by taskId.
	 *
	 * @param taskId
	 */
	public void createRollbackTask(Integer taskId) {
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
		List<TaskBuildCommand> commands = taskBuildCommandDao.selectByTaskId(taskId);
		TaskHistory rollbackTaskHisy = taskHistoryService.createTaskHistory(project, instances, TASK_TYPE_ROLLBACK,
				TASK_STATUS_CREATE, backupTaskHisy.getBranchName(), null, taskId, backupTaskHisy.getPreCommand(),
				backupTaskHisy.getPostCommand(), backupTaskHisy.getTarType(), backupTaskHisy.getContactGroupId(), commands);

		// Do roll-back pipeline job.
		doRollback(rollbackTaskHisy.getId(), getPipelineProvider(rollbackTaskHisy));
	}

	/**
	 * Get task pipeline provider.
	 * 
	 * @param taskHisy
	 * @return
	 */
	private PipelineProvider getPipelineProvider(TaskHistory taskHisy) {
		log.info("into PipelineCoreProcessorImpl.buildDeployProvider prarms::" + "taskHistory = {} ", taskHisy);
		Assert.notNull(taskHisy, "taskHistory can not be null");

		Project project = projectDao.selectByPrimaryKey(taskHisy.getProjectId());
		Assert.notNull(project, "project can not be null");

		AppCluster appCluster = appClusterDao.getAppGroup(project.getAppClusterId());
		Assert.notNull(appCluster, "appCluster can not be null");
		project.setGroupName(appCluster.getName());

		List<TaskHistoryDetail> taskHistoryDetails = taskHistoryService.getDetailByTaskId(taskHisy.getId());
		Assert.notNull(taskHistoryDetails, "taskHistoryDetails can not be null");

		TaskHistory refTaskHistory = null;
		if (taskHisy.getRefId() != null) {
			refTaskHistory = taskHistoryService.getById(taskHisy.getRefId());
		}

		List<AppInstance> instances = new ArrayList<>();
		for (TaskHistoryDetail taskHistoryDetail : taskHistoryDetails) {
			AppInstance instance = appClusterDao.getAppInstance(taskHistoryDetail.getInstanceId().toString());
			instances.add(instance);
		}
		PipelineInfo info = new DefaultPipelineInfo();
		info.setProject(project);
		info.setTarType(taskHisy.getTarType());
		info.setPath(config.getWorkspace() + "/" + project.getProjectName());
		info.setBranch(taskHisy.getBranchName());
		info.setAlias(appCluster.getName());
		info.setInstances(instances);
		info.setTaskHistory(taskHisy);
		info.setRefTaskHistory(refTaskHistory);
		info.setTaskHistoryDetails(taskHistoryDetails);

		return getPrototypePipelineProvider(beanFactory, info.getTarType(), info);
	}

	/**
	 * Do roll-back pipeline job.
	 *
	 * @param taskId
	 * @param provider
	 */
	private void doRollback(Integer taskId, PipelineProvider provider) {
		if (log.isInfoEnabled()) {
			log.info("Rollback pipeline job for taskId: {}", taskId);
		}
		// Update status to running.
		taskHistoryService.updateStatus(taskId, TASK_STATUS_RUNNING);

		// Submit roll-back job.
		jobExecutor.getWorker().execute(() -> {
			try {
				provider.rollback();
				if (provider.getTaskResult().isSuccess()) {
					taskHistoryService.updateStatusAndResultAndSha(taskId, TASK_STATUS_SUCCESS,
							provider.getTaskResult().getStringBuffer().toString(), provider.getShaGit(), provider.getShaLocal());
				} else {
					taskHistoryService.updateStatusAndResult(taskId, TASK_STATUS_FAIL,
							provider.getTaskResult().getStringBuffer().toString());
				}
			} catch (Exception e) {
				taskHistoryService.updateStatusAndResult(taskId, TASK_STATUS_FAIL, e.getMessage());
				e.printStackTrace();
			}
		});
	}

	public FileIOUtils.ReadResult readLog(Integer taskHisId, Integer index, Integer size) {
		if (Objects.isNull(index)) {
			index = 0;
		}
		if (Objects.isNull(size)) {
			size = 100;
		}
		String logPath = config.getJobLog(taskHisId) + "/build.log";
		return FileIOUtils.readSeekLines(logPath, index, size);
	}

}