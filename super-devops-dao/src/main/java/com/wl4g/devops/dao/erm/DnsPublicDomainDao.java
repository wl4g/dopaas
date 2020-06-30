package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DnsPublicDomain;

public interface DnsPublicDomainDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DnsPublicDomain record);

    int insertSelective(DnsPublicDomain record);

    DnsPublicDomain selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DnsPublicDomain record);

    int updateByPrimaryKey(DnsPublicDomain record);
}