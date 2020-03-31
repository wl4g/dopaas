package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.CustomEngine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomEngineDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomEngine record);

    int insertSelective(CustomEngine record);

    CustomEngine selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomEngine record);

    int updateByPrimaryKey(CustomEngine record);

    List<CustomEngine> list(@Param("name") String name);
}