package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DnsPrivateDomain;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DnsPrivateDomainDao {
	int deleteByPrimaryKey(Integer id);

	int insert(DnsPrivateDomain record);

	int insertSelective(DnsPrivateDomain record);

	DnsPrivateDomain selectByPrimaryKey(Integer id);

	List<DnsPrivateDomain> list(@Param("organizationCodes") List<String> organizationCodes, @Param("zone") String zone);

	int updateByPrimaryKeySelective(DnsPrivateDomain record);

	int updateByPrimaryKey(DnsPrivateDomain record);
}