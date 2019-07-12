package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmRecordUser;

public interface AlarmRecordUserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmRecordUser record);

    int insertSelective(AlarmRecordUser record);

    AlarmRecordUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AlarmRecordUser record);

    int updateByPrimaryKey(AlarmRecordUser record);
}