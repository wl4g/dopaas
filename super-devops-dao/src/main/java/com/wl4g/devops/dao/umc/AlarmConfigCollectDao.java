package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmConfigCollect;

public interface AlarmConfigCollectDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmConfigCollect record);

    int insertSelective(AlarmConfigCollect record);

    AlarmConfigCollect selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AlarmConfigCollect record);

    int updateByPrimaryKey(AlarmConfigCollect record);
}