package com.wl4g.devops.ci.dao;

import org.apache.ibatis.annotations.Param;

import com.wl4g.devops.common.bean.ci.ClusterExtension;

import java.util.List;

public interface ClusterExtensionDao {
	int deleteByPrimaryKey(Long id);

	int insert(ClusterExtension record);

	int insertSelective(ClusterExtension record);

	ClusterExtension selectByPrimaryKey(Long id);

	ClusterExtension selectByClusterId(Long clusterId);

	ClusterExtension selectByClusterName(String clusterName);

	int updateByPrimaryKeySelective(ClusterExtension record);

	int updateByPrimaryKey(ClusterExtension record);

	List<ClusterExtension> list(@Param("clusterName") String clusterName);
}