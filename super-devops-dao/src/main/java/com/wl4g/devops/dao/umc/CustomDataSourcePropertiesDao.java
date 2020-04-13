package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.CustomDataSourceProperties;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomDataSourcePropertiesDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByDataSourceId(Integer dataSourceId);

    int insert(CustomDataSourceProperties record);

    int insertSelective(CustomDataSourceProperties record);

    CustomDataSourceProperties selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomDataSourceProperties record);

    int updateByPrimaryKey(CustomDataSourceProperties record);

    int insertBatch(@Param("properties") List<CustomDataSourceProperties> properties);
}