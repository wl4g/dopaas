package com.wl4g.devops.ci.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.ci.core.param.HookParameter;
import com.wl4g.devops.ci.core.param.NewParameter;
import com.wl4g.devops.ci.core.param.RollbackParameter;
import com.wl4g.devops.ci.service.PipelineHistoryService;
import com.wl4g.devops.common.bean.ci.Pipeline;
import com.wl4g.devops.common.bean.ci.PipelineHistory;
import com.wl4g.devops.common.bean.ci.PipelineHistoryInstance;
import com.wl4g.devops.dao.ci.PipelineDao;
import com.wl4g.devops.dao.ci.PipelineHistoryDao;
import com.wl4g.devops.dao.ci.PipelineHistoryInstanceDao;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.support.cli.DestroableProcessManager;
import com.wl4g.devops.support.cli.destroy.DestroySignal;
import com.wl4g.devops.tool.common.lang.Assert2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_STATUS_CREATE;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_STATUS_STOPING;

/**
 * @author vjay
 * @date 2020-04-27 17:25:00
 */
@Service
public class PipelineHistoryServiceImpl implements PipelineHistoryService {

    @Autowired
    private PipelineDao pipelineDao;
    @Autowired
    private PipelineHistoryDao pipelineHistoryDao;
    @Autowired
    private PipelineHistoryInstanceDao pipelineHistoryInstanceDao;
    @Autowired
    protected DestroableProcessManager pm;



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

    @Override
    public void updateStatus(int pipeId, int status) {
        PipelineHistory pipelineHistory = new PipelineHistory();
        pipelineHistory.preUpdate();
        pipelineHistory.setId(pipeId);
        pipelineHistory.setStatus(status);
        pipelineHistoryDao.updateByPrimaryKeySelective(pipelineHistory);
    }

    @Override
    public void updateStatusAndResultAndSha(int pipeId, int status, String sha) {
        PipelineHistory pipelineHistory = new PipelineHistory();
        pipelineHistory.preUpdate();
        pipelineHistory.setId(pipeId);
        pipelineHistory.setStatus(status);
        pipelineHistory.setShaLocal(sha);
        pipelineHistoryDao.updateByPrimaryKeySelective(pipelineHistory);
    }

    @Override
    public void stopByPipeHisId(Integer pipeHisId) {
        PipelineHistory pipelineHistory = new PipelineHistory();
        pipelineHistory.preUpdate();
        pipelineHistory.setId(pipeHisId);
        pipelineHistory.setStatus(TASK_STATUS_STOPING);
        pipelineHistoryDao.updateByPrimaryKeySelective(pipelineHistory);

        // TODO timeoutMs?
        pm.destroyForComplete(new DestroySignal(String.valueOf(pipeHisId), 5000L));
    }

    @Override
    public void updateCostTime(int taskId, long costTime) {
        PipelineHistory pipelineHistory = new PipelineHistory();
        pipelineHistory.preUpdate();
        pipelineHistory.setId(taskId);
        pipelineHistory.setCostTime(costTime);
        pipelineHistoryDao.updateByPrimaryKeySelective(pipelineHistory);
    }

    @Override
    public PageModel list(PageModel pm,  String pipeName, String clusterName, String environment, String startDate, String endDate, String providerKind) {
        pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
        pm.setRecords(pipelineHistoryDao.list(pipeName, clusterName, environment, startDate, endDate, providerKind));
        return pm;
    }

    @Override
    public List<PipelineHistoryInstance> getPipeHisInstanceByPipeId(Integer pipeId) {
        return pipelineHistoryInstanceDao.selectByPipeHistoryId(pipeId);
    }

    @Override
    public PipelineHistory detail(Integer pipeId) {
        PipelineHistory pipelineHistory = pipelineHistoryDao.selectByPrimaryKey(pipeId);
        List<PipelineHistoryInstance> pipelineHistoryInstances = pipelineHistoryInstanceDao.selectByPipeHistoryId(pipeId);
        pipelineHistory.setPipelineHistoryInstances(pipelineHistoryInstances);
        return pipelineHistory;
    }


    @Override
    public void updatePipeHisInstanceStatus(int pipeInstanceId, int status) {
        PipelineHistoryInstance pipelineHistoryInstance = new PipelineHistoryInstance();
        pipelineHistoryInstance.preUpdate();
        pipelineHistoryInstance.setId(pipeInstanceId);
        pipelineHistoryInstance.setStatus(status);
        pipelineHistoryInstanceDao.updateByPrimaryKeySelective(pipelineHistoryInstance);
    }


}
