package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AlarmConfigDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmConfig record);

    int insertSelective(AlarmConfig record);

    AlarmConfig selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AlarmConfig record);

    int updateByPrimaryKey(AlarmConfig record);

    List<AlarmConfig> selectAll();

    List<AlarmConfig> selectByTemplateId(Integer templateId);

    List<AlarmConfig> getByCollectIdAndTemplateId(@Param("templateId")Integer templateId, @Param("collectId")Integer collectId);


}