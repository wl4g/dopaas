package com.wl4g.devops.dao.share;

import com.wl4g.devops.common.bean.share.EntryAddress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EntryAddressDao {
    int deleteByPrimaryKey(Integer id);

    int insert(EntryAddress record);

    int insertSelective(EntryAddress record);

    EntryAddress selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(EntryAddress record);

    int updateByPrimaryKey(EntryAddress record);

    List<EntryAddress> getByAppNames(@Param("appNames") String[] appNames,@Param("envType") String envType,@Param("type") String type);

    EntryAddress getByAppName(@Param("appName") String appName,@Param("envType") String envType,@Param("type") String type);

    List<EntryAddress> getIamServer();
}