package com.wl4g.devops.ci.pipeline.pcm;

import com.wl4g.devops.common.bean.ci.PipeStepPcm;

/**
 * @author vjay
 * @date 2020-04-27 09:29:00
 */
public interface PcmPipelineProvider  {

    void createIssues(Integer pcmId, PipeStepPcm pipeStepPcm);

}
