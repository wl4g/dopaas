package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DnsPrivateBlacklist;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DnsPrivateBlacklistDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DnsPrivateBlacklist record);

    int insertSelective(DnsPrivateBlacklist record);

    DnsPrivateBlacklist selectByPrimaryKey(Integer id);

    DnsPrivateBlacklist selectByExpression(String expression);

    List<DnsPrivateBlacklist> list(@Param("expression") String expression);

    int updateByPrimaryKeySelective(DnsPrivateBlacklist record);

    int updateByPrimaryKey(DnsPrivateBlacklist record);
}