package com.wl4g.devops.dts.codegen.dao;

import com.wl4g.devops.dts.codegen.bean.GenTable;

public interface GenTableDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GenTable record);

    int insertSelective(GenTable record);

    GenTable selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GenTable record);

    int updateByPrimaryKey(GenTable record);
}