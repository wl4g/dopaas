package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipeStepBuilding;

public interface PipeStepBuildingDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PipeStepBuilding record);

    int insertSelective(PipeStepBuilding record);

    PipeStepBuilding selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PipeStepBuilding record);

    int updateByPrimaryKey(PipeStepBuilding record);
}