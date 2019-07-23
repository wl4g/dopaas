package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.TaskSign;
import org.apache.ibatis.annotations.Param;

public interface TaskSignDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TaskSign record);

    int insertSelective(TaskSign record);

    TaskSign selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TaskSign record);

    int updateByPrimaryKey(TaskSign record);

    TaskSign selectByDependencyIdAndTaskId(@Param("dependencyId") Integer dependencyId,@Param("taskId") Integer taskId);
}