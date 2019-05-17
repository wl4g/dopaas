package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.TriggerDetail;

import java.util.List;

public interface TriggerDetailDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TriggerDetail record);

    int insertSelective(TriggerDetail record);

    TriggerDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TriggerDetail record);

    int updateByPrimaryKey(TriggerDetail record);

    int deleteByTriggerId(Integer id);

    List<TriggerDetail> getDetailByTriggerId(Integer triggerId);
}