package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmContactGroupRef;

public interface AlarmContactGroupRefDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmContactGroupRef record);

    int insertSelective(AlarmContactGroupRef record);

    AlarmContactGroupRef selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AlarmContactGroupRef record);

    int updateByPrimaryKey(AlarmContactGroupRef record);
}