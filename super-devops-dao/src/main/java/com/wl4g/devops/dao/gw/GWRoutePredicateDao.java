package com.wl4g.devops.dao.gw;

import com.wl4g.devops.common.bean.gw.GWRoutePredicate;

public interface GWRoutePredicateDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GWRoutePredicate record);

    int insertSelective(GWRoutePredicate record);

    GWRoutePredicate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GWRoutePredicate record);

    int updateByPrimaryKey(GWRoutePredicate record);
}