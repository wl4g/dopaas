package com.wl4g.devops.ci.flow;

import com.wl4g.devops.ci.bean.PipelineModel;
import com.wl4g.devops.ci.bean.RunModel;
import com.wl4g.devops.ci.bean.RunModel.Pipeline;
import com.wl4g.devops.ci.core.PipelineJobExecutor;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.core.param.NewParameter;
import com.wl4g.devops.common.bean.ci.Orchestration;
import com.wl4g.devops.common.bean.ci.OrchestrationPipeline;
import com.wl4g.devops.dao.ci.OrchestrationDao;
import com.wl4g.devops.support.redis.JedisService;
import com.wl4g.devops.support.redis.ScanCursor;
import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.RunnerProperties;
import com.wl4g.devops.tool.common.lang.Assert2;
import com.wl4g.devops.tool.common.serialize.JacksonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.wl4g.devops.ci.flow.FlowManager.FlowStatus.*;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2020-03-09 09:27:00
 */
public class FlowManager {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private JedisService jedisService;

	@Autowired
	private PipelineManager pipeliner;

	@Autowired
	protected PipelineJobExecutor jobExecutor;

	@Autowired
	private OrchestrationDao orchestrationDao;

	public static final String REDIS_CI_RUN_PRE = "CI_RUN_";// redis key

	public static final int REDIS_CI_RUN_SCAN_BATCH = 10;// redis scan batch

	// TODO config
	private static String node = "master-1";
	private static int REDIS_SAVE_TIME_S = 30 * 60;// redis ttl(defautl 30 min)
	private static long FLOW_TIME_OUT_MS = 30 * 60 * 1000;// flow time
															// out(defautl
															// 30min)

	/**
	 * Start to run orchestration
	 * 
	 * @param orchestration
	 */
	public void runOrchestration(Orchestration orchestration, String remark, String taskTraceId, Integer taskTraceType,
			String annex) {
		List<OrchestrationPipeline> orchestrationPipelines = orchestration.getOrchestrationPipelines();
		List<List<OrchestrationPipeline>> orchestrationPipelinesSort = sortByPriority(orchestrationPipelines);
		List<List<PipelineModel>> pipelineModelSort = buildFlow(orchestration.getId(), orchestrationPipelinesSort);
		jobExecutor.getWorker().execute(() -> {
			try {
				handOut(pipelineModelSort, remark, taskTraceId, taskTraceType, annex);
			} catch (Exception e) {
				e.printStackTrace();
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
				if (groupBy.get(i).get(0).getPriority() > groupBy.get(j).get(0).getPriority()) {// use
																								// <
																								// to
																								// asc,
																								// use
																								// >
																								// to
																								// desc
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
	public List<List<PipelineModel>> buildFlow(Integer orchestrationId, List<List<OrchestrationPipeline>> list) {
		long currentTimeMillis = System.currentTimeMillis();
		RunModel runModel = new RunModel();
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
	public void handOut(List<List<PipelineModel>> pipelineModelSort, String remark, String taskTraceId, Integer taskTraceType,
			String annex) throws Exception {
		// Create runner.
		GenericTaskRunner<?> runner = createGenericTaskRunner(2);
		for (List<PipelineModel> pipelineModels : pipelineModelSort) { // run by
																		// batch
			List<Runnable> jobs = new ArrayList<>();
			for (PipelineModel pipelineModel : pipelineModels) {
				// pipeliner.runPipeline(new
				// NewParameter(pipelineModel.getPipeId(), null, null, null,
				// null),pipelineModel);
				// TODO set node, use defaule just now
				pipelineModel.setNode(node);
				pipelineStateChange(pipelineModel);
				// TODO hand out here
				master2slave(pipelineModel, remark, taskTraceId, taskTraceType, annex);
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

								if (!isNull(pipeline)
										&& !StringUtils.equalsAny(pipeline.getStatus(), SUCCESS.toString(), FAILED.toString())) { // RUNNING_DEPLOY
									batchFinish = false;
									break;
								}
							}
							Thread.sleep(3000L);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			// Submit jobs & listen job timeout.
			runner.getWorker().submitForComplete(jobs, (ex, completed, uncompleted) -> {

			}, FLOW_TIME_OUT_MS);
		}
		runner.close();
	}

	/**
	 *
	 * @param pipelineModel
	 */
	public void master2slave(PipelineModel pipelineModel, String remark, String taskTraceId, Integer taskTraceType,
			String annex) {
		log.info(
				"FlowManager.master2slave prarms::"
						+ "pipelineModel = {} , remark = {} , taskTraceId = {} , taskTraceType = {} , annex = {} ",
				pipelineModel, remark, taskTraceId, taskTraceType, annex);
		pipeliner.runPipeline(new NewParameter(pipelineModel.getPipeId(), remark, taskTraceId, taskTraceType, annex),
				pipelineModel);
	}

	private static GenericTaskRunner<RunnerProperties> createGenericTaskRunner(int concurrencyPoolSize) throws Exception {
		GenericTaskRunner<RunnerProperties> runner = new GenericTaskRunner<RunnerProperties>(
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
	public PipelineModel buildPipeline(Integer pipelineId) {
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
	public synchronized void pipelineStateChange(PipelineModel pipelineModel) {
		RunModel runModel = getRunModel(pipelineModel.getRunId());
		Pipeline pipeline = getPipeline(runModel.getPipelines(), pipelineModel.getPipeId());
		BeanUtils.copyProperties(pipelineModel, pipeline);
		saveRunModel(runModel);
	}

	/**
	 * when pipeline finish, del the pipeline from runModel
	 */
	public void pipelineComplete(PipelineModel pipelineModel) {
		RunModel runModel = getRunModel(pipelineModel.getRunId());
		if (isNull(runModel)) {
			return;
		}
		List<Pipeline> pipelines = runModel.getPipelines();
		if (CollectionUtils.isEmpty(pipelines)) {
			return;
		}

		boolean isAllComplete = true;
		for (Pipeline p : pipelines) {
			if (!StringUtils.equalsAnyIgnoreCase(p.getStatus(), SUCCESS.toString(), FAILED.toString())) {
				isAllComplete = false;
				break;
			}
		}
		if (isAllComplete) {
			flowComplete(runModel);
		}
	}

	/**
	 * whebn flow is complete,
	 * 
	 * @param runId
	 */
	public void flowComplete(RunModel runModel) {
		String runId = runModel.getRunId();
		// remove redis
		jedisService.del(runId);

		// TODO compute cost time
		Long createTime = runModel.getCreateTime();
		if (Objects.nonNull(createTime)) {
			log.info("flow conplete runId={},costTime={}ms", runModel.getRunId(), (System.currentTimeMillis() - createTime));
		}

		// update db status
		runId = runId.replaceAll(REDIS_CI_RUN_PRE, "");
		String[] split = runId.split("_");
		Orchestration orchestration = new Orchestration();
		orchestration.setId(Integer.valueOf(split[0]));
		orchestration.setStatus(5);
		orchestrationDao.updateByPrimaryKeySelective(orchestration);
	}

	private RunModel getRunModel(String runId) {
		return jedisService.getObjectAsJson(runId, RunModel.class);
	}

	private Pipeline getPipeline(String runId, Integer pipelineId) {
		RunModel runModel = getRunModel(runId);
		if (runModel == null) {
			return null;
		}
		return getPipeline(runModel.getPipelines(), pipelineId);
	}

	private Pipeline getPipeline(List<Pipeline> pipelines, Integer pipelineId) {
		if (!CollectionUtils.isEmpty(pipelines)) {
			for (Pipeline pipeline : pipelines) {
				if (pipelineId.intValue() == pipeline.getPipeId()) {
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
	public boolean isDependencyBuilded(String projectId) {
		List<RunModel> runModels = getRunModels();
		Set<String> alreadBuild = new HashSet<>();
		for (RunModel runModel : runModels) {
			List<Pipeline> pipelines = runModel.getPipelines();
			for (Pipeline pipeline : pipelines) {
				getAlreadyBuildModules(pipeline, alreadBuild);
			}
		}
		return alreadBuild.contains(projectId);
	}

	public boolean isPipelineRunning(Integer pipelineId) {
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

	private void getAlreadyBuildModules(Pipeline pipeline, Set<String> alreadBuild) {
		if (!StringUtils.equals(pipeline.getNode(), node)) {
			return;
		}
		List<String> modules = pipeline.getModules();
		if (CollectionUtils.isEmpty(modules)) {
			return;
		}
		if (StringUtils.equalsAnyIgnoreCase(pipeline.getStatus(), RUNNING_DEPLOY.toString(), FAILED.toString(),
				SUCCESS.toString())) {
			alreadBuild.addAll(modules);
			return;
		}
		String current = pipeline.getCurrent();
		if (StringUtils.isBlank(current)) {// if null ,it mean : not begin build
			return;
		}
		for (String module : modules) {
			if (StringUtils.equals(module, current)) {
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
