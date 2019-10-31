package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.Role;

import java.util.List;

public interface RoleDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Role record);

    int insertSelective(Role record);

    Role selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Role record);

    int updateByPrimaryKey(Role record);

    List selectByUserId(Integer userId);
}