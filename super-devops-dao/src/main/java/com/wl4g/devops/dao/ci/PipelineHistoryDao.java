package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipelineHistory;

public interface PipelineHistoryDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PipelineHistory record);

    int insertSelective(PipelineHistory record);

    PipelineHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PipelineHistory record);

    int updateByPrimaryKey(PipelineHistory record);
}