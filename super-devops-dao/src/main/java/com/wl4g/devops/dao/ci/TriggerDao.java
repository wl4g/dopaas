package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.Trigger;

public interface TriggerDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Trigger record);

    int insertSelective(Trigger record);

    Trigger selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Trigger record);

    int updateByPrimaryKey(Trigger record);
}