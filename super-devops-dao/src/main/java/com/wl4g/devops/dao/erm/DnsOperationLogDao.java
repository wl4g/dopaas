package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DnsOperationLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DnsOperationLogDao {
	int deleteByPrimaryKey(Integer id);

	int insert(DnsOperationLog record);

	int insertSelective(DnsOperationLog record);

	DnsOperationLog selectByPrimaryKey(Integer id);

	List<DnsOperationLog> list(@Param("organizationCodes") List<String> organizationCodes, @Param("domain") String domain);

	int updateByPrimaryKeySelective(DnsOperationLog record);

	int updateByPrimaryKey(DnsOperationLog record);
}