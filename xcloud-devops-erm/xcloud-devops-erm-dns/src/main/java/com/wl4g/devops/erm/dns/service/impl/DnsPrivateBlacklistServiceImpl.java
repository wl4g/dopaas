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
import com.wl4g.components.core.bean.erm.DnsPrivateBlacklist;
import com.wl4g.devops.dao.erm.DnsPrivateBlacklistDao;
import com.wl4g.devops.erm.dns.handler.DnsZoneHandler;
import com.wl4g.devops.erm.dns.service.DnsPrivateBlacklistService;
import com.wl4g.devops.page.PageModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class DnsPrivateBlacklistServiceImpl implements DnsPrivateBlacklistService {

	final private static String BLACKLIST = "1";
	// final public static String WHITELIST = "2";

	@Autowired
	private DnsPrivateBlacklistDao dnsPrivateBlacklistDao;

	@Autowired
	private DnsZoneHandler dnsServerInterface;

	@Override
	public PageModel page(PageModel pm, String expression) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(dnsPrivateBlacklistDao.list(expression));
		return pm;
	}

	public void save(DnsPrivateBlacklist dnsPrivateBlacklist) {
		if (isNull(dnsPrivateBlacklist.getId())) {
			dnsPrivateBlacklist.preInsert(getRequestOrganizationCode());
			insert(dnsPrivateBlacklist);
		} else {
			dnsPrivateBlacklist.preUpdate();
			update(dnsPrivateBlacklist);
		}
		if (dnsPrivateBlacklist.getEnable() == 1) {
			if (StringUtils.equals(BLACKLIST, dnsPrivateBlacklist.getType())) {
				dnsServerInterface.addDnsPrivateBlacklist(dnsPrivateBlacklist.getExpression(), null);
			} else {
				dnsServerInterface.addDnsPrivateBlacklist(null, dnsPrivateBlacklist.getExpression());
			}
		} else {
			if (StringUtils.equals(BLACKLIST, dnsPrivateBlacklist.getType())) {
				dnsServerInterface.removeDnsPrivateBlacklist(dnsPrivateBlacklist.getExpression(), null);
			} else {
				dnsServerInterface.removeDnsPrivateBlacklist(null, dnsPrivateBlacklist.getExpression());
			}
		}
	}

	private void insert(DnsPrivateBlacklist dnsPrivateBlacklist) {
		DnsPrivateBlacklist dnsPrivateBlacklistDB = dnsPrivateBlacklistDao
				.selectByExpression(dnsPrivateBlacklist.getExpression());
		Assert2.isNull(dnsPrivateBlacklistDB, "Repeat Expression");
		dnsPrivateBlacklistDao.insertSelective(dnsPrivateBlacklist);
	}

	private void update(DnsPrivateBlacklist dnsPrivateBlacklist) {
		dnsPrivateBlacklistDao.updateByPrimaryKeySelective(dnsPrivateBlacklist);
	}

	public DnsPrivateBlacklist detail(Integer id) {
		Assert.notNull(id, "id is null");
		return dnsPrivateBlacklistDao.selectByPrimaryKey(id);
	}

	public void del(Integer id) {
		Assert.notNull(id, "id is null");
		DnsPrivateBlacklist dnsPrivateBlacklist = dnsPrivateBlacklistDao.selectByPrimaryKey(id);
		dnsPrivateBlacklist.setId(id);
		dnsPrivateBlacklist.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		dnsPrivateBlacklistDao.updateByPrimaryKeySelective(dnsPrivateBlacklist);
		if (StringUtils.equals(BLACKLIST, dnsPrivateBlacklist.getType())) {
			dnsServerInterface.removeDnsPrivateBlacklist(dnsPrivateBlacklist.getExpression(), null);
		} else {
			dnsServerInterface.removeDnsPrivateBlacklist(null, dnsPrivateBlacklist.getExpression());
		}
	}

	@Override
	public void loadBlacklistAtStart() {
		List<DnsPrivateBlacklist> list = dnsPrivateBlacklistDao.list(null);
		Set<String> blacks = new HashSet<>();
		Set<String> whites = new HashSet<>();
		for (DnsPrivateBlacklist dnsPrivateBlacklist : list) {
			if (StringUtils.equals(BLACKLIST, dnsPrivateBlacklist.getType())) {
				blacks.add(dnsPrivateBlacklist.getExpression());
			} else {
				whites.add(dnsPrivateBlacklist.getExpression());
			}
		}
		dnsServerInterface.reloadDnsPrivateBlacklist(blacks, whites);
	}

}