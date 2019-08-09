package com.wl4g.devops.dao.app;

import com.wl4g.devops.common.bean.app.AppHost;

import java.util.List;

public interface HostDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AppHost record);

    int insertSelective(AppHost record);

    AppHost selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppHost record);

    int updateByPrimaryKey(AppHost record);

    List<AppHost> list();
}