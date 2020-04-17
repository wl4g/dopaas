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

import com.wl4g.devops.ci.bean.PipelineModel;
import com.wl4g.devops.ci.config.CiCdProperties;
import com.wl4g.devops.ci.core.context.DefaultPipelineContext;
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.core.param.HookParameter;
import com.wl4g.devops.ci.core.param.NewParameter;
import com.wl4g.devops.ci.core.param.RollbackParameter;
import com.wl4g.devops.ci.flow.FlowManager;
import com.wl4g.devops.ci.pipeline.PipelineProvider;
import com.wl4g.devops.ci.service.TaskHistoryService;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.common.bean.erm.AppCluster;
import com.wl4g.devops.common.bean.erm.AppInstance;
import com.wl4g.devops.common.bean.iam.Contact;
import com.wl4g.devops.common.bean.iam.ContactChannel;
import com.wl4g.devops.common.framework.beans.AliasPrototypeBeanFactory;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.dao.ci.*;
import com.wl4g.devops.dao.erm.AppClusterDao;
import com.wl4g.devops.dao.erm.AppInstanceDao;
import com.wl4g.devops.dao.iam.ContactDao;
import com.wl4g.devops.support.notification.GenericNotifyMessage;
import com.wl4g.devops.support.notification.MessageNotifier;
import com.wl4g.devops.support.notification.MessageNotifier.NotifierKind;
import com.wl4g.devops.tool.common.io.FileIOUtils.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.wl4g.devops.ci.flow.FlowManager.FlowStatus.*;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.*;
import static com.wl4g.devops.tool.common.collection.Collections2.safeList;
import static com.wl4g.devops.tool.common.io.FileIOUtils.*;
import static com.wl4g.devops.tool.common.lang.Exceptions.getStackTraceAsString;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.Assert.*;

/**
 * Default CI/CD pipeline management implements.
 *
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version v1.0.0 2019-11-01
 * @since
 */
public class DefaultPipelineManager implements PipelineManager {
	final protected Logger log = getLogger(getClass());

	@Autowired
	protected CiCdProperties config;
	@Autowired
	protected AliasPrototypeBeanFactory beanFactory;
	@Autowired
	protected PipelineJobExecutor jobExecutor;
	@Autowired
	protected GenericOperatorAdapter<NotifierKind, MessageNotifier> notifierAdapter;

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
	protected ContactDao contactDao;
	@Autowired
	protected TaskBuildCommandDao taskBuildCmdDao;
	@Autowired
	protected TaskHistoryDetailDao taskHistoryDetailDao;
	@Autowired
	protected FlowManager flowManager;

	@Override
	public void runPipeline(NewParameter param, PipelineModel pipelineModel) {
		log.info("Running pipeline job for: {}", param);

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
				task.getProviderKind(), task.getBranchType(), task.getContactGroupId(), taskBuildCmds, param.getTaskTraceId(), param.getTaskTraceType(),
				param.getRemark(), task.getEnvType(), param.getAnnex(), task.getParentAppHome(), task.getAssetsPath());

		// Execution pipeline job.
		doExecutePipeline(taskHisy.getId(), getPipelineProvider(taskHisy, pipelineModel));
	}

	@Override
	public void rollbackPipeline(RollbackParameter param, PipelineModel pipelineModel) {
		log.info("Rollback pipeline job for: {}", param);

		// Task
		TaskHistory bakTaskHisy = taskHistoryService.getById(param.getTaskId());
		notNull(bakTaskHisy, String.format("Not found pipeline task history for taskId:%s", param.getTaskId()));

		isTrue(bakTaskHisy.getStatus() == 2, "If taskHis is not success, Unnecessary to rollback");

		// Details
		List<TaskHistoryInstance> taskHistoryInstances = taskHistoryService.getDetailByTaskId(param.getTaskId());
		notEmpty(taskHistoryInstances, "taskHistoryInstances find empty list");

		// Project.
		Project project = projectDao.selectByPrimaryKey(bakTaskHisy.getProjectId());
		notNull(project, String.format("Not found project history for projectId:%s", bakTaskHisy.getProjectId()));

		// Instance.
		List<AppInstance> instances = new ArrayList<>();
		for (TaskHistoryInstance taskHistoryInstance : taskHistoryInstances) {
			AppInstance instance = appInstanceDao.selectByPrimaryKey(taskHistoryInstance.getInstanceId());
			instances.add(instance);
		}

		// Roll-back.
		List<TaskBuildCommand> commands = taskBuildCmdDao.selectByTaskId(param.getTaskId());
		TaskHistory rollbackTaskHisy = taskHistoryService.createTaskHistory(project, instances, TASK_TYPE_ROLLBACK,
				TASK_STATUS_CREATE, bakTaskHisy.getBranchName(), bakTaskHisy.getShaGit(), param.getTaskId(),
				bakTaskHisy.getBuildCommand(), bakTaskHisy.getPreCommand(), bakTaskHisy.getPostCommand(),
				bakTaskHisy.getProviderKind(), bakTaskHisy.getBranchType(),bakTaskHisy.getContactGroupId(), commands, bakTaskHisy.getTrackId(),
				bakTaskHisy.getTrackType(), bakTaskHisy.getRemark(), bakTaskHisy.getEnvType(), bakTaskHisy.getAnnex(),
				bakTaskHisy.getParentAppHome(), bakTaskHisy.getAssetsPath());

		// Do roll-back pipeline job.
		doRollbackPipeline(rollbackTaskHisy.getId(), getPipelineProvider(rollbackTaskHisy, pipelineModel));
	}

	@Override
	public void hookPipeline(HookParameter param) {
		log.info("On hook pipeline job for: {}", param);

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
				task.getProviderKind(), task.getBranchType(), task.getContactGroupId(), taskBuildCmds, null, null, null, task.getEnvType(), null,
				task.getParentAppHome(), task.getAssetsPath());

		PipelineModel pipelineModel = flowManager.buildPipeline(task.getId());
		// Execution pipeline job.
		doExecutePipeline(taskHisy.getId(), getPipelineProvider(taskHisy, pipelineModel));
	}

	@Override
	public ReadResult logfile(Integer taskHisId, Long startPos, Integer size) {
		if (isNull(startPos)) {
			startPos = 0l;
		}
		if (isNull(size)) {
			size = 100;
		}
		String logPath = config.getJobLog(taskHisId).getAbsolutePath();
		// End if 'EOF'
		return seekReadLines(logPath, startPos, size, line -> trimToEmpty(line).equalsIgnoreCase(LOG_FILE_END));
	}

	@Override
	public ReadResult logDetailFile(Integer taskHisId, Integer instanceId, Long startPos, Integer size) {
		if (isNull(startPos)) {
			startPos = 0l;
		}
		if (isNull(size)) {
			size = 100;
		}
		String logPath = config.getJobDeployerLog(taskHisId, instanceId).getAbsolutePath();
		// End if 'EOF'
		return seekReadLines(logPath, startPos, size, line -> trimToEmpty(line).equalsIgnoreCase(LOG_FILE_END));
	}

	/**
	 * Execution new pipeline job.
	 *
	 * @param taskId
	 * @param provider
	 */
	private void doExecutePipeline(Integer taskId, PipelineProvider provider) {
		notNull(taskId, "Pipeline taskId must not be null");
		notNull(provider, "Pipeline provider must not be null");
		log.info("Starting pipeline job for taskId: {}, provider: {}", taskId, provider.getClass().getSimpleName());

		// Setup status to running.
		taskHistoryService.updateStatus(taskId, TASK_STATUS_RUNNING);
		log.info("Updated pipeline job status to {} for {}", TASK_STATUS_RUNNING, taskId);

		// Setup Flow status to running.
		PipelineModel pipelineModel = provider.getContext().getPipelineModel();
		pipelineModel.setService(provider.getContext().getAppCluster().getName());
		pipelineModel.setProvider(provider.getContext().getTaskHistory().getProviderKind());
		pipelineModel.setStatus(RUNNING.toString());
		flowManager.pipelineStateChange(pipelineModel);

		// Starting pipeline job.
		jobExecutor.getWorker().execute(() -> {
			long startTime = currentTimeMillis();
			try {
				// Pre Pileline Execute
				log.info("Pre pipeline executing of taskId: {}, provider: {}", taskId, provider.getClass().getSimpleName());
				prePipelineExecute(taskId);

				// Execution pipeline.
				provider.execute();
				log.info("Pipeline execute completed of taskId: {}, provider: {}", taskId, provider.getClass().getSimpleName());

				// flow status
				pipelineModel.setStatus(SUCCESS.toString());
				flowManager.pipelineStateChange(pipelineModel);

				postPipelineRunSuccess(taskId, provider);
			} catch (Throwable e) {
				log.error(format("Failed to pipeline job for taskId: %s, provider: %s", taskId,
						provider.getClass().getSimpleName()), e);
				writeALineFile(config.getJobLog(taskId).getAbsoluteFile(), getStackTraceAsString(e));

				// Update status.
				TaskHistory taskHistory = taskHistoryService.getById(taskId);
				if(TASK_STATUS_STOPING == taskHistory.getStatus()){
					log.info("Updating pipeline job status to {} of taskId: {}", TASK_STATUS_STOP, taskId);
					taskHistoryService.updateStatusAndResult(taskId, TASK_STATUS_STOP, getStackTraceAsString(e));
				}else{
					log.info("Updating pipeline job status to {} of taskId: {}", TASK_STATUS_FAIL, taskId);
					taskHistoryService.updateStatusAndResult(taskId, TASK_STATUS_FAIL, getStackTraceAsString(e));
				}

				// flow status
				pipelineModel.setStatus(FAILED.toString());
				flowManager.pipelineStateChange(pipelineModel);

				// Failed process.
				log.info("Post pipeline executeing of taskId: {}, provider: {}", taskId, provider.getClass().getSimpleName());
				postPipelineRunFailure(taskId, provider, e);
			} finally {
				// Log file end EOF.
				writeALineFile(config.getJobLog(taskId).getAbsoluteFile(), LOG_FILE_END);
				log.info("Completed for pipeline taskId: {}", taskId);
				taskHistoryService.updateCostTime(taskId, (currentTimeMillis() - startTime));
				flowManager.pipelineComplete(provider.getContext().getPipelineModel());
			}
		});
	}

	/**
	 * Pre pipeline job execution successful properties process.
	 *
	 * @param taskId
	 * @param provider
	 */
	protected void prePipelineExecute(Integer taskId) {
		// For example, after the test database is imported into the production
		// database, because the primary key of the ci_task table is growing
		// automatically, there may be confusion (the current sequence value is
		// overwritten, resulting in repeated incrementing). At this time, the
		// logs of the newly created pipeline task are written additionally. In
		// order to avoid cleaning the logs, it is necessary to clear the
		// invalid log files here.
		File oldLog = config.getJobLog(taskId).getAbsoluteFile();
		if (oldLog.exists()) {
			oldLog.delete();
		}

		// Log file start EOF.
		writeBLineFile(config.getJobLog(taskId).getAbsoluteFile(), LOG_FILE_START);
	}

	/**
	 * Post pipeline job execution successful properties process.
	 * 
	 * @param taskId
	 * @param provider
	 */
	protected void postPipelineRunSuccess(Integer taskId, PipelineProvider provider) {
		List<TaskHistoryInstance> taskHistoryInstances = taskHistoryDetailDao.getDetailByTaskId(taskId);
		boolean allSuccess = true;
		boolean allFail = true;
		for (TaskHistoryInstance taskHistoryInstance : taskHistoryInstances) {
			if (taskHistoryInstance.getStatus() != TASK_STATUS_SUCCESS) {
				allSuccess = false;
			} else {
				allFail = false;
			}
		}
		if (allSuccess) {
			// Setup status to success.
			taskHistoryService.updateStatusAndResultAndSha(taskId, TASK_STATUS_SUCCESS, null, provider.getSourceFingerprint(),
					provider.getAssetsFingerprint());
			log.info("Updated pipeline job status to {} for {}", TASK_STATUS_SUCCESS, taskId);
		} else if (allFail) {
			// Setup status to success.
			taskHistoryService.updateStatusAndResultAndSha(taskId, TASK_STATUS_STOP, null, provider.getSourceFingerprint(),
					provider.getAssetsFingerprint());
			log.info("Updated pipeline job status to {} for {}", TASK_STATUS_STOP, taskId);
		} else {
			// Setup status to success.
			taskHistoryService.updateStatusAndResultAndSha(taskId, TASK_STATUS_PART_SUCCESS, null,
					provider.getSourceFingerprint(), provider.getAssetsFingerprint());
			log.info("Updated pipeline job status to {} for {}", TASK_STATUS_PART_SUCCESS, taskId);
		}

		// Successful execute job notification.
		notificationResult(provider.getContext().getTaskHistory().getContactGroupId(), taskId,"Success");
	}

	/**
	 * Post pipeline job execution failure properties process.
	 * 
	 * @param taskId
	 * @param provider
	 * @param e
	 */
	protected void postPipelineRunFailure(Integer taskId, PipelineProvider provider, Throwable e) {
		// Failure execute job notification.
		notificationResult(provider.getContext().getTaskHistory().getContactGroupId(), taskId,"Fail");
	}

	/**
	 * Notification pipeline execution result.
	 * 
	 * @param contactGroupId
	 * @param message
	 */
	protected void notificationResult(Integer contactGroupId, Integer taskId, String result) {
		try{
			List<Contact> contacts = contactDao.getContactByGroupIds(asList(contactGroupId));
			for (Contact contact : contacts) {

				// new
				List<ContactChannel> contactChannels = contact.getContactChannels();
				if (CollectionUtils.isEmpty(contactChannels)) {
					continue;
				}
				for (ContactChannel contactChannel : contactChannels) {
					if (1 != contactChannel.getEnable()) {
						continue;
					}

					GenericNotifyMessage msg = new GenericNotifyMessage(contactChannel.getPrimaryAddress(), "tpl3");
					// Common parameters.
					msg.addParameter("isSuccess", result);
					msg.addParameter("pipelineId", taskId);

					notifierAdapter.forOperator(contactChannel.getKind()).send(msg);
				}

			}
		}catch (Exception e){
			log.error("send message fail",e);
		}

	}

	/**
	 * Get task pipeline provider.
	 * 
	 * @param taskHisy
	 * @return
	 */
	protected PipelineProvider getPipelineProvider(TaskHistory taskHisy, PipelineModel pipelineModel) {
		notNull(taskHisy, "TaskHistory can not be null");

		Project project = projectDao.selectByPrimaryKey(taskHisy.getProjectId());
		notNull(project, "Project can not be null");

		AppCluster appCluster = appClusterDao.selectByPrimaryKey(project.getAppClusterId());
		notNull(appCluster, "AppCluster can not be null");
		project.setGroupName(appCluster.getName());

		List<TaskHistoryInstance> taskHisyDetails = taskHistoryService.getDetailByTaskId(taskHisy.getId());
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

		// TODO add pipeline status track
		PipelineContext context = new DefaultPipelineContext(project, projectSourceDir, appCluster, instances, taskHisy,
				refTaskHisy, taskHisyDetails, pipelineModel);

		// Get prototype provider.
		return beanFactory.getPrototypeBean(context.getTaskHistory().getProviderKind(), context);
	}

	/**
	 * Execution roll-back pipeline job.
	 *
	 * @param taskId
	 * @param provider
	 */
	protected void doRollbackPipeline(Integer taskId, PipelineProvider provider) {
		notNull(taskId, "TaskId must not be null.");
		log.info("Starting rollback pipeline job for taskId: {}, provider: {}", taskId, provider.getClass().getSimpleName());

		// Update status to running.
		taskHistoryService.updateStatus(taskId, TASK_STATUS_RUNNING);
		log.info("Updated rollback pipeline job status to {} for {}", TASK_STATUS_RUNNING, taskId);

		// Submit roll-back job.
		jobExecutor.getWorker().execute(() -> {
			try {
				// Pre Pileline Execute
				prePipelineExecute(taskId);

				// Execution roll-back pipeline.
				provider.rollback();

				// Success.
				log.info(format("Rollback pipeline job successful for taskId: %s, provider: %s", taskId,
						provider.getClass().getSimpleName()));

				postPipelineRunSuccess(taskId, provider);
				log.info("Updated rollback pipeline job status to {} for {}", TASK_STATUS_SUCCESS, taskId);
			} catch (Exception e) {
				log.error(format("Failed to rollback pipeline job for taskId: %s, provider: %s", taskId,
						provider.getClass().getSimpleName()), e);
				writeALineFile(config.getJobLog(taskId).getAbsoluteFile(), e.getMessage() + getStackTraceAsString(e));

				taskHistoryService.updateStatusAndResult(taskId, TASK_STATUS_FAIL, e.getMessage());
				log.info("Updated rollback pipeline job status to {} for {}", TASK_STATUS_FAIL, taskId);

				postPipelineRunFailure(taskId, provider, e);
			} finally {
				// Log file end EOF.
				writeALineFile(config.getJobLog(taskId).getAbsoluteFile(), LOG_FILE_END);
				log.info("Completed for rollback pipeline taskId: {}", taskId);
			}
		});
	}

}