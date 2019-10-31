package com.wl4g.devops.dao.share;


import com.wl4g.devops.common.bean.share.AppCluster;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppClusterDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AppCluster record);

    int insertSelective(AppCluster record);

    AppCluster selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppCluster record);

    int updateByPrimaryKey(AppCluster record);

    List<AppCluster> list(@Param("clusterName") String clusterName);
}