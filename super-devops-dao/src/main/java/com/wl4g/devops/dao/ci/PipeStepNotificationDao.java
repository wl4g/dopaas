package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipeStepNotification;

public interface PipeStepNotificationDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByPipeId(Integer pipeId);

    int insert(PipeStepNotification record);

    int insertSelective(PipeStepNotification record);

    PipeStepNotification selectByPrimaryKey(Integer id);

    PipeStepNotification selectByPipeId(Integer pipeId);

    int updateByPrimaryKeySelective(PipeStepNotification record);

    int updateByPrimaryKey(PipeStepNotification record);
}