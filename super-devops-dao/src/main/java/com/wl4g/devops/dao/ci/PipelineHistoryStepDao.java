package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipelineHistoryStep;

public interface PipelineHistoryStepDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PipelineHistoryStep record);

    int insertSelective(PipelineHistoryStep record);

    PipelineHistoryStep selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PipelineHistoryStep record);

    int updateByPrimaryKey(PipelineHistoryStep record);
}