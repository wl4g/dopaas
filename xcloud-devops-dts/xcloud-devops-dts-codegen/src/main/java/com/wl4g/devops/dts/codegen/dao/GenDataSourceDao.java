package com.wl4g.devops.dts.codegen.dao;

import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GenDataSourceDao {

	int deleteByPrimaryKey(Integer id);

	int insert(GenDataSource record);

	int insertSelective(GenDataSource record);

	GenDataSource selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(GenDataSource record);

	int updateByPrimaryKey(GenDataSource record);

	List<GenDataSource> list(@Param("name") String name);
}