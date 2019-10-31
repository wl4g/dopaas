package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.GroupMenu;

public interface GroupMenuDao {
    int deleteByPrimaryKey(Integer id);

    int insert(GroupMenu record);

    int insertSelective(GroupMenu record);

    GroupMenu selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GroupMenu record);

    int updateByPrimaryKey(GroupMenu record);
}