package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmRule;

import java.util.List;

public interface AlarmRuleDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmRule record);

    int insertSelective(AlarmRule record);

    AlarmRule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AlarmRule record);

    int updateByPrimaryKey(AlarmRule record);

    List<AlarmRule> selectAll();

}