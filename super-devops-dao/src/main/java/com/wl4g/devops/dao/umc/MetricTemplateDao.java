package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.MetricTemplate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MetricTemplateDao {
    int deleteByPrimaryKey(Integer id);

    int insert(MetricTemplate record);

    int insertSelective(MetricTemplate record);

    MetricTemplate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MetricTemplate record);

    int updateByPrimaryKey(MetricTemplate record);

    List<MetricTemplate> list( @Param("metric") String metric, @Param("classify") String classify);
}