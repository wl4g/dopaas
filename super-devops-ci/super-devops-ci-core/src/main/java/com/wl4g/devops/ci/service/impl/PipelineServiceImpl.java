package com.wl4g.devops.ci.service.impl;

import com.wl4g.devops.ci.service.PipelineService;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.ci.*;
import com.wl4g.devops.dao.ci.*;
import com.wl4g.devops.tool.common.lang.Assert2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * @author vjay
 * @date 2020-04-27 15:08:00
 */
public class PipelineServiceImpl implements PipelineService {

    @Autowired
    private PipelineDao pipelineDao;

    @Autowired
    private PipelineInstanceDao pipelineInstanceDao;

    @Autowired
    private PipeStepBuildingDao pipeStepBuildingDao;

    @Autowired
    private PipeStepBuildingProjectDao pipeStepBuildingProjectDao;

    @Autowired
    private PipeStepPcmDao pipeStepPcmDao;

    @Autowired
    PipeStepNotificationDao pipeStepNotificationDao;

    @Override
    public List<Pipeline> list(String pipeName, String providerKind, String environment) {
        return pipelineDao.list(null, pipeName, providerKind, environment);
    }

    @Override
    public void save(Pipeline pipeline) {
        if (nonNull(pipeline.getId())) {
            update(pipeline);
        } else {
            insert(pipeline);
        }
    }

    @Override
    public Pipeline detail(Integer id) {
        Assert2.notNullOf(id,"id");
        //Pipeline
        Pipeline pipeline = pipelineDao.selectByPrimaryKey(id);
        //Pipeline Instance
        List<PipelineInstance> pipelineInstances = pipelineInstanceDao.selectByPipeId(id);
        Integer[] instanceIds = new Integer[pipelineInstances.size()];
        for(int i = 0;i<pipelineInstances.size();i++){
            instanceIds[i] = pipelineInstances.get(i).getInstanceId();
        }
        pipeline.setInstanceIds(instanceIds);

        //Pipeline Building
        PipeStepBuilding pipeStepBuilding = pipeStepBuildingDao.selectByPipeId(id);
        pipeline.setPipeStepBuilding(pipeStepBuilding);

        //Pipeline Pcm
        PipeStepPcm pipeStepPcm = pipeStepPcmDao.selectByPipeId(id);
        pipeline.setPipeStepPcm(pipeStepPcm);

        //Pipeline Notification
        PipeStepNotification pipeStepNotification = pipeStepNotificationDao.selectByPipeId(id);
        pipeline.setPipeStepNotification(pipeStepNotification);

        //TODO ...... testing,analysis,docker,k8s

        return pipeline;
    }

    @Override
    public void del(Integer id) {
        Pipeline pipeline = new Pipeline();
        pipeline.setId(id);
        pipeline.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        pipelineDao.updateByPrimaryKeySelective(pipeline);
    }

    @Transactional
    public void insert(Pipeline pipeline) {
        Assert2.notNullOf(pipeline, "pipeline");
        // Insert Pipeline
        pipeline.preInsert();
        pipelineDao.insertSelective(pipeline);
        // Insert PipeInstance
        Integer[] instanceIds = pipeline.getInstanceIds();
        if (nonNull(instanceIds)) {
            List<PipelineInstance> pipelineInstances = new ArrayList<>();
            for (Integer i : instanceIds) {
                PipelineInstance pipelineInstance = new PipelineInstance();
                pipelineInstance.preInsert();
                pipelineInstance.setPipeId(pipeline.getId());
                pipelineInstance.setInstanceId(i);
                pipelineInstances.add(pipelineInstance);
            }
            pipelineInstanceDao.insertBatch(pipelineInstances);
        }
        //Insert PipeStepBuilding
        PipeStepBuilding pipeStepBuilding = pipeline.getPipeStepBuilding();
        if (nonNull(pipeStepBuilding)) {
            pipeStepBuilding.preInsert();
            pipeStepBuilding.setPipeId(pipeline.getId());
            pipeStepBuildingDao.insertSelective(pipeStepBuilding);
            //Insert PipeStepBuildingProject
            List<PipeStepBuildingProject> pipeStepBuildingProjects = pipeline.getPipeStepBuilding().getPipeStepBuildingProjects();
            if (!CollectionUtils.isEmpty(pipeStepBuildingProjects)) {
                for (PipeStepBuildingProject pipeStepBuildingProject : pipeStepBuildingProjects) {
                    pipeStepBuildingProject.preInsert();
                    pipeStepBuildingProject.setBuildingId(pipeStepBuilding.getId());
                }
                pipeStepBuildingProjectDao.insertBatch(pipeStepBuildingProjects);
            }
        }

        //TODO ...... testing,analysis,docker,k8s

        //Insert Pcm
        PipeStepPcm pipeStepPcm = pipeline.getPipeStepPcm();
        if (nonNull(pipeStepPcm)) {
            pipeStepPcm.preInsert();
            pipeStepPcm.setPipeId(pipeline.getId());
            pipeStepPcmDao.insertSelective(pipeStepPcm);
        }
        //Insert Notification
        PipeStepNotification pipeStepNotification = pipeline.getPipeStepNotification();
        if (nonNull(pipeStepNotification)) {
            pipeStepNotification.preInsert();
            pipeStepNotification.setPipeId(pipeline.getId());
            pipeStepNotificationDao.insertSelective(pipeStepNotification);
        }

    }

    private void update(Pipeline pipeline) {
        pipeline.preUpdate();
        Assert2.notNullOf(pipeline, "pipeline");
        // Insert Pipeline
        pipeline.preUpdate();
        pipelineDao.updateByPrimaryKeySelective(pipeline);
        // Insert PipeInstance
        Integer[] instanceIds = pipeline.getInstanceIds();
        pipelineInstanceDao.deleteByPipeId(pipeline.getId());
        if (nonNull(instanceIds)) {
            List<PipelineInstance> pipelineInstances = new ArrayList<>();
            for (Integer i : instanceIds) {
                PipelineInstance pipelineInstance = new PipelineInstance();
                pipelineInstance.preInsert();
                pipelineInstance.setPipeId(pipeline.getId());
                pipelineInstance.setInstanceId(i);
                pipelineInstances.add(pipelineInstance);
            }
            pipelineInstanceDao.insertBatch(pipelineInstances);
        }
        //Insert PipeStepBuilding
        pipeStepBuildingDao.deleteByPipeId(pipeline.getId());
        PipeStepBuilding pipeStepBuilding = pipeline.getPipeStepBuilding();
        if (nonNull(pipeStepBuilding)) {
            pipeStepBuilding.preInsert();
            pipeStepBuilding.setPipeId(pipeline.getId());
            pipeStepBuildingDao.insertSelective(pipeStepBuilding);
            //Insert PipeStepBuildingProject
            pipeStepBuildingProjectDao.deleteByBuildingId(pipeline.getPipeStepBuilding().getId());
            List<PipeStepBuildingProject> pipeStepBuildingProjects = pipeline.getPipeStepBuilding().getPipeStepBuildingProjects();
            if (!CollectionUtils.isEmpty(pipeStepBuildingProjects)) {
                for (PipeStepBuildingProject pipeStepBuildingProject : pipeStepBuildingProjects) {
                    pipeStepBuildingProject.preInsert();
                    pipeStepBuildingProject.setBuildingId(pipeline.getPipeStepBuilding().getId());
                }
                pipeStepBuildingProjectDao.insertBatch(pipeStepBuildingProjects);
            }
        }

        //TODO ...... testing,analysis,docker,k8s

        //Insert Pcm
        pipeStepPcmDao.deleteByPipeId(pipeline.getId());
        PipeStepPcm pipeStepPcm = pipeline.getPipeStepPcm();
        if (nonNull(pipeStepPcm)) {
            pipeStepPcm.preInsert();
            pipeStepPcm.setPipeId(pipeline.getId());
            pipeStepPcmDao.insertSelective(pipeStepPcm);
        }
        //Insert Notification
        pipeStepNotificationDao.deleteByPipeId(pipeline.getId());
        PipeStepNotification pipeStepNotification = pipeline.getPipeStepNotification();
        if (nonNull(pipeStepNotification)) {
            pipeStepNotification.preInsert();
            pipeStepNotification.setPipeId(pipeline.getId());
            pipeStepNotificationDao.insertSelective(pipeStepNotification);
        }
    }
}
