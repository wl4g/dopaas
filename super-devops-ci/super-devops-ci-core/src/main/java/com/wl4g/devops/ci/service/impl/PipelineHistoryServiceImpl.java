package com.wl4g.devops.ci.service.impl;

import com.wl4g.devops.ci.core.param.HookParameter;
import com.wl4g.devops.ci.core.param.NewParameter;
import com.wl4g.devops.ci.core.param.RollbackParameter;
import com.wl4g.devops.ci.service.PipelineHistoryService;
import com.wl4g.devops.common.bean.ci.Pipeline;
import com.wl4g.devops.common.bean.ci.PipelineHistory;
import com.wl4g.devops.dao.ci.PipelineDao;
import com.wl4g.devops.dao.ci.PipelineHistoryDao;
import com.wl4g.devops.tool.common.lang.Assert2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_STATUS_CREATE;

/**
 * @author vjay
 * @date 2020-04-27 17:25:00
 */
public class PipelineHistoryServiceImpl implements PipelineHistoryService {

    @Autowired
    private PipelineDao pipelineDao;
    @Autowired
    private PipelineHistoryDao pipelineHistoryDao;


    @Override
    public PipelineHistory createPipelineHistory(NewParameter newParameter) {
        Assert2.notNullOf(newParameter,"newParameter");
        Integer pipeId = newParameter.getPipeId();
        String traceId = newParameter.getTraceId();
        String traceType = newParameter.getTraceType();
        String remark = newParameter.getRemark();
        String annex = newParameter.getAnnex();

        Assert2.notNullOf(pipeId,"pipeId");
        Pipeline pipeline = pipelineDao.selectByPrimaryKey(pipeId);
        Assert2.notNullOf(pipeline,"pipeline");

        PipelineHistory pipelineHistory = new PipelineHistory();
        pipelineHistory.preInsert();
        pipelineHistory.setPipeId(pipeId);
        pipelineHistory.setProviderKind(pipeline.getProviderKind());
        pipelineHistory.setAnnex(annex);
        pipelineHistory.setStatus(TASK_STATUS_CREATE);
        pipelineHistory.setTrackId(traceId);
        pipelineHistory.setTrackType(traceType);
        pipelineHistory.setRemark(remark);

        pipelineHistoryDao.insertSelective(pipelineHistory);

        return pipelineHistory;

    }

    @Override
    public PipelineHistory createPipelineHistory(HookParameter hookParameter) {
        Integer pipeId = hookParameter.getPipeId();
        String remark = hookParameter.getRemark();
        NewParameter newParameter = new NewParameter(pipeId,remark,null,null,null);


        return createPipelineHistory(newParameter);
    }

    @Override
    public PipelineHistory createPipelineHistory(RollbackParameter rollbackParameter) {
        Assert2.notNullOf(rollbackParameter,"rollbackParameter");
        Integer pipeId = rollbackParameter.getPipeId();
        Assert2.notNullOf(pipeId,"pipeId");
        String remark = rollbackParameter.getRemark();

        PipelineHistory oldPipelineHistory = pipelineHistoryDao.selectByPrimaryKey(pipeId);
        Assert2.notNullOf(oldPipelineHistory,"pipelineHistory");

        Integer oldPipeId = oldPipelineHistory.getPipeId();
        Pipeline pipeline = pipelineDao.selectByPrimaryKey(oldPipeId);
        Assert2.notNullOf(pipeline,"pipeline");

        PipelineHistory pipelineHistory = new PipelineHistory();
        BeanUtils.copyProperties(oldPipelineHistory,pipelineHistory);
        pipelineHistory.preInsert();
        pipelineHistory.setPipeId(pipeId);
        pipelineHistory.setProviderKind(pipeline.getProviderKind());
        pipelineHistory.setStatus(TASK_STATUS_CREATE);
        pipelineHistory.setRemark(remark);
        pipelineHistory.setRefId(pipeId);

        pipelineHistoryDao.insertSelective(pipelineHistory);

        return pipelineHistory;

    }


}
