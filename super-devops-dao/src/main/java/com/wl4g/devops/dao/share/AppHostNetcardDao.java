package com.wl4g.devops.dao.share;

import com.wl4g.devops.common.bean.share.AppHostNetcard;

public interface AppHostNetcardDao {
	int deleteByPrimaryKey(Integer id);

	int insert(AppHostNetcard record);

	int insertSelective(AppHostNetcard record);

	AppHostNetcard selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(AppHostNetcard record);

	int updateByPrimaryKey(AppHostNetcard record);
}