package com.wl4g.devops.dao.gw;

import com.wl4g.devops.common.bean.gw.GWUpstream;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GWUpstreamDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GWUpstream record);

    int insertSelective(GWUpstream record);

    GWUpstream selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GWUpstream record);

    int updateByPrimaryKey(GWUpstream record);

    List<GWUpstream> list(@Param("organizationCodes") List<String> organizationCodes, @Param("name") String name);
}