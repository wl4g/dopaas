package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.RoleMenu;

public interface RoleMenuDao {
    int deleteByPrimaryKey(Integer id);

    int insert(RoleMenu record);

    int insertSelective(RoleMenu record);

    RoleMenu selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoleMenu record);

    int updateByPrimaryKey(RoleMenu record);
}