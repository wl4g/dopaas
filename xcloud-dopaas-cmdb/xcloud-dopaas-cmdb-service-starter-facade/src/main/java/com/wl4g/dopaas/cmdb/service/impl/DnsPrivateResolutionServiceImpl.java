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
import com.wl4g.dopaas.cmdb.service.DnsPrivateResolutionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;

import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class DnsPrivateResolutionServiceImpl implements DnsPrivateResolutionService {

	private @Autowired DnsPrivateResolutionDao privateResolutionDao;

	private @Autowired DnsZoneHandler dnsZoneHandler;

	private @Autowired DnsPrivateZoneDao dnsPrivateZoneDao;

	@Override
	public PageHolder<DnsPrivateResolution> page(PageHolder<DnsPrivateResolution> pm, String host, Long domainId) {
		pm.useCount().bind();
		pm.setRecords(privateResolutionDao.list(getRequestOrganizationCodes(), host, domainId));
		return pm;
	}

	public void save(DnsPrivateResolution resolution) {
		DnsPrivateResolution resolution0 = privateResolutionDao.selectByDomainIdAndHost(resolution.getDomainId(),
				resolution.getHost());

		if (isNull(resolution.getId())) {
			Assert2.isNull(resolution0, "repeat host");
			resolution.preInsert(getRequestOrganizationCode());
			insert(resolution);
		} else {
			Assert2.isTrue(Objects.isNull(resolution0) || resolution0.getId().equals(resolution.getId()), "repeat host");
			resolution.preUpdate();
			update(resolution);
		}
		DnsPrivateZone privateZone = dnsPrivateZoneDao.selectByPrimaryKey(resolution.getDomainId());
		dnsZoneHandler.putHost(privateZone, resolution);
	}

	private void insert(DnsPrivateResolution dnsPrivateResolution) {
		privateResolutionDao.insertSelective(dnsPrivateResolution);
	}

	private void update(DnsPrivateResolution dnsPrivateResolution) {
		privateResolutionDao.updateByPrimaryKeySelective(dnsPrivateResolution);
	}

	public DnsPrivateResolution detail(Long id) {
		Assert.notNull(id, "id is null");
		return privateResolutionDao.selectByPrimaryKey(id);
	}

	public void del(Long id) {
		Assert.notNull(id, "id is null");
		DnsPrivateResolution dnsPrivateResolution = privateResolutionDao.selectByPrimaryKey(id);
		DnsPrivateZone dnsPrivateDomain = dnsPrivateZoneDao.selectByPrimaryKey(dnsPrivateResolution.getDomainId());
		dnsPrivateResolution.setId(id);
		dnsPrivateResolution.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		privateResolutionDao.updateByPrimaryKeySelective(dnsPrivateResolution);
		dnsZoneHandler.delhost(dnsPrivateDomain.getZone(), dnsPrivateResolution.getHost());
	}

}