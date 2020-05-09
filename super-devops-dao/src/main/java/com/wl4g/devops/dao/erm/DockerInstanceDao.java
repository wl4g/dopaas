package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DockerInstance;

public interface DockerInstanceDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DockerInstance record);

    int insertSelective(DockerInstance record);

    DockerInstance selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DockerInstance record);

    int updateByPrimaryKey(DockerInstance record);
}