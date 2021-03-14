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
package com.wl4g.paas.cmdb.service.impl;

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.paas.common.bean.cmdb.DnsPublicZone;
import com.wl4g.paas.cmdb.data.DnsPublicZoneDao;
import com.wl4g.paas.cmdb.service.DnsPublicZoneService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class DnsPublicZoneServiceImpl implements DnsPublicZoneService {

	@Autowired
	private DnsPublicZoneDao publicZoneDao;

	@Override
	public PageHolder<DnsPublicZone> page(PageHolder<DnsPublicZone> pm, String zone) {
		pm.count().startPage();
		pm.setRecords(publicZoneDao.list(getRequestOrganizationCodes(), zone));
		return pm;
	}

	public void save(DnsPublicZone dnsPublicDomain) {
		if (isNull(dnsPublicDomain.getId())) {
			dnsPublicDomain.preInsert(getRequestOrganizationCode());
			insert(dnsPublicDomain);
		} else {
			dnsPublicDomain.preUpdate();
			update(dnsPublicDomain);
		}
	}

	private void insert(DnsPublicZone dnsPublicDomain) {
		publicZoneDao.insertSelective(dnsPublicDomain);
	}

	private void update(DnsPublicZone dnsPublicDomain) {
		publicZoneDao.updateByPrimaryKeySelective(dnsPublicDomain);
	}

	public DnsPublicZone detail(Long id) {
		Assert.notNull(id, "id is null");
		return publicZoneDao.selectByPrimaryKey(id);
	}

	public void del(Long id) {
		Assert.notNull(id, "id is null");
		/*
		 * DnsPublicZone dnsPublicDomain = new DnsPublicZone();
		 * dnsPublicDomain.setId(id);
		 * dnsPublicDomain.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		 * dnsPublicDomainDao.updateByPrimaryKeySelective(dnsPublicDomain);
		 */
		publicZoneDao.deleteByPrimaryKey(id);
	}

}