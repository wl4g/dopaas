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
import com.wl4g.devops.common.bean.gw.GWUpstreamGroup;
import com.wl4g.devops.dao.gw.GWUpstreamGroupDao;
import com.wl4g.devops.gateway.service.UpstreamGroupService;
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
public class UpstreamGroupServiceImpl implements UpstreamGroupService {

	@Autowired
	private GWUpstreamGroupDao gwUpstreamGroupDao;

	@Override
	public PageModel page(PageModel pm, String name) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(gwUpstreamGroupDao.list(getRequestOrganizationCodes(), name));
		return pm;
	}

	@Override
	public List<GWUpstreamGroup> getForSelect() {
		return gwUpstreamGroupDao.list(getRequestOrganizationCodes(), null);
	}

	public void save(GWUpstreamGroup gwUpstreamGroup) {
		if (isNull(gwUpstreamGroup.getId())) {
			gwUpstreamGroup.preInsert(getRequestOrganizationCode());
			insert(gwUpstreamGroup);
		} else {
			gwUpstreamGroup.preUpdate();
			update(gwUpstreamGroup);
		}
	}

	private void insert(GWUpstreamGroup gwUpstreamGroup) {
		gwUpstreamGroupDao.insertSelective(gwUpstreamGroup);
	}

	private void update(GWUpstreamGroup gwUpstreamGroup) {
		gwUpstreamGroupDao.updateByPrimaryKeySelective(gwUpstreamGroup);
	}

	public GWUpstreamGroup detail(Integer id) {
		Assert.notNull(id, "id is null");
		return gwUpstreamGroupDao.selectByPrimaryKey(id);
	}

	public void del(Integer id) {
		Assert.notNull(id, "id is null");
		GWUpstreamGroup gwUpstreamGroup = new GWUpstreamGroup();
		gwUpstreamGroup.setId(id);
		gwUpstreamGroup.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		gwUpstreamGroupDao.updateByPrimaryKeySelective(gwUpstreamGroup);
	}

}