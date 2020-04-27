package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipeStepBuildingProject;

public interface PipeStepBuildingProjectDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PipeStepBuildingProject record);

    int insertSelective(PipeStepBuildingProject record);

    PipeStepBuildingProject selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PipeStepBuildingProject record);

    int updateByPrimaryKey(PipeStepBuildingProject record);
}