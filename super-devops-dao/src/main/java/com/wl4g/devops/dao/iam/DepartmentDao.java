package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.Department;

public interface DepartmentDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Department record);

    int insertSelective(Department record);

    Department selectByPrimaryKey(Integer id);

    Department selectByGroupId(Integer groupId);

    int updateByPrimaryKeySelective(Department record);

    int updateByPrimaryKey(Department record);
}