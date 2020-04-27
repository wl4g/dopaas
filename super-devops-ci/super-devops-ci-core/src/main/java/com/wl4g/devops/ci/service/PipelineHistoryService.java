package com.wl4g.devops.ci.service;

/**
 * @author vjay
 * @date 2020-04-27 17:24:00
 */
public interface PipelineHistoryService {

    void savePipelineHistory(Integer pipeId, String trackId, Integer trackType, String remark, String annex);


}
