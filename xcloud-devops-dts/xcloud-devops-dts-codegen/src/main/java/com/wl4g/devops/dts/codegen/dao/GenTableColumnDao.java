package com.wl4g.devops.dts.codegen.dao;

import com.wl4g.devops.dts.codegen.bean.GenTableColumn;

public interface GenTableColumnDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GenTableColumn record);

    int insertSelective(GenTableColumn record);

    GenTableColumn selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GenTableColumn record);

    int updateByPrimaryKey(GenTableColumn record);
}