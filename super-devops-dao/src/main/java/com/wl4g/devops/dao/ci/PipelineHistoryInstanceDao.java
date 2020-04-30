package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipelineHistoryInstance;

import java.util.List;

public interface PipelineHistoryInstanceDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PipelineHistoryInstance record);

    int insertSelective(PipelineHistoryInstance record);

    PipelineHistoryInstance selectByPrimaryKey(Integer id);

    List<PipelineHistoryInstance> selectByPipeHistoryId(Integer pipeHistoryId);

    int updateByPrimaryKeySelective(PipelineHistoryInstance record);

    int updateByPrimaryKey(PipelineHistoryInstance record);
}