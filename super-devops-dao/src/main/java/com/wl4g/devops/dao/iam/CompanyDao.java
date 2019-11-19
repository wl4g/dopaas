package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.Company;

public interface CompanyDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Company record);

    int insertSelective(Company record);

    Company selectByPrimaryKey(Integer id);

    Company selectByGroupId(Integer groupId);

    int updateByPrimaryKeySelective(Company record);

    int updateByPrimaryKey(Company record);
}