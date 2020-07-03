package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DnsPrivateZone;
import com.wl4g.devops.common.bean.erm.DnsPrivateResolution;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DnsPrivateResolutionDao {
	int deleteByPrimaryKey(Integer id);

	int insert(DnsPrivateResolution record);

	int insertSelective(DnsPrivateResolution record);

	DnsPrivateResolution selectByPrimaryKey(Integer id);

    List<DnsPrivateResolution> selectByDomainId(Integer domainId);

    List<DnsPrivateZone> list(@Param("organizationCodes") List<String> organizationCodes, @Param("host") String host, @Param("domainId") Integer domainId);

	int updateByPrimaryKeySelective(DnsPrivateResolution record);

	int updateByPrimaryKey(DnsPrivateResolution record);
}