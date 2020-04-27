package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipeStepPcm;

public interface PipeStepPcmDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PipeStepPcm record);

    int insertSelective(PipeStepPcm record);

    PipeStepPcm selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PipeStepPcm record);

    int updateByPrimaryKey(PipeStepPcm record);
}