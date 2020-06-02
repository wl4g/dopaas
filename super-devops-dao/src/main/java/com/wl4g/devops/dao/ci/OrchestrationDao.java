package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.Orchestration;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrchestrationDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Orchestration record);

    int insertSelective(Orchestration record);

    Orchestration selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Orchestration record);

    int updateByPrimaryKey(Orchestration record);

    List<Orchestration> list(@Param("organizationCodes")List<String> organizationCodes, @Param("name") String name);
}