package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.TaskDetail;

import java.util.List;

public interface TaskDetailDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TaskDetail record);

    int insertSelective(TaskDetail record);

    TaskDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TaskDetail record);

    int updateByPrimaryKeyWithBLOBs(TaskDetail record);

    int updateByPrimaryKey(TaskDetail record);

    List<TaskDetail> getDetailByTaskId(Integer id);

}