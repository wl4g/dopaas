package com.wl4g.devops.ci.flow;

import com.fasterxml.jackson.core.type.TypeReference;
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
import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.RunnerProperties;
import com.wl4g.devops.tool.common.serialize.JacksonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author vjay
 * @date 2020-03-09 09:27:00
 */
public class FlowManager {

	@Autowired
	private JedisService jedisService;

    @Autowired
    private PipelineManager pipeliner;

	@Autowired
	protected PipelineJobExecutor jobExecutor;

	@Autowired
	private OrchestrationDao orchestrationDao;

	private static final String RUN_FLOW_LIST = "run_flow_list";
	
	//TODO config
	private static String node = "master-1";
	private static int REDIS_SAVE_TIME_S = 30 * 60;//TODO defautl 30 min (300min just for debug)
	private static long FLOW_TIME_OUT_MS = 300 * 60 * 1000;//TODO defautl 30min (300min just for debug)
	

    public void gateway(Orchestration orchestration) {
		List<OrchestrationPipeline> orchestrationPipelines = orchestration.getOrchestrationPipelines();
        List<List<OrchestrationPipeline>> orchestrationPipelinesSort = sortByPriority(orchestrationPipelines);
        List<List<PipelineModel>> pipelineModelSort = buildFlow(orchestration.getId(), orchestrationPipelinesSort);

		jobExecutor.getWorker().execute(() -> {
			try {
				handOut(pipelineModelSort);
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
				if (groupBy.get(i).get(0).getPriority() > groupBy.get(j).get(0).getPriority()) {// use < to asc, use > to desc
					Collections.swap(groupBy, i, j);
				}
			}
		}
		return groupBy;
	}

    /**
     * Step 2 : build flow and it's childs(pipelines)
     * @param orchestrationId
     * @param list
     * @return
     */
    public List<List<PipelineModel>> buildFlow(Integer orchestrationId, List<List<OrchestrationPipeline>> list){

        long currentTimeMillis = System.currentTimeMillis();
        RunModel runModel = new RunModel();
        runModel.setCreateTime(currentTimeMillis);
        runModel.setRunId("RUN-" + orchestrationId + "-" + currentTimeMillis);
        runModel.setType("FLOW");

        List<List<PipelineModel>> pipelineSort = new ArrayList<>();
        List<Pipeline> pipelines = new ArrayList<>();

        for(List<OrchestrationPipeline> orchestrationPipelines : list){
            List<PipelineModel> pipelineModels = new ArrayList<>();
            for(OrchestrationPipeline orchestrationPipeline : orchestrationPipelines){
                PipelineModel pipelineModel = new PipelineModel();
                pipelineModel.setPipeId(orchestrationPipeline.getPipelineId());
                pipelineModel.setStatus("WAITING");
                pipelineModel.setCreateTime(currentTimeMillis);
                pipelineModel.setRunId(runModel.getRunId());
                pipelineModel.setPriority(orchestrationPipeline.getPriority());

                pipelines.add(pipelineModel);
                pipelineModels.add(pipelineModel);
            }
            pipelineSort.add(pipelineModels);
        }

        runModel.setPipelines(pipelines);
        add(runModel);

        return pipelineSort;
    }

    /**
     * Step 2 : Hand out
     * @param lists
     */
    public void handOut(List<List<PipelineModel>> pipelineModelSort) throws Exception {
        // Create runner.
        GenericTaskRunner runner = createGenericTaskRunner(2);
        for(List<PipelineModel> pipelineModels : pipelineModelSort){ // run by batch

            List<Runnable> jobs = new ArrayList<>();
            for(PipelineModel pipelineModel : pipelineModels){
                //pipeliner.runPipeline(new NewParameter(pipelineModel.getPipeId(), null, null, null, null),pipelineModel);
				//TODO set node, use defaule just now
				pipelineModel.setNode(node);
				pipelineStateChange(pipelineModel);
                master2slave(pipelineModel);
            }

            //wait for this batch finish;
            jobs.add(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean batchFinish = false;
                        while(!batchFinish){
							batchFinish = true;
                            for(PipelineModel pipelineModel : pipelineModels){
                                // the status get from redis
								Pipeline pipeline = getPipeline(pipelineModel.getRunId(), pipelineModel.getPipeId());

								if(!Objects.isNull(pipeline)&&!StringUtils.equalsAny(pipeline.getStatus(),"SUCCESS","FAILED")){
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
    public void master2slave(PipelineModel pipelineModel){
        pipeliner.runPipeline(new NewParameter(pipelineModel.getPipeId(), "generate by system", null, null, null),pipelineModel);
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

		long currentTimeMillis = System.currentTimeMillis();

		PipelineModel pipelineModel = new PipelineModel();
		pipelineModel.setPipeId(pipelineId);
		pipelineModel.setStatus("RUNNING");
		pipelineModel.setCreateTime(currentTimeMillis);
		pipelineModel.setNode(node);

		RunModel runModel = new RunModel();
		runModel.setCreateTime(currentTimeMillis);
		runModel.setRunId("RUN-" + pipelineId + "-" + currentTimeMillis);
		runModel.setType("PIPE");

		List<Pipeline> pipelines = new ArrayList<>();
		pipelines.add(pipelineModel);
		runModel.setPipelines(pipelines);

		add(runModel);

		pipelineModel.setRunId(runModel.getRunId());

		return pipelineModel;
	}

	/**
	 * when pipeline state change , call this method
	 */
	public synchronized void pipelineStateChange(PipelineModel pipelineModel) {
		List<RunModel> list = getRunModels();
		RunModel runModel = getRunModel(list, pipelineModel.getRunId());
		Pipeline pipeline = getPipeline(runModel.getPipelines(), pipelineModel.getPipeId());
		BeanUtils.copyProperties(pipelineModel, pipeline);
		save(list);
	}

	/**
	 * when pipeline finish, del the pipeline from runModel
	 */
	public void pipelineComplete(PipelineModel pipelineModel) {
		List<RunModel> list = getRunModels();
		RunModel runModel = getRunModel(list, pipelineModel.getRunId());
		if(Objects.isNull(runModel)){
		    return;
        }
		List<Pipeline> pipelines = runModel.getPipelines();
		if (CollectionUtils.isEmpty(pipelines)) {
            return;
        }
        for (Pipeline pipeline : pipelines) {
            if (pipelineModel.getPipeId().intValue() == pipeline.getPipeId()) {
                //pipelines.remove(pipeline);
                pipeline.setStatus("SUCCESS");
                break;
            }
        }
        save(list);

        boolean isAllSuccess = true;
		for(Pipeline pipeline : pipelines){
            if(!StringUtils.equals(pipeline.getStatus(),"SUCCESS")){
                isAllSuccess = false;
                break;
            }
        }
		if (isAllSuccess) {
			flowComplete(pipelineModel.getRunId());
		}

	}

	/**
	 * whebn flow is complete,
	 * 
	 * @param runId
	 */
	public void flowComplete(String runId) {
		List<RunModel> list = getRunModels();
		for (RunModel runModel : list) {
			if (StringUtils.equals(runModel.getRunId(), runId)) {
				list.remove(runModel);
				break;
			}
		}
		save(list);

		String[] split = runId.split("-");
		Orchestration orchestration = new Orchestration();
		orchestration.setId(Integer.valueOf(split[1]));
		orchestration.setStatus(5);
		orchestrationDao.updateByPrimaryKeySelective(orchestration);
	}

	private void add(RunModel runModel) {
		List<RunModel> list = getRunModels();
		list.add(runModel);
		save(list);
	}

	private RunModel getRunModel(List<RunModel> list, String runId) {
		for (RunModel runModel : list) {
			if (StringUtils.equals(runModel.getRunId(), runId)) {
				return runModel;
			}
		}
		return null;
	}

	private Pipeline getPipeline(String runId,Integer pipelineId){
		List<RunModel> list = getRunModels();
		RunModel runModel = getRunModel(list, runId);
		if(runModel == null){
			return null;
		}
		return getPipeline(runModel.getPipelines(),pipelineId);
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
		String s = jedisService.get(RUN_FLOW_LIST);
		if (StringUtils.isBlank(s)) {
			return new ArrayList<>();
		}
		List<RunModel> runModels = JacksonUtils.parseJSON(s, new TypeReference<List<RunModel>>() {
		});
		if (Objects.isNull(runModels)) {
			runModels = new ArrayList<>();
		}
		return runModels;
	}

	private void save(List<RunModel> runModels) {
		if (Objects.isNull(runModels)) {
			runModels = new ArrayList<>();
		}
		jedisService.set(RUN_FLOW_LIST, JacksonUtils.toJSONString(runModels), REDIS_SAVE_TIME_S);
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
				if(!StringUtils.equals(pipeline.getNode(),node)){
					continue;
				}
				getAlreadyBuildModules(pipeline.getModules(), pipeline.getCurrent(), alreadBuild);
			}
		}
		return alreadBuild.contains(projectId);
	}

	private void getAlreadyBuildModules(List<String> modules, String current, Set<String> alreadBuild) {
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

}
