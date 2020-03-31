package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.CustomAlarmEvent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomAlarmEventDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomAlarmEvent record);

    int insertSelective(CustomAlarmEvent record);

    CustomAlarmEvent selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomAlarmEvent record);

    int updateByPrimaryKey(CustomAlarmEvent record);

    List<CustomAlarmEvent> list(@Param("name") String name);
}