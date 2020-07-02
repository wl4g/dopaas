package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DockerInstance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DockerInstanceDao {
	int deleteByPrimaryKey(Integer id);

	int deleteByDockerId(Integer dockerId);

	int insert(DockerInstance record);

	int insertSelective(DockerInstance record);

	DockerInstance selectByPrimaryKey(Integer id);

	List<Integer> selectHostIdByDockerId(Integer dockerId);

	int updateByPrimaryKeySelective(DockerInstance record);

	int updateByPrimaryKey(DockerInstance record);

	int insertBatch(@Param("dockerInstances") List<DockerInstance> dockerInstances);
}