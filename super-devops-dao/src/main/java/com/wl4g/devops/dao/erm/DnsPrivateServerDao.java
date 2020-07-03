package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DnsPrivateDomain;
import com.wl4g.devops.common.bean.erm.DnsPrivateServer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DnsPrivateServerDao {
	int deleteByPrimaryKey(Integer id);

	int insert(DnsPrivateServer record);

	int insertSelective(DnsPrivateServer record);

	DnsPrivateServer selectByPrimaryKey(Integer id);

	List<DnsPrivateDomain> list(@Param("organizationCodes") List<String> organizationCodes, @Param("name") String name);

	int updateByPrimaryKeySelective(DnsPrivateServer record);

	int updateByPrimaryKey(DnsPrivateServer record);
}