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
package com.wl4g.devops.ci.core.orchestration;

import com.wl4g.component.common.lang.Assert2;
import com.wl4g.component.common.serialize.JacksonUtils;
import com.wl4g.component.common.task.RunnerProperties;
import com.wl4g.component.support.redis.jedis.JedisService;
import com.wl4g.component.support.redis.jedis.ScanCursor;
import com.wl4g.component.support.task.ApplicationTaskRunner;
import com.wl4g.devops.ci.core.PipelineJobExecutor;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.data.OrchestrationDao;
import com.wl4g.devops.ci.data.OrchestrationHistoryDao;
import com.wl4g.devops.ci.data.PipelineHistoryDao;
import com.wl4g.devops.common.bean.ci.Orchestration;
import com.wl4g.devops.common.bean.ci.OrchestrationHistory;
import com.wl4g.devops.common.bean.ci.OrchestrationPipeline;
import com.wl4g.devops.common.bean.ci.PipelineHistory;
import com.wl4g.devops.common.bean.ci.model.PipelineModel;
import com.wl4g.devops.common.bean.ci.model.RunModel;
import com.wl4g.devops.common.bean.ci.model.RunModel.Pipeline;
import com.wl4g.devops.common.bean.ci.param.RunParameter;
import com.wl4g.devops.common.constant.CiConstants;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.ci.core.orchestration.DefaultOrchestrationManagerImpl.FlowStatus.*;
import static com.wl4g.devops.common.constant.CiConstants.*;
import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCode;
import static java.util.Objects.isNull;

/**
 * {@link DefaultOrchestrationManagerImpl}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @sine v1.0
 * @see
 */
public class DefaultOrchestrationManagerImpl implements OrchestrationManager {
	protected final Logger log = getLogger(getClass());

	@Autowired
	private JedisService jedisService;
	@Autowired
	private PipelineManager pipelineManager;
	@Autowired
	private PipelineJobExecutor jobExecutor;
	@Autowired
	private OrchestrationDao orchestrationDao;
	@Autowired
	private OrchestrationHistoryDao orchestrationHistoryDao;
	@Autowired
	private PipelineHistoryDao pipelineHistoryDao;

	/**
	 * Start to run orchestration
	 *
	 * @param orchestration
	 */
	public void runOrchestration(Orchestration orchestration, String remark, String taskTraceId, String taskTraceType,
			String annex) {
		List<OrchestrationPipeline> orchestrationPipelines = orchestration.getOrchestrationPipelines();
		List<List<OrchestrationPipeline>> orchestrationPipelinesSort = sortByPriority(orchestrationPipelines);
		RunModel runModel = new RunModel();
		List<List<PipelineModel>> pipelineModelSort = buildFlow(orchestration.getId(), orchestrationPipelinesSort, runModel);
		log.info("start hand out job");

		// insert flow history
		OrchestrationHistory orchestrationHistory = new OrchestrationHistory();
		orchestrationHistory.preInsert(getRequestOrganizationCode());
		orchestrationHistory.setRunId(runModel.getRunId());
		orchestrationHistory.setStatus(TASK_STATUS_RUNNING);
		orchestrationHistory.setInfo(JacksonUtils.toJSONString(runModel));
		orchestrationHistoryDao.insertSelective(orchestrationHistory);

		jobExecutor.getWorker().execute(() -> {
			try {
				handOut(orchestrationHistory, pipelineModelSort, runModel, remark, taskTraceId, taskTraceType, annex);
			} catch (Exception e) {
				log.error("run flow fail", e);
			}
		});
	}

	/**
	 * Step 1 : Sort By Priority
	 */
	public List<List<OrchestrationPipeline>> sortByPriority(List<OrchestrationPipeline> orchestrationPipelines) {

		// step1: group by
		List<List<OrchestrationPipeline>> groupBy = new ArrayList<>();
		for (OrchestrationPipeline orchestrationPipeline : orchestrationPipelines) {
			Integer priority = orchestrationPipeline.getPriority();
			List<OrchestrationPipeline> group = getGroup(groupBy, priority);
			if (CollectionUtils.isEmpty(group)) {
				group = new ArrayList<>();
				groupBy.add(group);
			}
			group.add(orchestrationPipeline);
		}

		// step2: sort by priority
		for (int i = 0; i < groupBy.size(); i++) {
			for (int j = i + 1; j < groupBy.size(); j++) {
				// use < to asc, use > to desc
				if (groupBy.get(i).get(0).getPriority() > groupBy.get(j).get(0).getPriority()) {
					Collections.swap(groupBy, i, j);
				}
			}
		}
		return groupBy;
	}

	/**
	 * Step 2 : build flow and it's childs(pipelines)
	 *
	 * @param orchestrationId
	 * @param list
	 * @return
	 */
	public List<List<PipelineModel>> buildFlow(Long orchestrationId, List<List<OrchestrationPipeline>> list, RunModel runModel) {
		long currentTimeMillis = System.currentTimeMillis();
		runModel.setCreateTime(currentTimeMillis);
		runModel.setRunId(REDIS_CI_RUN_PRE + orchestrationId + "_" + currentTimeMillis);
		runModel.setType("FLOW");
		List<List<PipelineModel>> pipelineSort = new ArrayList<>();
		List<Pipeline> pipelines = new ArrayList<>();
		for (List<OrchestrationPipeline> orchestrationPipelines : list) {
			List<PipelineModel> pipelineModels = new ArrayList<>();
			for (OrchestrationPipeline orchestrationPipeline : orchestrationPipelines) {
				PipelineModel pipelineModel = new PipelineModel();
				pipelineModel.setPipeId(orchestrationPipeline.getPipelineId());
				pipelineModel.setStatus(WAITING.toString());
				pipelineModel.setCreateTime(currentTimeMillis);
				pipelineModel.setRunId(runModel.getRunId());
				pipelineModel.setAttempting(1);
				pipelineModel.setPriority(orchestrationPipeline.getPriority());
				pipelines.add(pipelineModel);
				pipelineModels.add(pipelineModel);
			}
			pipelineSort.add(pipelineModels);
		}
		runModel.setPipelines(pipelines);
		saveRunModel(runModel);
		return pipelineSort;
	}

	/**
	 * Step 3 : Hand out
	 *
	 * @param lists
	 */
	public void handOut(OrchestrationHistory orchestrationHistory, List<List<PipelineModel>> pipelineModelSort, RunModel runModel,
			String remark, String taskTraceId, String taskTraceType, String annex) throws Exception {
		// Create runner.
		ApplicationTaskRunner<?> runner = createGenericTaskRunner(2);
		long startTime = System.currentTimeMillis();

		try {
			for (List<PipelineModel> pipelineModels : pipelineModelSort) { // run
																			// by
																			// batch
				List<Runnable> jobs = new ArrayList<>();
				for (PipelineModel pipelineModel : pipelineModels) {
					// TODO set node, use default just now
					pipelineModel.setNode(node);
					pipelineStateChange(pipelineModel);
					// TODO hand out here
					master2slave(orchestrationHistory, pipelineModel, remark, taskTraceId, taskTraceType, annex);
				}
				// wait for this batch finish;
				jobs.add(new Runnable() {
					@Override
					public void run() {
						try {
							boolean batchFinish = false;
							while (!batchFinish) {
								batchFinish = true;
								for (PipelineModel pipelineModel : pipelineModels) {
									// the status get from redis
									Pipeline pipeline = getPipeline(pipelineModel.getRunId(), pipelineModel.getPipeId());
									if (!isNull(pipeline) && !StringUtils.equalsAny(pipeline.getStatus(), SUCCESS.toString(),
											FAILED.toString())) { // RUNNING_DEPLOY
										batchFinish = false;
										break;
									}
								}
								Thread.sleep(3000L);
							}
						} catch (InterruptedException ie) {
							log.error("wait for this batch finish fail", ie);
						}
					}
				});
				// Submit jobs & listen job timeout.
				runner.getWorker().submitForComplete(jobs, (ex, completed, uncompleted) -> {
					log.error("error", ex);
				}, CiConstants.FLOW_TIME_OUT_MS);
			}
		} catch (Exception e) {
			log.error("flow run fail", e);
			pipelineCompleteFocus(runModel.getRunId());
			throw e;
		} finally {
			List<PipelineHistory> list = pipelineHistoryDao.list(null, null, null, null, null, null, null, 2,
					orchestrationHistory.getId());

			boolean isAllSuccess = true;
			for (PipelineHistory p : list) {
				if (p.getStatus() != 2) {
					isAllSuccess = false;
				}
			}

			OrchestrationHistory orchestrationHistoryNew = new OrchestrationHistory();
			orchestrationHistoryNew.setId(orchestrationHistory.getId());
			long endTime = System.currentTimeMillis();
			orchestrationHistoryNew.preUpdate();
			if (isAllSuccess) {
				orchestrationHistoryNew.preUpdate();
				orchestrationHistoryNew.setStatus(TASK_STATUS_SUCCESS);
			} else {
				orchestrationHistoryNew.preUpdate();
				orchestrationHistoryNew.setStatus(TASK_STATUS_FAIL);
			}
			orchestrationHistoryNew.setCostTime(endTime - startTime);
			orchestrationHistoryDao.updateByPrimaryKeySelective(orchestrationHistoryNew);
		}

		runner.close();
	}

	/**
	 * @param pipeModel
	 */
	public void master2slave(OrchestrationHistory orchestrationHistory, PipelineModel pipeModel, String remark,
			String taskTraceId, String taskTraceType, String annex) throws Exception {
		log.info(
				"FlowManager.master2slave prarms::"
						+ "pipelineModel = {} , remark = {} , taskTraceId = {} , taskTraceType = {} , annex = {} ",
				pipeModel, remark, taskTraceId, taskTraceType, annex);
		pipelineManager.runPipeline(new RunParameter(pipeModel.getPipeId(), remark, taskTraceId, taskTraceType, annex, 2,
				orchestrationHistory.getId(), pipeModel));
	}

	private static ApplicationTaskRunner<RunnerProperties> createGenericTaskRunner(int concurrencyPoolSize) throws Exception {
		ApplicationTaskRunner<RunnerProperties> runner = new ApplicationTaskRunner<RunnerProperties>(
				new RunnerProperties(false, concurrencyPoolSize)) {
		};
		runner.run(null);
		return runner;
	}

	private List<OrchestrationPipeline> getGroup(List<List<OrchestrationPipeline>> groupBy, Integer priority) {
		if (CollectionUtils.isEmpty(groupBy)) {
			return null;
		}
		for (List<OrchestrationPipeline> g : groupBy) {
			if (Objects.equals(g.get(0).getPriority(), priority)) {
				return g;
			}
		}
		return null;
	}

	/**
	 * for single pipeline
	 *
	 * @param pipelineId
	 * @return
	 */
	// TODO 这里需要添加redis锁（）jedisService.setMap()
	public PipelineModel buildPipeline(Long pipelineId) {
		// check pipeline is running
		Assert2.isTrue(!isPipelineRunning(pipelineId), "this pipeline is running, Please try later");

		long currentTimeMillis = System.currentTimeMillis();
		PipelineModel pipelineModel = new PipelineModel();
		pipelineModel.setPipeId(pipelineId);
		pipelineModel.setStatus(WAITING.toString());
		pipelineModel.setCreateTime(currentTimeMillis);
		pipelineModel.setNode(node);
		pipelineModel.setAttempting(1);
		RunModel runModel = new RunModel();
		runModel.setCreateTime(currentTimeMillis);
		runModel.setRunId(REDIS_CI_RUN_PRE + pipelineId + "_" + currentTimeMillis);
		runModel.setType("PIPE");
		List<Pipeline> pipelines = new ArrayList<>();
		pipelines.add(pipelineModel);
		runModel.setPipelines(pipelines);
		saveRunModel(runModel);
		pipelineModel.setRunId(runModel.getRunId());
		return pipelineModel;
	}

	/**
	 * when pipeline state change , call this method
	 */
	public void pipelineStateChange(PipelineModel pipelineModel) {
		RunModel runModel = getRunModel(pipelineModel.getRunId());
		Pipeline pipeline = getPipeline(runModel.getPipelines(), pipelineModel.getPipeId());
		if (pipeline != null) {
			BeanUtils.copyProperties(pipelineModel, pipeline);
		}
		saveRunModel(runModel);
	}

	/**
	 * when pipeline finish, del the pipeline from runModel
	 */
	public void pipelineComplete(String runId) {
		RunModel runModel = getRunModel(runId);
		if (isNull(runModel)) {
			return;
		}
		List<Pipeline> pipelines = runModel.getPipelines();
		if (CollectionUtils.isEmpty(pipelines)) {
			return;
		}

		boolean isAllComplete = true;
		boolean isAllSuccess = true;
		for (Pipeline p : pipelines) {
			if (!StringUtils.equalsIgnoreCase(p.getStatus(), SUCCESS.toString())) {
				isAllSuccess = false;
			}
			if (!StringUtils.equalsAnyIgnoreCase(p.getStatus(), SUCCESS.toString(), FAILED.toString())) {
				isAllComplete = false;
				break;
			}
		}
		if (isAllComplete) {
			flowComplete(runModel, isAllSuccess);
		}
	}

	public void pipelineCompleteFocus(String runId) {
		RunModel runModel = getRunModel(runId);
		if (Objects.nonNull(runModel)) {
			flowComplete(runModel, false);
		}
	}

	/**
	 * whebn flow is complete,
	 *
	 * @param runId
	 */
	public void flowComplete(RunModel runModel, boolean isAllSuccess) {
		String runId = runModel.getRunId();
		// remove redis
		jedisService.del(runId);

		// TODO compute cost time
		Long createTime = runModel.getCreateTime();
		if (Objects.nonNull(createTime)) {
			log.info("flow conplete runId={},costTime={}ms", runModel.getRunId(), (System.currentTimeMillis() - createTime));
		}

		// update db status
		String runId2 = runId.replaceAll(REDIS_CI_RUN_PRE, "");
		String[] split = runId2.split("_");
		Orchestration orchestration = new Orchestration();
		orchestration.setId(Long.valueOf(split[0]));
		orchestration.setStatus(5);
		orchestrationDao.updateByPrimaryKeySelective(orchestration);
	}

	private RunModel getRunModel(String runId) {
		return jedisService.getObjectAsJson(runId, RunModel.class);
	}

	private Pipeline getPipeline(String runId, Long pipelineId) {
		RunModel runModel = getRunModel(runId);
		if (runModel == null) {
			return null;
		}
		return getPipeline(runModel.getPipelines(), pipelineId);
	}

	private Pipeline getPipeline(List<Pipeline> pipelines, Long pipelineId) {
		if (!CollectionUtils.isEmpty(pipelines)) {
			for (Pipeline pipeline : pipelines) {
				if (pipelineId.longValue() == pipeline.getPipeId()) {
					return pipeline;
				}
			}
		}
		return null;
	}

	private List<RunModel> getRunModels() {
		ScanCursor<String> scan = jedisService.scan(REDIS_CI_RUN_PRE + "*", REDIS_CI_RUN_SCAN_BATCH, String.class);
		List<RunModel> runModels = new ArrayList<>();

		while (scan.hasNext()) {
			String next = scan.next();
			runModels.add(JacksonUtils.parseJSON(next, RunModel.class));
		}
		/*
		 * List<RunModel> runModels = JacksonUtils.parseJSON(s, new
		 * TypeReference<List<RunModel>>() { });
		 */
		if (isNull(runModels)) {
			runModels = new ArrayList<>();
		}
		return runModels;
	}

	private void saveRunModel(RunModel runModel) {
		jedisService.set(runModel.getRunId(), JacksonUtils.toJSONString(runModel), REDIS_SAVE_TIME_S);
	}

	/**
	 * check the dependency is already builded
	 *
	 * @param pipelineModel
	 * @param projectName
	 * @return
	 */
	public boolean isDependencyBuilded(Long projectId) {
		List<RunModel> runModels = getRunModels();
		Set<Long> alreadBuild = new HashSet<>();
		for (RunModel runModel : runModels) {
			List<Pipeline> pipelines = runModel.getPipelines();
			for (Pipeline pipeline : pipelines) {
				getAlreadyBuildModules(pipeline, alreadBuild);
			}
		}
		return alreadBuild.contains(projectId);
	}

	public boolean isPipelineRunning(Long pipelineId) {
		if (isNull(pipelineId)) {
			return false;
		}
		List<RunModel> runModels = getRunModels();
		for (RunModel runModel : runModels) {
			List<Pipeline> pipelines = runModel.getPipelines();
			for (Pipeline pipeline : pipelines) {
				if (pipelineId.equals(pipeline.getPipeId())) {
					return true;
				}
			}
		}
		return false;
	}

	private void getAlreadyBuildModules(Pipeline pipeline, Set<Long> alreadBuild) {
		if (!StringUtils.equals(pipeline.getNode(), node)) {
			return;
		}
		List<Long> modules = new ArrayList<>();

		if (CollectionUtils.isEmpty(pipeline.getModulesPorjects())) {
			return;
		}

		pipeline.getModulesPorjects().forEach((modulesPorject) -> {
			modules.add(modulesPorject.getProjectId());
		});

		if (CollectionUtils.isEmpty(modules)) {
			return;
		}
		// TODO FAILED --> throw
		if (StringUtils.equalsAnyIgnoreCase(pipeline.getStatus(), RUNNING_DEPLOY.toString(), FAILED.toString(),
				SUCCESS.toString())) {
			alreadBuild.addAll(modules);
			return;
		}
		Long current = pipeline.getCurrent();
		if (isNull(current)) {// if null ,it mean : not begin build
			return;
		}
		for (Long module : modules) {
			if (current.equals(module)) {
				break;
			} else {
				alreadBuild.add(module);
			}
		}
	}

	public static enum FlowStatus {
		WAITING, RUNNING, RUNNING_BUILD, RUNNING_DEPLOY, FAILED, SUCCESS;

		/**
		 * Converter string to {@link FlowStatus}
		 *
		 * @param stauts
		 * @return
		 */
		public static FlowStatus of(String stauts) {
			FlowStatus wh = safeOf(stauts);
			if (wh == null) {
				throw new IllegalArgumentException(String.format("Illegal action '%s'", stauts));
			}
			return wh;
		}

		/**
		 * Safe converter string to {@link FlowStatus}
		 *
		 * @param stauts
		 * @return
		 */
		public static FlowStatus safeOf(String stauts) {
			for (FlowStatus t : values()) {
				if (String.valueOf(stauts).equalsIgnoreCase(t.name())) {
					return t;
				}
			}
			return null;
		}
	}

}