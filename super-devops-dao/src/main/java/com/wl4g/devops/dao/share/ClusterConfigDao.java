package com.wl4g.devops.dao.share;

import com.wl4g.devops.common.bean.share.ClusterConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClusterConfigDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ClusterConfig record);

    int insertSelective(ClusterConfig record);

    ClusterConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ClusterConfig record);

    int updateByPrimaryKey(ClusterConfig record);

    List<ClusterConfig> getByAppNames(@Param("appNames") String[] appNames, @Param("envType") String envType, @Param("type") String type);

    ClusterConfig getByAppName(@Param("appName") String appName,@Param("envType") String envType,@Param("type") String type);

    List<ClusterConfig> getIamServer();
}