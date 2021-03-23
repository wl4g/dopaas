/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.cmdb.service.impl;

import com.wl4g.component.common.lang.Assert2;
import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.page.PageHolder;
import com.wl4g.dopaas.cmdb.data.DnsPrivateResolutionDao;
import com.wl4g.dopaas.cmdb.data.DnsPrivateZoneDao;
import com.wl4g.dopaas.cmdb.handler.DnsZoneHandler;
import com.wl4g.dopaas.common.bean.cmdb.DnsPrivateResolution;
import com.wl4g.dopaas.common.bean.cmdb.DnsPrivateZone;
import com.wl4g.dopaas.cmdb.service.DnsPrivateZoneService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class DnsPrivateZoneServiceImpl implements DnsPrivateZoneService {

	private @Autowired DnsPrivateZoneDao dnsPrivateDomainDao;

	private @Autowired DnsPrivateResolutionDao privateResolutionDao;

	private @Autowired DnsZoneHandler dnsZoneHandler;

	@Override
	public PageHolder<DnsPrivateZone> page(PageHolder<DnsPrivateZone> pm, String zone) {
		pm.useCount().bindPage();
		List<DnsPrivateZone> list = dnsPrivateDomainDao.list(getRequestOrganizationCodes(), zone);
		pm.setRecords(list);
		return pm;
	}

	public void save(DnsPrivateZone privateZone) {
		DnsPrivateZone dnsPrivateZoneDB = dnsPrivateDomainDao.selectByZone(privateZone.getZone());
		if (isNull(privateZone.getId())) {
			Assert2.isNull(dnsPrivateZoneDB, "repeat zone");
			privateZone.preInsert(getRequestOrganizationCode());
			privateZone.setStatus("RUNNING");
			insert(privateZone);
		} else {
			Assert2.isTrue(Objects.isNull(dnsPrivateZoneDB) || dnsPrivateZoneDB.getId().equals(privateZone.getId()),
					"repeat zone");
			privateZone.preUpdate();
			update(privateZone);
		}
		List<DnsPrivateResolution> dnsPrivateResolutions = privateResolutionDao.selectByDomainId(privateZone.getId());
		privateZone.setDnsPrivateResolutions(dnsPrivateResolutions);
		dnsZoneHandler.putDomian(privateZone);
	}

	private void insert(DnsPrivateZone privateZone) {
		dnsPrivateDomainDao.insertSelective(privateZone);
	}

	private void update(DnsPrivateZone privateZone) {
		dnsPrivateDomainDao.updateByPrimaryKeySelective(privateZone);
	}

	public DnsPrivateZone detail(Long id) {
		Assert.notNull(id, "id is null");
		return dnsPrivateDomainDao.selectByPrimaryKey(id);
	}

	public void del(Long id) {
		Assert.notNull(id, "id is null");
		DnsPrivateZone dnsPrivateDomain = dnsPrivateDomainDao.selectByPrimaryKey(id);
		dnsPrivateDomain.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		dnsPrivateDomainDao.updateByPrimaryKeySelective(dnsPrivateDomain);
		dnsZoneHandler.delDomain(dnsPrivateDomain.getZone());
	}

	@Override
	public void loadDnsAtStart() {
		List<DnsPrivateZone> list = dnsPrivateDomainDao.list(null, null);
		for (DnsPrivateZone dnsPrivateDomain : list) {
			List<DnsPrivateResolution> dnsPrivateResolutions = privateResolutionDao.selectByDomainId(dnsPrivateDomain.getId());
			dnsPrivateDomain.setDnsPrivateResolutions(dnsPrivateResolutions);
			dnsZoneHandler.putDomian(dnsPrivateDomain);
		}
	}

}