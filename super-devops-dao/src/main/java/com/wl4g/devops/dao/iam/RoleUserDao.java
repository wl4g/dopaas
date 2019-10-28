package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.RoleUser;

public interface RoleUserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(RoleUser record);

    int insertSelective(RoleUser record);

    RoleUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoleUser record);

    int updateByPrimaryKey(RoleUser record);
}