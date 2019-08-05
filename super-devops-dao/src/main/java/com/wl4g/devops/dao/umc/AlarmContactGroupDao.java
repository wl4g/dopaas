package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmContactGroup;

public interface AlarmContactGroupDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmContactGroup record);

    int insertSelective(AlarmContactGroup record);

    AlarmContactGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AlarmContactGroup record);

    int updateByPrimaryKey(AlarmContactGroup record);
}