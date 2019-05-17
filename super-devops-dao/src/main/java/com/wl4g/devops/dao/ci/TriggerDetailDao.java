package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.TriggerDetail;

public interface TriggerDetailDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TriggerDetail record);

    int insertSelective(TriggerDetail record);

    TriggerDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TriggerDetail record);

    int updateByPrimaryKey(TriggerDetail record);
}