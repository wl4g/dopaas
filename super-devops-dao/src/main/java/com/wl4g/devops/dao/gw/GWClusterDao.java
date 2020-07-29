package com.wl4g.devops.dao.gw;

import com.wl4g.devops.common.bean.erm.Idc;
import com.wl4g.devops.common.bean.gw.GWCluster;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GWClusterDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GWCluster record);

    int insertSelective(GWCluster record);

    GWCluster selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GWCluster record);

    int updateByPrimaryKey(GWCluster record);

    List<GWCluster> list(@Param("organizationCodes") List<String> organizationCodes, @Param("name") String name);
}