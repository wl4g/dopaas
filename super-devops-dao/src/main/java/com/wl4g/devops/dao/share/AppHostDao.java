package com.wl4g.devops.dao.share;

import com.wl4g.devops.common.bean.share.AppHost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppHostDao {
	int deleteByPrimaryKey(Integer id);

	int insert(AppHost record);

	int insertSelective(AppHost record);

	AppHost selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(AppHost record);

	int updateByPrimaryKey(AppHost record);

	List<AppHost> list(@Param("name") String name, @Param("hostname") String hostname, @Param("idcId") Integer idcId);

}