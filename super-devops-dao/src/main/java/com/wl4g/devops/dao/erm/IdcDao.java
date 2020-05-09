package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.Idc;

public interface IdcDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Idc record);

    int insertSelective(Idc record);

    Idc selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Idc record);

    int updateByPrimaryKey(Idc record);
}