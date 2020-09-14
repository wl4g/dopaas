package com.wl4g.devops.dts.codegen.dao;

import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GenProjectDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GenProject record);

    int insertSelective(GenProject record);

    GenProject selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GenProject record);

    int updateByPrimaryKey(GenProject record);

    List<GenDataSource> list(@Param("projectName") String projectName);
}