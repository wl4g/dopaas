package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AlarmRecordDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmRecord record);

    int insertSelective(AlarmRecord record);

    AlarmRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AlarmRecord record);

    int updateByPrimaryKey(AlarmRecord record);

    List<AlarmRecord> list(@Param("name") String name, @Param("startDate") String startDate,
                           @Param("endDate") String endDate);
}