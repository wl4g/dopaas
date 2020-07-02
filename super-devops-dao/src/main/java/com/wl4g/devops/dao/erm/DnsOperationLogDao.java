package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.DnsOperationLog;

public interface DnsOperationLogDao {
	int deleteByPrimaryKey(Integer id);

	int insert(DnsOperationLog record);

	int insertSelective(DnsOperationLog record);

	DnsOperationLog selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(DnsOperationLog record);

	int updateByPrimaryKey(DnsOperationLog record);
}