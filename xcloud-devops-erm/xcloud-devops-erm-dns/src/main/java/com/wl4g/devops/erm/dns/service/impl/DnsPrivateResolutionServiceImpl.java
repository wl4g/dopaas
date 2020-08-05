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
import com.wl4g.components.common.lang.Assert2;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.core.bean.erm.DnsPrivateResolution;
import com.wl4g.components.core.bean.erm.DnsPrivateZone;
import com.wl4g.devops.dao.erm.DnsPrivateZoneDao;
import com.wl4g.devops.dao.erm.DnsPrivateResolutionDao;
import com.wl4g.devops.erm.dns.handler.DnsZoneHandler;
import com.wl4g.devops.erm.dns.service.DnsPrivateResolutionService;
import com.wl4g.devops.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;

import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class DnsPrivateResolutionServiceImpl implements DnsPrivateResolutionService {

	@Autowired
	private DnsPrivateResolutionDao dnsPrivateResolutionDao;

	@Autowired
	private DnsZoneHandler dnsServerInterface;

	@Autowired
	private DnsPrivateZoneDao dnsPrivateDomainDao;

	@Override
	public PageModel page(PageModel pm, String host, Integer domainId) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(dnsPrivateResolutionDao.list(getRequestOrganizationCodes(), host, domainId));
		return pm;
	}

	public void save(DnsPrivateResolution dnsPrivateResolution) {

		DnsPrivateResolution dnsPrivateResolutionDB = dnsPrivateResolutionDao
				.selectByDomainIdAndHost(dnsPrivateResolution.getDomainId(), dnsPrivateResolution.getHost());

		if (isNull(dnsPrivateResolution.getId())) {
			Assert2.isNull(dnsPrivateResolutionDB, "repeat host");
			dnsPrivateResolution.preInsert(getRequestOrganizationCode());
			insert(dnsPrivateResolution);
		} else {
			Assert2.isTrue(
					Objects.isNull(dnsPrivateResolutionDB) || dnsPrivateResolutionDB.getId().equals(dnsPrivateResolution.getId()),
					"repeat host");
			dnsPrivateResolution.preUpdate();
			update(dnsPrivateResolution);
		}
		DnsPrivateZone dnsPrivateDomain = dnsPrivateDomainDao.selectByPrimaryKey(dnsPrivateResolution.getDomainId());
		dnsServerInterface.putHost(dnsPrivateDomain, dnsPrivateResolution);
	}

	private void insert(DnsPrivateResolution dnsPrivateResolution) {
		dnsPrivateResolutionDao.insertSelective(dnsPrivateResolution);
	}

	private void update(DnsPrivateResolution dnsPrivateResolution) {
		dnsPrivateResolutionDao.updateByPrimaryKeySelective(dnsPrivateResolution);
	}

	public DnsPrivateResolution detail(Integer id) {
		Assert.notNull(id, "id is null");
		return dnsPrivateResolutionDao.selectByPrimaryKey(id);
	}

	public void del(Integer id) {
		Assert.notNull(id, "id is null");
		DnsPrivateResolution dnsPrivateResolution = dnsPrivateResolutionDao.selectByPrimaryKey(id);
		DnsPrivateZone dnsPrivateDomain = dnsPrivateDomainDao.selectByPrimaryKey(dnsPrivateResolution.getDomainId());
		dnsPrivateResolution.setId(id);
		dnsPrivateResolution.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		dnsPrivateResolutionDao.updateByPrimaryKeySelective(dnsPrivateResolution);
		dnsServerInterface.delhost(dnsPrivateDomain.getZone(), dnsPrivateResolution.getHost());
	}

}