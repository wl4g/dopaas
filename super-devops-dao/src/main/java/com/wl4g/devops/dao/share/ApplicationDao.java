package com.wl4g.devops.dao.share;

import com.wl4g.devops.common.bean.share.Application;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApplicationDao {
    int deleteByPrimaryKey(String appname);

    int insert(Application record);

    int insertSelective(Application record);

    Application selectByPrimaryKey(String appname);

    int updateByPrimaryKeySelective(Application record);

    int updateByPrimaryKey(Application record);

    List<Application> getByAppNames (@Param("appNames") String[] appNames);
}