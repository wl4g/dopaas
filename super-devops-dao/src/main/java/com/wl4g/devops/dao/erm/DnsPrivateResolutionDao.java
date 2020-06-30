package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DnsPrivateResolution;

public interface DnsPrivateResolutionDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DnsPrivateResolution record);

    int insertSelective(DnsPrivateResolution record);

    DnsPrivateResolution selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DnsPrivateResolution record);

    int updateByPrimaryKey(DnsPrivateResolution record);
}