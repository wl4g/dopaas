package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.HostNetcard;

public interface HostNetcardDao {
    int deleteByPrimaryKey(Integer id);

    int insert(HostNetcard record);

    int insertSelective(HostNetcard record);

    HostNetcard selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HostNetcard record);

    int updateByPrimaryKey(HostNetcard record);
}