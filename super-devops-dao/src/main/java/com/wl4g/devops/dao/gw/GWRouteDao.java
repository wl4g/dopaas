package com.wl4g.devops.dao.gw;

import com.wl4g.devops.common.bean.gw.GWRoute;

public interface GWRouteDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GWRoute record);

    int insertSelective(GWRoute record);

    GWRoute selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GWRoute record);

    int updateByPrimaryKey(GWRoute record);
}