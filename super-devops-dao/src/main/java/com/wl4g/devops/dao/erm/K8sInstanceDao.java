package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.K8sInstance;

public interface K8sInstanceDao {
    int deleteByPrimaryKey(Integer id);

    int insert(K8sInstance record);

    int insertSelective(K8sInstance record);

    K8sInstance selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(K8sInstance record);

    int updateByPrimaryKey(K8sInstance record);
}