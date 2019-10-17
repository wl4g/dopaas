package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.TaskBuildCommand;

import java.util.List;

public interface TaskBuildCommandDao {
	int deleteByPrimaryKey(Integer id);

	int deleteByTaskId(Integer taskId);

	int insert(TaskBuildCommand record);

	int insertSelective(TaskBuildCommand record);

	TaskBuildCommand selectByPrimaryKey(Integer id);

	List<TaskBuildCommand> selectByTaskId(Integer id);

	int updateByPrimaryKeySelective(TaskBuildCommand record);

	int updateByPrimaryKey(TaskBuildCommand record);
}