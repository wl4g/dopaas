package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.PipeHistoryPcm;

public interface PipeHistoryPcmDao {
    int deleteByPrimaryKey(Integer id);

    int insert(PipeHistoryPcm record);

    int insertSelective(PipeHistoryPcm record);

    PipeHistoryPcm selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PipeHistoryPcm record);

    int updateByPrimaryKey(PipeHistoryPcm record);
}