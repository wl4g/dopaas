package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DockerRepository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DockerRepositoryDao {
	int deleteByPrimaryKey(Integer id);

	int insert(DockerRepository record);

	int insertSelective(DockerRepository record);

	DockerRepository selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(DockerRepository record);

	int updateByPrimaryKey(DockerRepository record);

	List<DockerRepository> list(@Param("organizationCodes") List<String> organizationCodes, @Param("name") String name);
}