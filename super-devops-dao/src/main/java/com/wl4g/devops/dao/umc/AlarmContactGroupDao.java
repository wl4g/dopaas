package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmContactGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AlarmContactGroupDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmContactGroup record);

    int insertSelective(AlarmContactGroup record);

    AlarmContactGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AlarmContactGroup record);

    int updateByPrimaryKey(AlarmContactGroup record);

    List<AlarmContactGroup> list(@Param("name") String name);
}