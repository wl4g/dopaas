package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.Idc;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IdcDao {
	int deleteByPrimaryKey(Integer id);

	int insert(Idc record);

	int insertSelective(Idc record);

	Idc selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Idc record);

	int updateByPrimaryKey(Idc record);

	List<Idc> list(@Param("organizationCodes") List<String> organizationCodes, @Param("name") String name);
}