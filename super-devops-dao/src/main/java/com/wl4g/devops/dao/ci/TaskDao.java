package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.Task;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Task record);

    int insertSelective(Task record);

    Task selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Task record);

    int updateByPrimaryKeyWithBLOBs(Task record);

    int updateByPrimaryKey(Task record);

    List<Task> list(@Param("id") Integer id,@Param("taskName") String taskName,@Param("groupName") String groupName,
                    @Param("branchName") String branchName,@Param("tarType") Integer tarType,@Param("startDate") String startDate,
                    @Param("endDate") String endDate);
}