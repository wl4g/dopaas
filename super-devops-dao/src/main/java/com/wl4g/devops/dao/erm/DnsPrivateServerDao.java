package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DnsPrivateServer;

public interface DnsPrivateServerDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DnsPrivateServer record);

    int insertSelective(DnsPrivateServer record);

    DnsPrivateServer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DnsPrivateServer record);

    int updateByPrimaryKey(DnsPrivateServer record);
}