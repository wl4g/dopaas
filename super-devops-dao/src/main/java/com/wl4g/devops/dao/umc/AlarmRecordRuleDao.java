package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmRecordRule;

public interface AlarmRecordRuleDao {
	int deleteByPrimaryKey(Integer id);

	int insert(AlarmRecordRule record);

	int insertSelective(AlarmRecordRule record);

	AlarmRecordRule selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(AlarmRecordRule record);

	int updateByPrimaryKey(AlarmRecordRule record);
}