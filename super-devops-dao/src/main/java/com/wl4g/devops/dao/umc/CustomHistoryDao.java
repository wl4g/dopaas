package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.CustomHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomHistoryDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomHistory record);

    int insertSelective(CustomHistory record);

    CustomHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomHistory record);

    int updateByPrimaryKey(CustomHistory record);

    List<CustomHistory> list(@Param("name") String name);
}