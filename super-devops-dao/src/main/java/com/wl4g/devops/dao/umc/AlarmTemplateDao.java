package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

public interface AlarmTemplateDao {
	int deleteByPrimaryKey(Integer id);

	int insert(AlarmTemplate record);

	int insertSelective(AlarmTemplate record);

	AlarmTemplate selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(AlarmTemplate record);

	int updateByPrimaryKey(AlarmTemplate record);

	List<AlarmTemplate> getByCollectId(@Param("collectId") Serializable collectId);

	List<AlarmTemplate> getByClusterId(@Param("clusterId") Serializable clusterId);

}