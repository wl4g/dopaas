package com.wl4g.devops.ci.service;

import com.wl4g.devops.ci.core.param.HookParameter;
import com.wl4g.devops.ci.core.param.NewParameter;
import com.wl4g.devops.ci.core.param.RollbackParameter;
import com.wl4g.devops.common.bean.ci.PipelineHistory;

/**
 * @author vjay
 * @date 2020-04-27 17:24:00
 */
public interface PipelineHistoryService {

    PipelineHistory createPipelineHistory(NewParameter newParameter);

    PipelineHistory createPipelineHistory(HookParameter hookParameter);

    PipelineHistory createPipelineHistory(RollbackParameter rollbackParameter);


}
