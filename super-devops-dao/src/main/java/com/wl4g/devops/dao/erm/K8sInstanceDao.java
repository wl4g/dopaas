package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.K8sInstance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface K8sInstanceDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByK8sId(Integer k8sId);

    int insert(K8sInstance record);

    int insertSelective(K8sInstance record);

    K8sInstance selectByPrimaryKey(Integer id);

    List<Integer> selectHostIdByK8sId(Integer k8sId);

    int updateByPrimaryKeySelective(K8sInstance record);

    int updateByPrimaryKey(K8sInstance record);

    int insertBatch(@Param("k8sInstances") List<K8sInstance> k8sInstances);
}