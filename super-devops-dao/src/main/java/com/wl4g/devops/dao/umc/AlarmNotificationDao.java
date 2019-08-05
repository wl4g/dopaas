package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmNotification;

public interface AlarmNotificationDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmNotification record);

    int insertSelective(AlarmNotification record);

    AlarmNotification selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AlarmNotification record);

    int updateByPrimaryKey(AlarmNotification record);
}