package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.Pipeline;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PipelineDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Pipeline record);

    int insertSelective(Pipeline record);

    Pipeline selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Pipeline record);

    int updateByPrimaryKey(Pipeline record);

    List<Pipeline> list(@Param("id") Integer id,@Param("pipeName") String pipeName, @Param("providerKind") String providerKind,  @Param("environment") String environment);
}