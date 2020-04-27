package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipelineHistoryInstance;

public interface PipelineHistoryInstanceDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PipelineHistoryInstance record);

    int insertSelective(PipelineHistoryInstance record);

    PipelineHistoryInstance selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PipelineHistoryInstance record);

    int updateByPrimaryKey(PipelineHistoryInstance record);
}