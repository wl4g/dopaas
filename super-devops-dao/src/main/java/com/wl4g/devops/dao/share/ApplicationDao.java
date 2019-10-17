package com.wl4g.devops.dao.share;

import com.wl4g.devops.common.bean.iam.ApplicationInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApplicationDao {
	int deleteByPrimaryKey(String appname);

	int insert(ApplicationInfo record);

	int insertSelective(ApplicationInfo record);

	ApplicationInfo selectByPrimaryKey(String appname);

	int updateByPrimaryKeySelective(ApplicationInfo record);

	int updateByPrimaryKey(ApplicationInfo record);

	List<ApplicationInfo> getByAppNames(@Param("appNames") String[] appNames);
}