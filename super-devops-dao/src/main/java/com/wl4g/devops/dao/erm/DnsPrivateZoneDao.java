package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DnsPrivateZone;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DnsPrivateZoneDao {
	int deleteByPrimaryKey(Integer id);

	int insert(DnsPrivateZone record);

	int insertSelective(DnsPrivateZone record);

	DnsPrivateZone selectByPrimaryKey(Integer id);

	List<DnsPrivateZone> list(@Param("organizationCodes") List<String> organizationCodes, @Param("zone") String zone);

	int updateByPrimaryKeySelective(DnsPrivateZone record);

	int updateByPrimaryKey(DnsPrivateZone record);
}