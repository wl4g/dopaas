package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipelineHistory;
import com.wl4g.devops.common.bean.ci.TaskHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PipelineHistoryDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PipelineHistory record);

    int insertSelective(PipelineHistory record);

    PipelineHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PipelineHistory record);

    int updateByPrimaryKey(PipelineHistory record);

    List<TaskHistory> list(@Param("pipeName") String pipeName, @Param("clusterName") String clusterName,
                           @Param("environment") String environment, @Param("startDate") String startDate,
                           @Param("endDate") String endDate, @Param("providerKind") String providerKind);

}