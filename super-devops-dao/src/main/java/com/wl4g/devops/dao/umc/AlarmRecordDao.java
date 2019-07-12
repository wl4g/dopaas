package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmRecord;

public interface AlarmRecordDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmRecord record);

    int insertSelective(AlarmRecord record);

    AlarmRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AlarmRecord record);

    int updateByPrimaryKey(AlarmRecord record);
}