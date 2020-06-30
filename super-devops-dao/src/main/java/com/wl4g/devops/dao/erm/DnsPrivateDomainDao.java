package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DnsPrivateDomain;

public interface DnsPrivateDomainDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DnsPrivateDomain record);

    int insertSelective(DnsPrivateDomain record);

    DnsPrivateDomain selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DnsPrivateDomain record);

    int updateByPrimaryKey(DnsPrivateDomain record);
}