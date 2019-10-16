package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.TaskBuildCommand;

import java.util.List;

public interface TaskHisBuildCommandDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TaskBuildCommand record);

    int insertSelective(TaskBuildCommand record);

    TaskBuildCommand selectByPrimaryKey(Integer id);

    List<TaskBuildCommand> selectByTaskHisId(Integer taskHisId);

    int updateByPrimaryKeySelective(TaskBuildCommand record);

    int updateByPrimaryKey(TaskBuildCommand record);
}