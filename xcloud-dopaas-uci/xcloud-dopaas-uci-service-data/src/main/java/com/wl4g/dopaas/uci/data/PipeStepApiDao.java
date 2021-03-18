package com.wl4g.dopaas.uci.data;

import com.wl4g.dopaas.common.bean.uci.PipeStepApi;

public interface PipeStepApiDao {
    int deleteByPrimaryKey(Long id);

    int insert(PipeStepApi record);

    int insertSelective(PipeStepApi record);

    PipeStepApi selectByPrimaryKey(Long id);

    PipeStepApi selectByPipeId(Long pipeId);

    int updateByPrimaryKeySelective(PipeStepApi record);

    int updateByPrimaryKey(PipeStepApi record);
}