package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DnsPrivateZone;
import com.wl4g.devops.common.bean.erm.DnsPublicZone;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DnsPublicZoneDao {
	int deleteByPrimaryKey(Integer id);

	int insert(DnsPublicZone record);

	int insertSelective(DnsPublicZone record);

	DnsPublicZone selectByPrimaryKey(Integer id);

	List<DnsPrivateZone> list(@Param("organizationCodes") List<String> organizationCodes, @Param("zone") String zone);

	int updateByPrimaryKeySelective(DnsPublicZone record);

	int updateByPrimaryKey(DnsPublicZone record);

}