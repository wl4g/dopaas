package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.TaskDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskDetailDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TaskDetail record);

    int insertSelective(TaskDetail record);

    TaskDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TaskDetail record);

    int updateByPrimaryKey(TaskDetail record);

    int deleteByTaskId(Integer taskId);

    List<TaskDetail> getUsedInstance(@Param("appGroupId") Integer appGroupId, @Param("taskId") Integer taskId);

    List<TaskDetail> selectByTaskId(Integer taskId);



}