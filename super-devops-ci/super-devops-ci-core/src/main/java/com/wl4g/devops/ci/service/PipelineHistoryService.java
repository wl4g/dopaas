package com.wl4g.devops.ci.service;

import com.wl4g.devops.ci.core.param.HookParameter;
import com.wl4g.devops.ci.core.param.NewParameter;
import com.wl4g.devops.ci.core.param.RollbackParameter;
import com.wl4g.devops.common.bean.ci.PipelineHistory;
import com.wl4g.devops.common.bean.ci.PipelineHistoryInstance;
import com.wl4g.devops.page.PageModel;

import java.util.List;

/**
 * @author vjay
 * @date 2020-04-27 17:24:00
 */
public interface PipelineHistoryService {

    PipelineHistory createPipelineHistory(NewParameter newParameter);

    PipelineHistory createPipelineHistory(HookParameter hookParameter);

    PipelineHistory createPipelineHistory(RollbackParameter rollbackParameter);

    void updatePipeHisInstanceStatus(int pipeInstanceId, int status);

    void updateStatus(int pipeId, int status);

    void updateStatusAndResultAndSha(int pipeId, int status, String sha);

    void stopByPipeHisId(Integer taskHisId);

    void updateCostTime(int taskId, long costTime);

    PageModel list(PageModel pm,  String pipeName, String clusterName, String environment, String startDate, String endDate, String providerKind);

    List<PipelineHistoryInstance> getPipeHisInstanceByPipeId(Integer pipeHisId);

    PipelineHistory detail(Integer pipeHisId);

    PipelineHistory getById(Integer pipeHisId);

}
