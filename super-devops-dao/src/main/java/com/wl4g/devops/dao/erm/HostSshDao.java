package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.HostSsh;

public interface HostSshDao {
    int deleteByPrimaryKey(Integer id);

    int insert(HostSsh record);

    int insertSelective(HostSsh record);

    HostSsh selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HostSsh record);

    int updateByPrimaryKey(HostSsh record);
}