package com.wl4g.devops.dao.gw;

import com.wl4g.devops.common.bean.gw.GWRouteFilter;

public interface GWRouteFilterDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GWRouteFilter record);

    int insertSelective(GWRouteFilter record);

    GWRouteFilter selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GWRouteFilter record);

    int updateByPrimaryKey(GWRouteFilter record);
}