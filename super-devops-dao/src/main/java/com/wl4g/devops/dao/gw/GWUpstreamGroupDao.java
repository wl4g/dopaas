package com.wl4g.devops.dao.gw;

import com.wl4g.devops.common.bean.gw.GWCluster;
import com.wl4g.devops.common.bean.gw.GWUpstreamGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GWUpstreamGroupDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GWUpstreamGroup record);

    int insertSelective(GWUpstreamGroup record);

    GWUpstreamGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GWUpstreamGroup record);

    int updateByPrimaryKey(GWUpstreamGroup record);

    List<GWUpstreamGroup> list(@Param("organizationCodes") List<String> organizationCodes, @Param("name") String name);
}