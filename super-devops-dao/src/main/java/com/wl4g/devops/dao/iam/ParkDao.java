package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.Park;

public interface ParkDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Park record);

    int insertSelective(Park record);

    Park selectByPrimaryKey(Integer id);

    Park selectByGroupId(Integer groupId);

    int updateByPrimaryKeySelective(Park record);

    int updateByPrimaryKey(Park record);
}