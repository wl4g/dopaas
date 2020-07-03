package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DnsPrivateDomain;
import com.wl4g.devops.common.bean.erm.DnsPublicDomain;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DnsPublicDomainDao {
	int deleteByPrimaryKey(Integer id);

	int insert(DnsPublicDomain record);

	int insertSelective(DnsPublicDomain record);

	DnsPublicDomain selectByPrimaryKey(Integer id);

	List<DnsPrivateDomain> list(@Param("organizationCodes") List<String> organizationCodes, @Param("zone") String zone);

	int updateByPrimaryKeySelective(DnsPublicDomain record);

	int updateByPrimaryKey(DnsPublicDomain record);

}