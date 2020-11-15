package com.wl4g.devops.ci.dao;

import org.apache.ibatis.annotations.Param;

import com.wl4g.devops.common.bean.ci.OrchestrationHistory;

import java.util.List;

public interface OrchestrationHistoryDao {
	int deleteByPrimaryKey(Long id);

	int insert(OrchestrationHistory record);

	int insertSelective(OrchestrationHistory record);

	OrchestrationHistory selectByPrimaryKey(Long id);

	OrchestrationHistory selectByRunId(String runId);

	int updateByPrimaryKeySelective(OrchestrationHistory record);

	int updateByPrimaryKey(OrchestrationHistory record);

	List<OrchestrationHistory> list(@Param("organizationCodes") List<String> organizationCodes, @Param("runId") String runId);
}