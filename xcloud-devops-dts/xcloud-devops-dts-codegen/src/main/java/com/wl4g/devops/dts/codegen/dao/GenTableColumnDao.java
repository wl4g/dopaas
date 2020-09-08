package com.wl4g.devops.dts.codegen.dao;

import com.wl4g.devops.dts.codegen.bean.GenTableColumn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GenTableColumnDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByTableId(Integer tableId);

    int insert(GenTableColumn record);

    int insertBatch(@Param("columns")List<GenTableColumn> columns);

    int insertSelective(GenTableColumn record);

    GenTableColumn selectByPrimaryKey(Integer id);

    List<GenTableColumn> selectByTableId(Integer tableId);

    int updateByPrimaryKeySelective(GenTableColumn record);

    int updateByPrimaryKey(GenTableColumn record);
}