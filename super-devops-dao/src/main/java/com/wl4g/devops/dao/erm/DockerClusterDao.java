package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DockerCluster;

public interface DockerClusterDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DockerCluster record);

    int insertSelective(DockerCluster record);

    DockerCluster selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DockerCluster record);

    int updateByPrimaryKey(DockerCluster record);
}