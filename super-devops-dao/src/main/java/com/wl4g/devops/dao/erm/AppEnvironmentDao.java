package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.AppEnvironment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppEnvironmentDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByClusterId(Integer cluster);

    int insert(AppEnvironment record);

    int insertBatch(@Param("environments") List<AppEnvironment> environments);

    int insertSelective(AppEnvironment record);

    AppEnvironment selectByPrimaryKey(Integer id);

    List<AppEnvironment> selectByClusterId(Integer id);

    int updateByPrimaryKeySelective(AppEnvironment record);

    int updateByPrimaryKeyWithBLOBs(AppEnvironment record);

    int updateByPrimaryKey(AppEnvironment record);
}