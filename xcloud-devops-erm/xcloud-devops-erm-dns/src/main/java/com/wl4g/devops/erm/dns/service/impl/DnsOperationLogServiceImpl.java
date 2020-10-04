/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.erm.dns.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.core.bean.erm.DnsOperationLog;
import com.wl4g.devops.dao.erm.DnsOperationLogDao;
import com.wl4g.devops.erm.dns.service.DnsOperationLogService;
import com.wl4g.devops.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class DnsOperationLogServiceImpl implements DnsOperationLogService {

	@Autowired
	private DnsOperationLogDao dnsOperationLogDao;

	@Override
	public PageModel page(PageModel pm, String domain) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(dnsOperationLogDao.list(getRequestOrganizationCodes(), domain));
		return pm;
	}

	public void save(DnsOperationLog dnsOperationLog) {
		if (isNull(dnsOperationLog.getId())) {
			dnsOperationLog.preInsert(getRequestOrganizationCode());
			insert(dnsOperationLog);
		} else {
			dnsOperationLog.preUpdate();
			update(dnsOperationLog);
		}
	}

	private void insert(DnsOperationLog dnsOperationLog) {
		dnsOperationLogDao.insertSelective(dnsOperationLog);
	}

	private void update(DnsOperationLog dnsOperationLog) {
		dnsOperationLogDao.updateByPrimaryKeySelective(dnsOperationLog);
	}

	public DnsOperationLog detail(Long id) {
		Assert.notNull(id, "id is null");
		return dnsOperationLogDao.selectByPrimaryKey(id);
	}

	public void del(Long id) {
		Assert.notNull(id, "id is null");
		DnsOperationLog dnsOperationLog = new DnsOperationLog();
		dnsOperationLog.setId(id);
		dnsOperationLog.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		dnsOperationLogDao.updateByPrimaryKeySelective(dnsOperationLog);
	}

}