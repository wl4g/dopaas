package com.wl4g.devops.ci.web;

import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.common.web.RespBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author vjay
 * @date 2020-03-06 17:36:00
 */
@RestController
@RequestMapping("/flow")
public class FlowController {

    @Autowired
    private PipelineManager pipeliner;

    @PostMapping(value = "/createFlow")
    public RespBase<?> createFlow(List<Integer> pipelineIds) {
        RespBase<Object> resp = RespBase.create();
        for(Integer pipelineId : pipelineIds){
            //pipeliner.runPipeline(new NewParameter(pipelineId, null, null, null, null));
        }


        return resp;
    }
}
