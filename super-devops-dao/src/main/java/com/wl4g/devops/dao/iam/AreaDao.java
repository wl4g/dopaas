package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.Area;

import java.util.List;

public interface AreaDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Area record);

    int insertSelective(Area record);

    Area selectByPrimaryKey(Integer id);

    List<Area> getTotal();

    int updateByPrimaryKeySelective(Area record);

    int updateByPrimaryKey(Area record);
}