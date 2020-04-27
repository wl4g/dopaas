package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipelineInstance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PipelineInstanceDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByPipeId(Integer pipeId);

    int insert(PipelineInstance record);

    int insertSelective(PipelineInstance record);

    PipelineInstance selectByPrimaryKey(Integer id);

    List<PipelineInstance> selectByPipeId(Integer pipeId);

    int updateByPrimaryKeySelective(PipelineInstance record);

    int updateByPrimaryKey(PipelineInstance record);

    int insertBatch(@Param("pipelineInstances")List<PipelineInstance> pipelineInstances);
}