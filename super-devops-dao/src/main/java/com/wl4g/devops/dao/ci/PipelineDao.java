package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.Pipeline;

public interface PipelineDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Pipeline record);

    int insertSelective(Pipeline record);

    Pipeline selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Pipeline record);

    int updateByPrimaryKey(Pipeline record);
}