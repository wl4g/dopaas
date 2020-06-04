package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipeStepInstanceCommand;

public interface PipeStepInstanceCommandDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByPipeId(Integer pipeId);

    int insert(PipeStepInstanceCommand record);

    int insertSelective(PipeStepInstanceCommand record);

    PipeStepInstanceCommand selectByPrimaryKey(Integer id);

    PipeStepInstanceCommand selectByPipeId(Integer pipeId);

    int updateByPrimaryKeySelective(PipeStepInstanceCommand record);

    int updateByPrimaryKey(PipeStepInstanceCommand record);
}