package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.AnalysisHistory;

import java.util.List;

public interface AnalysisHistoryDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AnalysisHistory record);

    int insertSelective(AnalysisHistory record);

    AnalysisHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AnalysisHistory record);

    int updateByPrimaryKey(AnalysisHistory record);

    List<AnalysisHistory> list();
}