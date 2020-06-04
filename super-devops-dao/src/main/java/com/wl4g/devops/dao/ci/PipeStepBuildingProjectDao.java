package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipeStepBuildingProject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PipeStepBuildingProjectDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByBuildingId(Integer buildingId);

    int deleteByPipeId(Integer pipeId);

    int insert(PipeStepBuildingProject record);

    int insertSelective(PipeStepBuildingProject record);

    PipeStepBuildingProject selectByPrimaryKey(Integer id);

    List<PipeStepBuildingProject> selectByBuildingId(Integer buildingId);

    List<PipeStepBuildingProject> selectByPipeId(Integer pipeId);

    int updateByPrimaryKeySelective(PipeStepBuildingProject record);

    int updateByPrimaryKey(PipeStepBuildingProject record);

    int insertBatch(@Param("pipeStepBuildingProjects") List<PipeStepBuildingProject> pipeStepBuildingProjects);

}