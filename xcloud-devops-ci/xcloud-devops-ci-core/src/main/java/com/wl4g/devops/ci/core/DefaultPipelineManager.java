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

import com.wl4g.components.common.collection.CollectionUtils2;
import com.wl4g.components.common.io.FileIOUtils.*;
import com.wl4g.components.common.serialize.JacksonUtils;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.core.bean.ci.*;
import com.wl4g.components.core.bean.erm.AppCluster;
import com.wl4g.components.core.bean.erm.AppEnvironment;
import com.wl4g.components.core.bean.erm.AppInstance;
import com.wl4g.components.core.bean.erm.DockerRepository;
import com.wl4g.components.core.bean.iam.Contact;
import com.wl4g.components.core.bean.iam.ContactChannel;
import com.wl4g.components.core.framework.beans.NamingPrototypeBeanFactory;
import com.wl4g.components.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.components.support.notification.GenericNotifyMessage;
import com.wl4g.components.support.notification.MessageNotifier;
import com.wl4g.components.support.notification.MessageNotifier.NotifierKind;
import com.wl4g.devops.ci.bean.ActionControl;
import com.wl4g.devops.ci.bean.PipelineModel;
import com.wl4g.devops.ci.config.CiProperties;
import com.wl4g.devops.ci.core.context.DefaultPipelineContext;
import com.wl4g.devops.ci.core.context.PipelineContext;
import com.wl4g.devops.ci.core.param.HookParameter;
import com.wl4g.devops.ci.core.param.RunParameter;
import com.wl4g.devops.ci.core.param.RollbackParameter;
import com.wl4g.devops.ci.flow.FlowManager;
import com.wl4g.devops.ci.pipeline.PipelineProvider;
import com.wl4g.devops.ci.service.PipelineHistoryService;
import com.wl4g.devops.ci.utils.HookCommandHolder;
import com.wl4g.devops.dao.ci.*;
import com.wl4g.devops.dao.erm.AppClusterDao;
import com.wl4g.devops.dao.erm.AppEnvironmentDao;
import com.wl4g.devops.dao.erm.AppInstanceDao;
import com.wl4g.devops.dao.erm.DockerRepositoryDao;
import com.wl4g.devops.dao.iam.ContactDao;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wl4g.components.common.collection.Collections2.safeList;
import static com.wl4g.components.common.io.FileIOUtils.*;
import static com.wl4g.components.common.lang.Exceptions.getStackTraceAsString;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.core.constants.CiDevOpsConstants.*;
import static com.wl4g.devops.ci.flow.FlowManager.FlowStatus.*;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
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
	final protected Logger log = getLogger(getClass());

	@Autowired
	protected CiProperties config;
	@Autowired
	protected NamingPrototypeBeanFactory beanFactory;
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
	protected TaskDetailDao taskDetailDao;
	@Autowired
	protected ContactDao contactDao;
	@Autowired
	protected TaskBuildCommandDao taskBuildCmdDao;
	@Autowired
	protected FlowManager flowManager;
	@Autowired
	private PipelineDao pipelineDao;
	@Autowired
	private PipelineHistoryService pipelineHistoryService;
	@Autowired
	private PipelineHistoryInstanceDao pipelineHistoryInstanceDao;
	@Autowired
	private PipeStepInstanceCommandDao pipeStepInstanceCommandDao;
	@Autowired
	private PipeStepNotificationDao pipeStepNotificationDao;
	@Autowired
	private PipeStepBuildingDao pipeStepBuildingDao;
	@Autowired
	private AppEnvironmentDao appEnvironmentDao;
	@Autowired
	private DockerRepositoryDao dockerRepositoryDao;
	@Autowired
	private ClusterExtensionDao clusterExtensionDao;

	@Override
	public void runPipeline(RunParameter param, PipelineModel pipelineModel) throws Exception {
		log.info("Running pipeline job for: {}", param);

		Pipeline pipeline = pipelineDao.selectByPrimaryKey(param.getPipeId());

		notNull(pipeline, String.format("Not found task of %s", param.getPipeId()));
		notNull(pipeline.getClusterId(), "Task clusterId must not be null.");
		AppCluster appCluster = appClusterDao.selectByPrimaryKey(pipeline.getClusterId());
		notNull(appCluster, "not found this app");

		PipelineHistory pipelineHistory = pipelineHistoryService.createRunnerPipeline(param);

		// Execution pipeline job.
		doExecutePipeline(pipelineHistory.getId(), getPipelineProvider(pipelineHistory, pipelineModel, null));
	}

	@Override
	public void rollbackPipeline(RollbackParameter param, PipelineModel pipelineModel) {
		log.info("Rollback pipeline job for: {}", param);

		PipelineHistory pipelineHistory = pipelineHistoryService.createRollbackPipeline(param);

		// Do roll-back pipeline job.
		doRollbackPipeline(pipelineHistory.getId(), getPipelineProvider(pipelineHistory, pipelineModel, null));
	}

	@Override
	public void hookPipeline(HookCommandHolder.HookCommand hookCommand) throws Exception {
		log.info("On hook pipeline job for: {}", hookCommand);

		ActionControl actionControl = new ActionControl();
		String[] projects = null;
		String env = null;

		if (hookCommand instanceof HookCommandHolder.DeployCommand) {
			HookCommandHolder.DeployCommand deployCommand = (HookCommandHolder.DeployCommand) hookCommand;
			projects = deployCommand.getProjects();
			env = deployCommand.getEnv();
			actionControl.setBranch(deployCommand.getBranch());
			actionControl.setTest(deployCommand.isTest());
			actionControl.setDeploy(true);
		} else if (hookCommand instanceof HookCommandHolder.BuildCommand) {
			HookCommandHolder.BuildCommand buildCommand = (HookCommandHolder.BuildCommand) hookCommand;
			projects = buildCommand.getProjects();
			actionControl.setBranch(buildCommand.getBranch());
			actionControl.setTest(buildCommand.isTest());
			actionControl.setDeploy(false);
		}
		if (projects == null || projects.length < 1) {
			log.error("project is empty");
			return;
		}

		for (String project : projects) {

			// User default
			ClusterExtension clusterExtension = clusterExtensionDao.selectByClusterName(project);
			if (Objects.nonNull(clusterExtension)) {
				if (StringUtils.isBlank(env) && StringUtils.isNotBlank(clusterExtension.getDefaultEnv())) {
					env = clusterExtension.getDefaultEnv();
				}
				if (StringUtils.isBlank(actionControl.getBranch())
						&& StringUtils.isNotBlank(clusterExtension.getDefaultBranch())) {
					actionControl.setBranch(clusterExtension.getDefaultBranch());
				}
			}

			List<Pipeline> list = pipelineDao.list(null, null, null, null, env, project);
			if (CollectionUtils2.isEmpty(list)) {
				continue;
			}
			Pipeline pipeline = list.get(0);
			PipelineModel pipelineModel = flowManager.buildPipeline(pipeline.getId());
			HookParameter hookParameter = new HookParameter();
			hookParameter.setPipeId(pipeline.getId());
			hookParameter.setRemark("hook");
			PipelineHistory pipelineHistory = pipelineHistoryService.createHookPipeline(hookParameter);

			// Execution pipeline job.
			doExecutePipeline(pipelineHistory.getId(), getPipelineProvider(pipelineHistory, pipelineModel, actionControl));
		}
	}

	@Override
	public ReadResult logfile(Long taskHisId, Long startPos, Integer size) {
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
	public ReadResult logDetailFile(Long taskHisId, Long instanceId, Long startPos, Integer size) {
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
	private void doExecutePipeline(Long taskId, PipelineProvider provider) throws Exception {
		notNull(taskId, "Pipeline taskId must not be null");
		notNull(provider, "Pipeline provider must not be null");
		log.info("Starting pipeline job for taskId: {}, provider: {}", taskId, provider.getClass().getSimpleName());

		// Setup status to running.
		pipelineHistoryService.updateStatus(taskId, TASK_STATUS_RUNNING);
		log.info("Updated pipeline job status to {} for {}", TASK_STATUS_RUNNING, taskId);

		// Setup Flow status to running.
		PipelineModel pipelineModel = provider.getContext().getPipelineModel();
		pipelineModel.setService(provider.getContext().getAppCluster().getName());
		pipelineModel.setProvider(provider.getContext().getPipeline().getProviderKind());
		pipelineModel.setStatus(RUNNING.toString());
		flowManager.pipelineStateChange(pipelineModel);

		// Starting pipeline job.
		jobExecutor.getWorker().execute(() -> {
			long startTime = currentTimeMillis();
			try {
				/*
				 * if(taskId>0){//TODO just for test throw new
				 * CiException("Test Throw Exception"); }
				 */

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
				PipelineHistory pipelineHistory = pipelineHistoryService.getById(taskId);
				if (TASK_STATUS_STOPING == pipelineHistory.getStatus()) {
					log.info("Updating pipeline job status to {} of taskId: {}", TASK_STATUS_STOP, taskId);
					pipelineHistoryService.updateStatus(taskId, TASK_STATUS_STOP);
				} else {
					log.info("Updating pipeline job status to {} of taskId: {}", TASK_STATUS_FAIL, taskId);
					pipelineHistoryService.updateStatus(taskId, TASK_STATUS_FAIL);
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
				pipelineHistoryService.updateCostTime(taskId, (currentTimeMillis() - startTime));
				flowManager.pipelineComplete(provider.getContext().getPipelineModel().getRunId());
			}
		});
	}

	/**
	 * Pre pipeline job execution successful properties process.
	 *
	 * @param taskId
	 * @param provider
	 */
	protected void prePipelineExecute(Long taskId) {
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
	protected void postPipelineRunSuccess(Long taskId, PipelineProvider provider) {
		List<PipelineHistoryInstance> pipelineHistoryInstances = pipelineHistoryInstanceDao.selectByPipeHistoryId(taskId);
		boolean allSuccess = true;
		boolean allFail = true;
		for (PipelineHistoryInstance pipelineHistoryInstance : pipelineHistoryInstances) {
			if (pipelineHistoryInstance.getStatus() != TASK_STATUS_SUCCESS
					&& pipelineHistoryInstance.getStatus() != TASK_STATUS_CREATE) {
				allSuccess = false;
			} else {
				allFail = false;
			}
		}
		if (allSuccess) {
			// Setup status to success.
			pipelineHistoryService.updateStatusAndResultAndSha(taskId, TASK_STATUS_SUCCESS, provider.getAssetsFingerprint());
			log.info("Updated pipeline job status to {} for {}", TASK_STATUS_SUCCESS, taskId);
		} else if (allFail) {
			// Setup status to success.
			pipelineHistoryService.updateStatusAndResultAndSha(taskId, TASK_STATUS_STOP, provider.getAssetsFingerprint());
			log.info("Updated pipeline job status to {} for {}", TASK_STATUS_STOP, taskId);
		} else {
			// Setup status to success.
			pipelineHistoryService.updateStatusAndResultAndSha(taskId, TASK_STATUS_PART_SUCCESS, provider.getAssetsFingerprint());
			log.info("Updated pipeline job status to {} for {}", TASK_STATUS_PART_SUCCESS, taskId);
		}

		// Successful execute job notification.

		notificationResult(provider.getContext().getPipeStepNotification().getContactGroupIds(), taskId, "Success", provider);
	}

	/**
	 * Post pipeline job execution failure properties process.
	 *
	 * @param taskId
	 * @param provider
	 * @param e
	 */
	protected void postPipelineRunFailure(Long taskId, PipelineProvider provider, Throwable e) {
		// Failure execute job notification.
		notificationResult(provider.getContext().getPipeStepNotification().getContactGroupIds(), taskId, "Fail", provider);
	}

	/**
	 * Notification pipeline execution result.
	 *
	 * @param contactGroupId
	 * @param message
	 */
	protected void notificationResult(String contactGroupIds, Long taskId, String result, PipelineProvider provider) {
		try {
			String[] split = contactGroupIds.split(",");
			List<Long> ints = new ArrayList<>();
			for (int i = 0; i < split.length; i++) {
				if (StringUtils.isBlank(split[i])) {
					continue;
				}
				ints.add(Long.parseLong(split[i]));
			}
			if (ints.size() <= 0) {
				return;
			}

			List<Contact> contacts = contactDao.getContactByGroupIds(ints);
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
					msg.addParameter("projectName", provider.getContext().getProject().getProjectName());
					msg.addParameter("createDate", provider.getContext().getPipelineHistory().getCreateDate());
					msg.addParameter("costTime",
							currentTimeMillis() - provider.getContext().getPipelineHistory().getCreateDate().getTime());

					notifierAdapter.forOperator(contactChannel.getKind()).send(msg);
				}

			}
		} catch (Exception e) {
			log.error("send message fail", e);
		}

	}

	/**
	 * Get task pipeline provider.
	 *
	 * @param taskHisy
	 * @return
	 */
	protected PipelineProvider getPipelineProvider(PipelineHistory pipelineHistory, PipelineModel pipelineModel,
			ActionControl actionControl) {
		notNull(pipelineHistory, "TaskHistory can not be null");

		Pipeline pipeline = pipelineDao.selectByPrimaryKey(pipelineHistory.getPipeId());

		AppCluster appCluster = appClusterDao.selectByPrimaryKey(pipeline.getClusterId());
		notNull(appCluster, "AppCluster can not be null");

		Project project = projectDao.getByAppClusterId(pipeline.getClusterId());
		notNull(project, "Project can not be null");
		project.setGroupName(appCluster.getName());

		List<PipelineHistoryInstance> pipelineHistoryInstances = pipelineHistoryInstanceDao
				.selectByPipeHistoryId(pipelineHistory.getId());

		// Obtain instances.
		List<AppInstance> instances = safeList(pipelineHistoryInstances).stream()
				.map(detail -> appInstanceDao.selectByPrimaryKey(detail.getInstanceId()))
				.filter(instance -> nonNull(instance.getEnable()) && instance.getEnable() == BaseBean.ENABLED).collect(toList());

		// New pipeline context.
		String projectSourceDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();

		PipeStepInstanceCommand pipeStepInstanceCommand = pipeStepInstanceCommandDao.selectByPipeId(pipeline.getId());

		PipeStepNotification pipeStepNotification = pipeStepNotificationDao.selectByPipeId(pipeline.getId());

		PipeStepBuilding pipeStepBuilding = pipeStepBuildingDao.selectByPipeId(pipeline.getId());
		setPipeStepBuildingRef(pipeStepBuilding, project.getId());

		AppEnvironment environment = appEnvironmentDao.selectByClusterIdAndEnv(appCluster.getId(), pipeline.getEnvironment());
		Long repositoryId = environment.getRepositoryId();
		if (nonNull(repositoryId) && repositoryId != -1) {
			DockerRepository dockerRepository = dockerRepositoryDao.selectByPrimaryKey(repositoryId);
			environment.setDockerRepository(dockerRepository);
		} else {
			if (StringUtils.isNotBlank(environment.getCustomRepositoryConfig())) {
				DockerRepository dockerRepository = JacksonUtils.parseJSON(environment.getCustomRepositoryConfig(),
						DockerRepository.class);
				environment.setDockerRepository(dockerRepository);
			}
		}

		// TODO add pipeline status track
		PipelineContext context = new DefaultPipelineContext(project, projectSourceDir, appCluster, instances, pipelineHistory,
				pipelineHistoryInstances, pipelineModel, pipeStepInstanceCommand, pipeline, pipeStepNotification,
				pipeStepBuilding, environment, actionControl);

		// Get prototype provider.
		return beanFactory.getPrototypeBean(pipeline.getProviderKind(), context);
	}

	/**
	 * Execution roll-back pipeline job.
	 *
	 * @param taskId
	 * @param provider
	 */
	protected void doRollbackPipeline(Long taskId, PipelineProvider provider) {
		notNull(taskId, "TaskId must not be null.");
		log.info("Starting rollback pipeline job for taskId: {}, provider: {}", taskId, provider.getClass().getSimpleName());

		// Update status to running.
		pipelineHistoryService.updateStatus(taskId, TASK_STATUS_RUNNING);
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

				pipelineHistoryService.updateStatus(taskId, TASK_STATUS_FAIL);
				log.info("Updated rollback pipeline job status to {} for {}", TASK_STATUS_FAIL, taskId);

				postPipelineRunFailure(taskId, provider, e);
			} finally {
				// Log file end EOF.
				writeALineFile(config.getJobLog(taskId).getAbsoluteFile(), LOG_FILE_END);
				log.info("Completed for rollback pipeline taskId: {}", taskId);
			}
		});
	}

	private void setPipeStepBuildingRef(PipeStepBuilding pipeStepBuilding, Long projectId) {
		List<PipeStepBuildingProject> pipeStepBuildingProjects = pipeStepBuilding.getPipeStepBuildingProjects();
		for (PipeStepBuildingProject pipeStepBuildingProject : pipeStepBuildingProjects) {
			if (projectId.equals(pipeStepBuildingProject.getProjectId())) {
				pipeStepBuilding.setRef(pipeStepBuildingProject.getRef());
				return;
			}
		}

	}

}