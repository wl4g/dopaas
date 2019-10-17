package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmCollector;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AlarmCollectorDao {
	int deleteByPrimaryKey(Integer id);

	int insert(AlarmCollector record);

	int insertSelective(AlarmCollector record);

	AlarmCollector selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(AlarmCollector record);

	int updateByPrimaryKey(AlarmCollector record);

	List<AlarmCollector> list(@Param("name") String name, @Param("addr") String addr);
}