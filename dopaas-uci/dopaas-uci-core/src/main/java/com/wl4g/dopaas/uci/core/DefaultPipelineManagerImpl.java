/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.uci.core;

import static com.wl4g.dopaas.common.constant.UciConstants.LOG_FILE_END;
import static com.wl4g.dopaas.common.constant.UciConstants.LOG_FILE_START;
import static com.wl4g.dopaas.common.constant.UciConstants.TASK_STATUS_CREATE;
import static com.wl4g.dopaas.common.constant.UciConstants.TASK_STATUS_FAIL;
import static com.wl4g.dopaas.common.constant.UciConstants.TASK_STATUS_PART_SUCCESS;
import static com.wl4g.dopaas.common.constant.UciConstants.TASK_STATUS_RUNNING;
import static com.wl4g.dopaas.common.constant.UciConstants.TASK_STATUS_STOP;
import static com.wl4g.dopaas.common.constant.UciConstants.TASK_STATUS_STOPING;
import static com.wl4g.dopaas.common.constant.UciConstants.TASK_STATUS_SUCCESS;
import static com.wl4g.dopaas.uci.core.orchestration.DefaultOrchestrationManagerImpl.FlowStatus.FAILED;
import static com.wl4g.dopaas.uci.core.orchestration.DefaultOrchestrationManagerImpl.FlowStatus.RUNNING;
import static com.wl4g.dopaas.uci.core.orchestration.DefaultOrchestrationManagerImpl.FlowStatus.SUCCESS;
import static com.wl4g.infra.common.collection.CollectionUtils2.isEmptyArray;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.io.FileIOUtils.seekReadLines;
import static com.wl4g.infra.common.io.FileIOUtils.writeALineFile;
import static com.wl4g.infra.common.io.FileIOUtils.writeBLineFile;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.Exceptions.getStackTraceAsString;
import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.springframework.util.Assert.notNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.dopaas.cmdb.service.AppClusterService;
import com.wl4g.dopaas.cmdb.service.AppInstanceService;
import com.wl4g.dopaas.cmdb.service.DockerRepositoryService;
import com.wl4g.dopaas.common.bean.cmdb.AppCluster;
import com.wl4g.dopaas.common.bean.cmdb.AppEnvironment;
import com.wl4g.dopaas.common.bean.cmdb.AppInstance;
import com.wl4g.dopaas.common.bean.cmdb.DockerRepository;
import com.wl4g.dopaas.common.bean.uci.ClusterExtension;
import com.wl4g.dopaas.common.bean.uci.PipeStageBuilding;
import com.wl4g.dopaas.common.bean.uci.PipeStageBuildingProject;
import com.wl4g.dopaas.common.bean.uci.PipeStageInstanceCommand;
import com.wl4g.dopaas.common.bean.uci.PipeStageNotification;
import com.wl4g.dopaas.common.bean.uci.PipeStepApi;
import com.wl4g.dopaas.common.bean.uci.Pipeline;
import com.wl4g.dopaas.common.bean.uci.PipelineHistory;
import com.wl4g.dopaas.common.bean.uci.PipelineHistoryInstance;
import com.wl4g.dopaas.common.bean.uci.Project;
import com.wl4g.dopaas.common.bean.uci.model.ActionControl;
import com.wl4g.dopaas.common.bean.uci.model.PipelineModel;
import com.wl4g.dopaas.common.bean.uci.param.HookParameter;
import com.wl4g.dopaas.common.bean.uci.param.RollbackParameter;
import com.wl4g.dopaas.common.bean.uci.param.RunParameter;
import com.wl4g.dopaas.uci.config.CiProperties;
import com.wl4g.dopaas.uci.core.context.DefaultPipelineContext;
import com.wl4g.dopaas.uci.core.context.PipelineContext;
import com.wl4g.dopaas.uci.core.orchestration.OrchestrationManager;
import com.wl4g.dopaas.uci.pipeline.provider.PipelineProvider;
import com.wl4g.dopaas.uci.service.PipelineHistoryService;
import com.wl4g.dopaas.uci.service.PipelineService;
import com.wl4g.dopaas.uci.service.ProjectService;
import com.wl4g.dopaas.uci.utils.HookCommandHolder.BuildCommand;
import com.wl4g.dopaas.uci.utils.HookCommandHolder.DeployCommand;
import com.wl4g.dopaas.uci.utils.HookCommandHolder.HookCommand;
import com.wl4g.iam.service.ContactService;
import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.infra.common.io.FileIOUtils.ReadTailFrame;
import com.wl4g.infra.common.log.SmartLogger;
import com.wl4g.infra.core.bean.BaseBean;
import com.wl4g.infra.core.framework.beans.NamingPrototypeBeanFactory;
import com.wl4g.infra.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.infra.support.notification.MessageNotifier;
import com.wl4g.infra.support.notification.MessageNotifier.NotifierKind;

/**
 * Default CI/CD pipeline management implements.
 *
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version v1.0.0 2019-11-01
 * @since
 */
public class DefaultPipelineManagerImpl implements PipelineManager {

    protected final SmartLogger log = getLogger(getClass());

    protected @Autowired CiProperties config;
    protected @Autowired NamingPrototypeBeanFactory beanFactory;
    protected @Autowired GenericOperatorAdapter<NotifierKind, MessageNotifier> notifier;

    protected @Autowired PipelineJobExecutor jobExecutor;
    protected @Autowired OrchestrationManager orchestrationManager;

    protected @Autowired AppInstanceService appInstanceService;
    protected @Autowired AppClusterService appClusterService;
    protected @Autowired DockerRepositoryService dockerRepoService;
    protected @Autowired ContactService contactService;

    protected @Autowired ProjectService projectService;
    protected @Autowired PipelineService pipeService;
    protected @Autowired PipelineHistoryService pipeHistoryService;
    protected @Autowired PipelineService pipelineService;

    @Override
    public void runPipeline(RunParameter runParam) throws Exception {
        log.info("Running pipeline job for: {}", runParam);

        Pipeline pipeline = notNull(pipeService.detail(runParam.getPipeId()), "Not found task of %s", runParam.getPipeId());
        notNull(pipeline.getClusterId(), "Task clusterId must not be null.");

        PipelineHistory pipeHistory = pipeHistoryService.createRunnerPipeline(runParam);

        // Execution pipeline job.
        executePipeline(pipeHistory.getId(), getPipelineProvider(pipeHistory, runParam.getPipeModel(), null));
    }

    @Override
    public void rollbackPipeline(RollbackParameter rollback) {
        log.info("Rollback pipeline job for: {}", rollback);

        PipelineHistory pipeHistory = pipeHistoryService.createRollbackPipeline(rollback);

        // Do roll-back pipeline job.
        doRollbackPipeline(pipeHistory.getId(), getPipelineProvider(pipeHistory, rollback.getPipeModel(), null));
    }

    @Override
    public void hookPipeline(HookCommand hook) throws Exception {
        log.info("On hook pipeline job for: {}", hook);

        ActionControl ctl = new ActionControl();
        String[] appClusters = null;
        String env = null;
        if (hook instanceof DeployCommand) {
            DeployCommand deployCmd = (DeployCommand) hook;
            appClusters = deployCmd.getProjects();
            env = deployCmd.getEnv();
            ctl.setBranch(deployCmd.getBranch());
            ctl.setTest(deployCmd.isTest());
            ctl.setDeploy(true);
        } else if (hook instanceof BuildCommand) {
            BuildCommand buildCmd = (BuildCommand) hook;
            appClusters = buildCmd.getProjects();
            ctl.setBranch(buildCmd.getBranch());
            ctl.setTest(buildCmd.isTest());
            ctl.setDeploy(false);
        }
        if (isEmptyArray(appClusters)) {
            log.error("Hook projects is empty, automatic building abort!");
            return;
        }

        for (String cluster : appClusters) {
            // User default
            ClusterExtension clusterExt = pipeService.getClusterExtensionByName(cluster);
            if (Objects.nonNull(clusterExt)) {
                if (isBlank(env) && isNotBlank(clusterExt.getDefaultEnv())) {
                    env = clusterExt.getDefaultEnv();
                }
                if (isBlank(ctl.getBranch()) && isNotBlank(clusterExt.getDefaultBranch())) {
                    ctl.setBranch(clusterExt.getDefaultBranch());
                }
            }

            List<Pipeline> list = pipeService.findList(null, null, null, null, env, cluster);
            if (CollectionUtils2.isEmpty(list)) {
                continue;
            }
            Pipeline pipeline = list.get(0);
            PipelineModel pipeModel = orchestrationManager.buildPipeline(pipeline.getId());
            HookParameter hookParameter = new HookParameter();
            hookParameter.setPipeId(pipeline.getId());
            hookParameter.setRemark("Build for hook");
            PipelineHistory pipeHistory = pipeHistoryService.createHookPipeline(hookParameter);

            // Execution pipeline job.
            executePipeline(pipeHistory.getId(), getPipelineProvider(pipeHistory, pipeModel, ctl));
        }
    }

    @Override
    public ReadTailFrame logfile(Long taskHisId, Long startPos, Integer size) {
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
    public ReadTailFrame logDetailFile(Long taskHisId, Long instanceId, Long startPos, Integer size) {
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
    private void executePipeline(Long taskId, PipelineProvider provider) throws Exception {
        notNull(taskId, "Pipeline taskId must not be null");
        notNull(provider, "Pipeline provider must not be null");
        log.info("Starting pipeline job for taskId: {}, provider: {}", taskId, provider.getClass().getSimpleName());

        // Setup status to running.
        pipeHistoryService.updateStatus(taskId, TASK_STATUS_RUNNING);
        log.info("Updated pipeline job status to {} for {}", TASK_STATUS_RUNNING, taskId);

        // Setup Flow status to running.
        PipelineModel pipelineModel = provider.getContext().getPipelineModel();
        pipelineModel.setService(provider.getContext().getAppCluster().getName());
        pipelineModel.setProvider(provider.getContext().getPipeline().getProviderKind());
        pipelineModel.setStatus(RUNNING.toString());
        orchestrationManager.pipelineStateChange(pipelineModel);

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
                orchestrationManager.pipelineStateChange(pipelineModel);

                postPipelineRunSuccess(taskId, provider);

            } catch (Throwable e) {
                log.error(format("Failed to pipeline job for taskId: %s, provider: %s", taskId,
                        provider.getClass().getSimpleName()), e);
                writeALineFile(config.getJobLog(taskId).getAbsoluteFile(), getStackTraceAsString(e));

                // Update status.
                PipelineHistory pipelineHistory = pipeHistoryService.getById(taskId);
                if (TASK_STATUS_STOPING == pipelineHistory.getStatus()) {
                    log.info("Updating pipeline job status to {} of taskId: {}", TASK_STATUS_STOP, taskId);
                    pipeHistoryService.updateStatus(taskId, TASK_STATUS_STOP);
                } else {
                    log.info("Updating pipeline job status to {} of taskId: {}", TASK_STATUS_FAIL, taskId);
                    pipeHistoryService.updateStatus(taskId, TASK_STATUS_FAIL);
                }

                // flow status
                pipelineModel.setStatus(FAILED.toString());
                orchestrationManager.pipelineStateChange(pipelineModel);

                // Failed process.
                log.info("Post pipeline executeing of taskId: {}, provider: {}", taskId, provider.getClass().getSimpleName());
                postPipelineRunFailure(taskId, provider, e);
            } finally {
                // Log file end EOF.
                writeALineFile(config.getJobLog(taskId).getAbsoluteFile(), LOG_FILE_END);
                log.info("Completed for pipeline taskId: {}", taskId);
                pipeHistoryService.updateCostTime(taskId, (currentTimeMillis() - startTime));
                orchestrationManager.pipelineComplete(provider.getContext().getPipelineModel().getRunId());
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
     * @param pipeHistoryId
     * @param provider
     */
    protected void postPipelineRunSuccess(Long pipeHistoryId, PipelineProvider provider) {
        List<PipelineHistoryInstance> pipeHisInstances = pipeHistoryService.getPipeHisInstanceByPipeId(pipeHistoryId);
        boolean allSuccess = true;
        boolean allFail = true;
        for (PipelineHistoryInstance pipeHisInstance : pipeHisInstances) {
            if (pipeHisInstance.getStatus() != TASK_STATUS_SUCCESS && pipeHisInstance.getStatus() != TASK_STATUS_CREATE) {
                allSuccess = false;
            } else {
                allFail = false;
            }
        }
        if (allSuccess) {
            // Setup status to success.
            pipeHistoryService.updateStatusAndResultAndSha(pipeHistoryId, TASK_STATUS_SUCCESS, provider.getAssetsFingerprint());
            log.info("Updated pipeline job status to {} for {}", TASK_STATUS_SUCCESS, pipeHistoryId);
        } else if (allFail) {
            // Setup status to success.
            pipeHistoryService.updateStatusAndResultAndSha(pipeHistoryId, TASK_STATUS_STOP, provider.getAssetsFingerprint());
            log.info("Updated pipeline job status to {} for {}", TASK_STATUS_STOP, pipeHistoryId);
        } else {
            // Setup status to success.
            pipeHistoryService.updateStatusAndResultAndSha(pipeHistoryId, TASK_STATUS_PART_SUCCESS,
                    provider.getAssetsFingerprint());
            log.info("Updated pipeline job status to {} for {}", TASK_STATUS_PART_SUCCESS, pipeHistoryId);
        }

        // Successful execute job notification.

        notificationResult(provider.getContext().getPipeStepNotification().getContactGroupIds(), pipeHistoryId, "Success",
                provider);
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
            List<Long> groupIds = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                if (isBlank(split[i])) {
                    continue;
                }
                groupIds.add(Long.parseLong(split[i]));
            }
            if (groupIds.size() <= 0) {
                return;
            }

            // Build common parameters.
            Map<String, Object> params = new HashMap<>();
            params.put("isSuccess", result);
            params.put("pipelineId", taskId);
            params.put("projectName", provider.getContext().getProject().getProjectName());
            params.put("createDate", provider.getContext().getPipelineHistory().getCreateDate());
            params.put("costTime", currentTimeMillis() - provider.getContext().getPipelineHistory().getCreateDate().getTime());

            contactService.notification(new ContactService.NotificationParameter("tpl3", params, groupIds));
        } catch (Exception e) {
            log.error("send message fail", e);
        }

    }

    /**
     * Gets task pipeline provider.
     *
     * @param taskHisy
     * @return
     */
    protected PipelineProvider getPipelineProvider(PipelineHistory pipeHistory, PipelineModel pipelineModel,
            ActionControl actionControl) {
        notNull(pipeHistory, "TaskHistory can not be null");

        Pipeline pipe = notNullOf(pipeService.detail(pipeHistory.getPipeId()), "pipeline");
        AppCluster appCluster = notNullOf(appClusterService.detail(pipe.getClusterId()), "appCluster");

        Project project = notNullOf(projectService.getByAppClusterId(pipe.getClusterId()), "project");
        project.setGroupName(appCluster.getName());

        List<PipelineHistoryInstance> pipeHisInstances = pipeHistoryService.getPipeHisInstanceByPipeId(pipeHistory.getId());

        // Obtain instances.
        List<AppInstance> instances = safeList(pipeHisInstances).stream()
                .map(detail -> appInstanceService.detail(detail.getInstanceId()))
                .filter(instance -> nonNull(instance.getEnable()) && instance.getEnable() == BaseBean.ENABLED)
                .collect(toList());

        // New pipeline context.
        String projectSourceDir = config.getProjectSourceDir(project.getProjectName()).getAbsolutePath();

        PipeStageInstanceCommand pipeStepInstanceCommand = pipeService.getPipeInstanceById(pipe.getId());
        PipeStageNotification pipeStepNotification = pipelineService.getPipeStageNotification(pipe.getId());

        PipeStageBuilding pipeStepBuilding = pipeService.getSimplePipeStageBuilding(pipe.getId());
        setPipeStepBuildingRef(pipeStepBuilding, project.getId());

        AppEnvironment env = appClusterService.getAppClusterEnvironment(appCluster.getId(), pipe.getEnvironment());
        Long repositoryId = env.getRepositoryId();
        if (nonNull(repositoryId) && repositoryId != -1) {
            DockerRepository dockerRepo = dockerRepoService.detail(repositoryId);
            env.setDockerRepository(dockerRepo);
        } else {
            if (isNotBlank(env.getCustomRepositoryConfig())) {
                env.setDockerRepository(parseJSON(env.getCustomRepositoryConfig(), DockerRepository.class));
            }
        }

        // TODO API Document
        PipeStepApi pipeStepApi = pipelineService.getPipeStageApi(pipe.getId());

        // add pipeline status track
        PipelineContext context = new DefaultPipelineContext(project, projectSourceDir, appCluster, instances, pipeHistory,
                pipeHisInstances, pipelineModel, pipeStepInstanceCommand, pipe, pipeStepNotification, pipeStepBuilding, env,
                actionControl, pipeStepApi);

        // Get prototype provider.
        return beanFactory.getPrototypeBean(pipe.getProviderKind(), context);
    }

    /**
     * Execution roll-back pipeline job.
     *
     * @param pipeHistoryId
     * @param provider
     */
    protected void doRollbackPipeline(Long pipeHistoryId, PipelineProvider provider) {
        notNull(pipeHistoryId, "TaskId must not be null.");
        log.info("Starting rollback pipeline job for pipeHistoryId: {}, provider: {}", pipeHistoryId,
                provider.getClass().getSimpleName());

        // Update status to running.
        pipeHistoryService.updateStatus(pipeHistoryId, TASK_STATUS_RUNNING);
        log.info("Updated rollback pipeline job status to {} for {}", TASK_STATUS_RUNNING, pipeHistoryId);

        // Submit roll-back job.
        jobExecutor.getWorker().execute(() -> {
            try {
                // Pre Pileline Execute
                prePipelineExecute(pipeHistoryId);

                // Execution roll-back pipeline.
                provider.rollback();

                // Success.
                log.info(format("Rollback pipeline job successful for taskId: %s, provider: %s", pipeHistoryId,
                        provider.getClass().getSimpleName()));

                postPipelineRunSuccess(pipeHistoryId, provider);
                log.info("Updated rollback pipeline job status to {} for {}", TASK_STATUS_SUCCESS, pipeHistoryId);
            } catch (Exception e) {
                log.error(format("Failed to rollback pipeline job for taskId: %s, provider: %s", pipeHistoryId,
                        provider.getClass().getSimpleName()), e);
                writeALineFile(config.getJobLog(pipeHistoryId).getAbsoluteFile(), e.getMessage() + getStackTraceAsString(e));

                pipeHistoryService.updateStatus(pipeHistoryId, TASK_STATUS_FAIL);
                log.info("Updated rollback pipeline job status to {} for {}", TASK_STATUS_FAIL, pipeHistoryId);

                postPipelineRunFailure(pipeHistoryId, provider, e);
            } finally {
                // Log file end EOF.
                writeALineFile(config.getJobLog(pipeHistoryId).getAbsoluteFile(), LOG_FILE_END);
                log.info("Completed for rollback pipeline taskId: {}", pipeHistoryId);
            }
        });
    }

    private void setPipeStepBuildingRef(PipeStageBuilding pipeStepBuilding, Long projectId) {
        List<PipeStageBuildingProject> pipeStepBuildingProjects = pipeStepBuilding.getPipeStepBuildingProjects();
        for (PipeStageBuildingProject pipeStepBuildingProject : pipeStepBuildingProjects) {
            if (projectId.equals(pipeStepBuildingProject.getProjectId())) {
                pipeStepBuilding.setRef(pipeStepBuildingProject.getRef());
                return;
            }
        }

    }

}