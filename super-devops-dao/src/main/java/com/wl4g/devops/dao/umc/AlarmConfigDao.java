package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmConfig;

import java.util.List;

public interface AlarmConfigDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmConfig record);

    int insertSelective(AlarmConfig record);

    AlarmConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AlarmConfig record);

    int updateByPrimaryKey(AlarmConfig record);

    List<AlarmConfig> selectAll();
}