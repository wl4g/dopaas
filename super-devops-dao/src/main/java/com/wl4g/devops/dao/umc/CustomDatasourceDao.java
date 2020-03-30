package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.CustomDataSource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomDatasourceDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CustomDataSource record);

    int insertSelective(CustomDataSource record);

    CustomDataSource selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CustomDataSource record);

    int updateByPrimaryKey(CustomDataSource record);

    List<CustomDataSource> list(@Param("name") String name);
}