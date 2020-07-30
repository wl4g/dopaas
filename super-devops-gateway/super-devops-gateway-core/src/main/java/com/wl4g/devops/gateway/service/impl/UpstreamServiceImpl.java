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
package com.wl4g.devops.gateway.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.gw.GWUpstream;
import com.wl4g.devops.dao.gw.GWUpstreamDao;
import com.wl4g.devops.gateway.service.UpstreamService;
import com.wl4g.devops.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static com.wl4g.devops.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.devops.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class UpstreamServiceImpl implements UpstreamService {

	@Autowired
	private GWUpstreamDao gwUpstreamDao;

	@Override
	public PageModel page(PageModel pm, String name) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(gwUpstreamDao.list(getRequestOrganizationCodes(), name));
		return pm;
	}

	@Override
	public List<GWUpstream> getForSelect() {
		return gwUpstreamDao.list(getRequestOrganizationCodes(), null);
	}

	public void save(GWUpstream gwUpstream) {
		if (isNull(gwUpstream.getId())) {
			gwUpstream.preInsert(getRequestOrganizationCode());
			insert(gwUpstream);
		} else {
			gwUpstream.preUpdate();
			update(gwUpstream);
		}
	}

	private void insert(GWUpstream gwUpstream) {
		gwUpstreamDao.insertSelective(gwUpstream);
	}

	private void update(GWUpstream gwUpstream) {
		gwUpstreamDao.updateByPrimaryKeySelective(gwUpstream);
	}

	public GWUpstream detail(Integer id) {
		Assert.notNull(id, "id is null");
		return gwUpstreamDao.selectByPrimaryKey(id);
	}

	public void del(Integer id) {
		Assert.notNull(id, "id is null");
		GWUpstream gwUpstream = new GWUpstream();
		gwUpstream.setId(id);
		gwUpstream.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		gwUpstreamDao.updateByPrimaryKeySelective(gwUpstream);
	}

}