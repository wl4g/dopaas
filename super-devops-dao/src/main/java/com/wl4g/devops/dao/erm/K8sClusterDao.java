package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DockerCluster;
import com.wl4g.devops.common.bean.erm.K8sCluster;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface K8sClusterDao {
    int deleteByPrimaryKey(Integer id);

    int insert(K8sCluster record);

    int insertSelective(K8sCluster record);

    K8sCluster selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(K8sCluster record);

    int updateByPrimaryKey(K8sCluster record);

    List<K8sCluster> list(@Param("name") String name);
}