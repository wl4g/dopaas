package com.wl4g.devops.dao.share;


import com.wl4g.devops.common.bean.share.AppInstance;

import java.util.List;

public interface AppInstanceDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AppInstance record);

    int insertSelective(AppInstance record);

    AppInstance selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppInstance record);

    int updateByPrimaryKey(AppInstance record);

    List<AppInstance> selectByClusterId(Integer clusterId);
}