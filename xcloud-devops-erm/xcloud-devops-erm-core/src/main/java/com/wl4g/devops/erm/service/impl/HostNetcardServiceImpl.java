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
package com.wl4g.devops.erm.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.core.bean.erm.HostNetcard;
import com.wl4g.components.core.bean.erm.HostTunnelOpenvpn;
import com.wl4g.components.core.bean.erm.HostTunnelPptp;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dao.erm.HostNetcardDao;
import com.wl4g.devops.dao.erm.HostTunnelOpenvpnDao;
import com.wl4g.devops.dao.erm.HostTunnelPptpDao;
import com.wl4g.devops.erm.service.HostNetcardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class HostNetcardServiceImpl implements HostNetcardService {

	@Autowired
	private HostNetcardDao appHostNetCardDao;

	@Autowired
	private HostTunnelOpenvpnDao hostTunnelOpenvpnDao;

	@Autowired
	private HostTunnelPptpDao hostTunnelPptpDao;

	@Override
	public PageModel<HostNetcard> page(PageModel<HostNetcard> pm, Long hostId, String name) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(appHostNetCardDao.list(getRequestOrganizationCodes(), hostId, name));
		return pm;
	}

	public void save(HostNetcard hostNetcard) {
		if (isNull(hostNetcard.getId())) {
			hostNetcard.preInsert(getRequestOrganizationCode());
			insert(hostNetcard);
		} else {
			hostNetcard.preUpdate();
			update(hostNetcard);
		}
	}

	private void insert(HostNetcard hostNetcard) {
		appHostNetCardDao.insertSelective(hostNetcard);
	}

	private void update(HostNetcard hostNetcard) {
		appHostNetCardDao.updateByPrimaryKeySelective(hostNetcard);
	}

	@Override
	public HostNetcard detail(Long id) {
		Assert.notNull(id, "id is null");
		return appHostNetCardDao.selectByPrimaryKey(id);
	}

	@Override
	public void del(Long id) {
		Assert.notNull(id, "id is null");
		HostNetcard hostNetcard = new HostNetcard();
		hostNetcard.setId(id);
		hostNetcard.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		appHostNetCardDao.updateByPrimaryKeySelective(hostNetcard);
	}

	@Override
	public Map<String, Object> getHostTunnel() {
		Map<String, Object> resutl = new HashMap<>();
		List<HostTunnelOpenvpn> hostTunnelOpenvpns = hostTunnelOpenvpnDao.selectAll(getRequestOrganizationCodes());
		List<HostTunnelPptp> hostTunnelPptps = hostTunnelPptpDao.selectAll(getRequestOrganizationCodes());
		resutl.put("openvpn", hostTunnelOpenvpns);
		resutl.put("pptp", hostTunnelPptps);
		return resutl;
	}

}