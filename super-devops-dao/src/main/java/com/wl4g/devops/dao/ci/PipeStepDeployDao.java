package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipeStepDeploy;

public interface PipeStepDeployDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PipeStepDeploy record);

    int insertSelective(PipeStepDeploy record);

    PipeStepDeploy selectByPrimaryKey(Integer id);

    PipeStepDeploy selectByPipeId(Integer pipeId);

    int updateByPrimaryKeySelective(PipeStepDeploy record);

    int updateByPrimaryKey(PipeStepDeploy record);
}