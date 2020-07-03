package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DnsPrivateDomain;
import com.wl4g.devops.common.bean.erm.DnsPrivateResolution;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DnsPrivateResolutionDao {
	int deleteByPrimaryKey(Integer id);

	int insert(DnsPrivateResolution record);

	int insertSelective(DnsPrivateResolution record);

	DnsPrivateResolution selectByPrimaryKey(Integer id);

	List<DnsPrivateDomain> list(@Param("organizationCodes") List<String> organizationCodes, @Param("host") String host);

	int updateByPrimaryKeySelective(DnsPrivateResolution record);

	int updateByPrimaryKey(DnsPrivateResolution record);
}