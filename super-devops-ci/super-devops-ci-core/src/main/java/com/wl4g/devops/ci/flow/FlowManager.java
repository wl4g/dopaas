package com.wl4g.devops.ci.flow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.devops.ci.bean.PipelineModel;
import com.wl4g.devops.ci.bean.RunModel;
import com.wl4g.devops.ci.bean.RunModel.Pipeline;
import com.wl4g.devops.support.redis.JedisService;
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

    private static final String RUN_FLOW_LIST = "run_flow_list";

    private static final int REDIS_SAVE_TIME = 30*60;//30 min


    /**
     * for single pipeline
     * @param pipelineId
     * @return
     */
    public PipelineModel buildPipeline(Integer pipelineId){

        long currentTimeMillis = System.currentTimeMillis();

        PipelineModel pipelineModel = new PipelineModel();
        pipelineModel.setPipeId(pipelineId);
        pipelineModel.setStatus("RUNNING");
        pipelineModel.setCreateTime(currentTimeMillis);

        RunModel runModel = new RunModel();
        runModel.setCreateTime(currentTimeMillis);
        runModel.setRunId("RUN-"+pipelineId+"-"+currentTimeMillis);
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
    public synchronized void pipelineStateChange(PipelineModel pipelineModel){
        List<RunModel> list = getRunModels();
        RunModel runModel = getRunModel(list,pipelineModel.getRunId());
        Pipeline pipeline = getPipeline(runModel.getPipelines(), pipelineModel.getPipeId());
        BeanUtils.copyProperties(pipelineModel,pipeline);
        save(list);
    }

    /**
     * when pipeline finish, del the pipeline from runModel
     */
    public void pipelineComplete(PipelineModel pipelineModel){
        List<RunModel> list = getRunModels();
        RunModel runModel = getRunModel(list,pipelineModel.getRunId());
        List<Pipeline> pipelines = runModel.getPipelines();
        if(!CollectionUtils.isEmpty(pipelines)){
            for(Pipeline pipeline : pipelines){
                if(pipelineModel.getPipeId().intValue()==pipeline.getPipeId()){
                    pipelines.remove(pipeline);
                    break;
                }
            }
        }
        if(CollectionUtils.isEmpty(pipelines)){
            flowComplete(pipelineModel.getRunId());
        }
        save(list);
    }

    /**
     * whebn flow is complete,
     * @param runId
     */
    public void flowComplete(String runId){
        List<RunModel> list = getRunModels();
        for(RunModel runModel : list){
            if(StringUtils.equals(runModel.getRunId(),runId)){
                list.remove(runModel);
                break;
            }
        }
        save(list);
    }

    private void add(RunModel runModel){
        List<RunModel> list = getRunModels();
        list.add(runModel);
        save(list);
    }

    private RunModel getRunModel(List<RunModel> list,String runId){
        for(RunModel runModel : list){
            if(StringUtils.equals(runModel.getRunId(),runId)){
                return runModel;
            }
        }
        return null;
    }

    private Pipeline getPipeline(List<Pipeline> pipelines,Integer pipelineId){
        if(!CollectionUtils.isEmpty(pipelines)){
            for(Pipeline pipeline : pipelines){
                if(pipelineId.intValue()==pipeline.getPipeId()){
                    return pipeline;
                }
            }
        }
        return null;
    }

    private List<RunModel> getRunModels(){
        String s = jedisService.get(RUN_FLOW_LIST);
        if(StringUtils.isBlank(s)){
            return new ArrayList<>();
        }
        List<RunModel> runModels = JacksonUtils.parseJSON(s, new TypeReference<List<RunModel>>() {
        });
        if(Objects.isNull(runModels)){
            runModels = new ArrayList<>();
        }
        return runModels;
    }

    private void save(List<RunModel> runModels){
        if(Objects.isNull(runModels)){
            runModels = new ArrayList<>();
        }
        jedisService.set(RUN_FLOW_LIST,JacksonUtils.toJSONString(runModels),REDIS_SAVE_TIME);
    }


    /**
     * check the dependency is already builded
     * @param pipelineModel
     * @param projectName
     * @return
     */
    public boolean isDependencyBuilded(String projectName){
        List<RunModel> runModels = getRunModels();
        Set<String> alreadBuild = new HashSet<>();
        for(RunModel runModel : runModels){
            List<Pipeline> pipelines = runModel.getPipelines();
            for(Pipeline pipeline : pipelines){
                getAlreadyBuildModules(pipeline.getModules(),pipeline.getCurrent(),alreadBuild);
            }
        }
        return alreadBuild.contains(projectName);
    }

    private void getAlreadyBuildModules(List<String> modules,String current,Set<String> alreadBuild){
        if(StringUtils.isBlank(current)){// if null ,it mean : not begin build
            return;
        }
        for(String module : modules){
            if(StringUtils.equals(module,current)){
                break;
            }else{
                alreadBuild.add(module);
            }
        }
    }




}
