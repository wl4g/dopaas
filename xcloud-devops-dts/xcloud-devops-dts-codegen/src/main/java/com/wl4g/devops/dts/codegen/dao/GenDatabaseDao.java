package com.wl4g.devops.dts.codegen.dao;

import com.wl4g.devops.dts.codegen.bean.GenDatabase;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GenDatabaseDao {
	int deleteByPrimaryKey(Integer id);

	int insert(GenDatabase record);

	int insertSelective(GenDatabase record);

	GenDatabase selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(GenDatabase record);

	int updateByPrimaryKey(GenDatabase record);

	List<GenDatabase> list(@Param("name") String name);
}