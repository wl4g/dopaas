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
package com.wl4g.devops.erm.service.impl;

import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.common.bean.erm.DnsPrivateServer;
import com.wl4g.devops.erm.data.DnsPrivateServerDao;
import com.wl4g.devops.erm.service.DnsPrivateServerService;

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
public class DnsPrivateServerServiceImpl implements DnsPrivateServerService {

	@Autowired
	private DnsPrivateServerDao dnsPrivateServerDao;

	@Override
	public PageHolder<DnsPrivateServer> page(PageHolder<DnsPrivateServer> pm, String name) {
		pm.startPage();
		pm.setRecords(dnsPrivateServerDao.list(getRequestOrganizationCodes(), name));
		return pm;
	}

	public void save(DnsPrivateServer dnsPrivateServer) {
		if (isNull(dnsPrivateServer.getId())) {
			dnsPrivateServer.preInsert(getRequestOrganizationCode());
			insert(dnsPrivateServer);
		} else {
			dnsPrivateServer.preUpdate();
			update(dnsPrivateServer);
		}
	}

	private void insert(DnsPrivateServer dnsPrivateServer) {
		dnsPrivateServerDao.insertSelective(dnsPrivateServer);
	}

	private void update(DnsPrivateServer dnsPrivateServer) {
		dnsPrivateServerDao.updateByPrimaryKeySelective(dnsPrivateServer);
	}

	public DnsPrivateServer detail(Long id) {
		Assert.notNull(id, "id is null");
		return dnsPrivateServerDao.selectByPrimaryKey(id);
	}

	public void del(Long id) {
		Assert.notNull(id, "id is null");
		DnsPrivateServer dnsPrivateServer = new DnsPrivateServer();
		dnsPrivateServer.setId(id);
		dnsPrivateServer.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		dnsPrivateServerDao.updateByPrimaryKeySelective(dnsPrivateServer);
	}

}