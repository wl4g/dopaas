package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.OrchestrationPipeline;

import java.util.List;

public interface OrchestrationPipelineDao {
    int deleteByPrimaryKey(Integer id);

    int insert(OrchestrationPipeline record);

    int insertSelective(OrchestrationPipeline record);

    OrchestrationPipeline selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrchestrationPipeline record);

    int updateByPrimaryKey(OrchestrationPipeline record);

    List<OrchestrationPipeline> selectByOrchestrationId(Integer OrchestrationId);
}