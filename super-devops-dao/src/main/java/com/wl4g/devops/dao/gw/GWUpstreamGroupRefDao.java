package com.wl4g.devops.dao.gw;

import com.wl4g.devops.common.bean.gw.GWUpstreamGroupRef;

public interface GWUpstreamGroupRefDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GWUpstreamGroupRef record);

    int insertSelective(GWUpstreamGroupRef record);

    GWUpstreamGroupRef selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GWUpstreamGroupRef record);

    int updateByPrimaryKey(GWUpstreamGroupRef record);
}