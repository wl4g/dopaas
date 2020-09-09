package com.wl4g.devops.dts.codegen.dao;

import com.wl4g.devops.dts.codegen.bean.GenDatabase;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GenTableDao {
	int deleteByPrimaryKey(Integer id);

	int insert(GenTable record);

	int insertSelective(GenTable record);

	GenTable selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(GenTable record);

	int updateByPrimaryKey(GenTable record);

	List<GenDatabase> list(@Param("tableName") String tableName);
}