package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipeStepBuilding;

public interface PipeStepBuildingDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByPipeId(Integer pipeId);

    int insert(PipeStepBuilding record);

    int insertSelective(PipeStepBuilding record);

    PipeStepBuilding selectByPrimaryKey(Integer id);

    PipeStepBuilding selectByPipeId(Integer pipeId);

    int updateByPrimaryKeySelective(PipeStepBuilding record);

    int updateByPrimaryKey(PipeStepBuilding record);
}