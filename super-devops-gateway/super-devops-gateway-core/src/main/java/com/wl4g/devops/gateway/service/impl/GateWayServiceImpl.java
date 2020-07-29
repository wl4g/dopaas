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
import com.wl4g.devops.common.bean.gw.GWCluster;
import com.wl4g.devops.dao.gw.GWClusterDao;
import com.wl4g.devops.gateway.service.GatewayService;
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
public class GateWayServiceImpl implements GatewayService {

	@Autowired
	private GWClusterDao gwClusterDao;

	@Override
	public PageModel page(PageModel pm, String name) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(gwClusterDao.list(getRequestOrganizationCodes(), name));
		return pm;
	}

	@Override
	public List<GWCluster> getForSelect() {
		return gwClusterDao.list(getRequestOrganizationCodes(), null);
	}

	public void save(GWCluster gwCluster) {
		if (isNull(gwCluster.getId())) {
			gwCluster.preInsert(getRequestOrganizationCode());
			insert(gwCluster);
		} else {
			gwCluster.preUpdate();
			update(gwCluster);
		}
	}

	private void insert(GWCluster gwCluster) {
		gwClusterDao.insertSelective(gwCluster);
	}

	private void update(GWCluster gwCluster) {
		gwClusterDao.updateByPrimaryKeySelective(gwCluster);
	}

	public GWCluster detail(Integer id) {
		Assert.notNull(id, "id is null");
		return gwClusterDao.selectByPrimaryKey(id);
	}

	public void del(Integer id) {
		Assert.notNull(id, "id is null");
		GWCluster gwCluster = new GWCluster();
		gwCluster.setId(id);
		gwCluster.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		gwClusterDao.updateByPrimaryKeySelective(gwCluster);
	}

}