package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.HostSsh;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HostSshDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByHostId(Integer hostId);

    int insert(HostSsh record);

    int insertSelective(HostSsh record);

    HostSsh selectByPrimaryKey(Integer id);

    List<Integer> selectByHostId(Integer hostId);

    int updateByPrimaryKeySelective(HostSsh record);

    int updateByPrimaryKey(HostSsh record);

    int insertBatch(@Param("hostSshes") List<HostSsh> hostSshes);
}