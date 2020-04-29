package com.wl4g.devops.ci.pipeline.pcm;

import com.wl4g.devops.ci.service.PcmService;
import com.wl4g.devops.common.bean.ci.PipeHistoryPcm;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author vjay
 * @date 2020-04-27 09:29:00
 */
public class RedminePipelinePorvider implements PcmPipelineProvider {

    @Autowired
    private PcmService pcmService;

    @Override
    public void createIssues(Integer pcmId, PipeHistoryPcm pipeHistoryPcm) {
        pcmService.createIssues(pcmId, pipeHistoryPcm);
    }

}
