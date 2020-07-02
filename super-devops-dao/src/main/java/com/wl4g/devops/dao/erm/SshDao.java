package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.Ssh;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SshDao {
	int deleteByPrimaryKey(Integer id);

	int insert(Ssh record);

	int insertSelective(Ssh record);

	Ssh selectByPrimaryKey(Integer id);

	Ssh selectByName(String name);

	int updateByPrimaryKeySelective(Ssh record);

	int updateByPrimaryKey(Ssh record);

	List<Ssh> list(@Param("organizationCodes") List<String> organizationCodes, @Param("name") String name);
}