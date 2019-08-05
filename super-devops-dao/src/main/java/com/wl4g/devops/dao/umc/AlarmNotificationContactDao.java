package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmNotificationContact;

public interface AlarmNotificationContactDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmNotificationContact record);

    int insertSelective(AlarmNotificationContact record);

    AlarmNotificationContact selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AlarmNotificationContact record);

    int updateByPrimaryKey(AlarmNotificationContact record);
}