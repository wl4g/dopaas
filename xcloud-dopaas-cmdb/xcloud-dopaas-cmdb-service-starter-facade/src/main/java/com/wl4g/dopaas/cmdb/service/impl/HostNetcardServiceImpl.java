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

import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.page.PageHolder;
import com.wl4g.dopaas.common.bean.cmdb.HostNetcard;
import com.wl4g.dopaas.common.bean.cmdb.HostTunnelOpenvpn;
import com.wl4g.dopaas.common.bean.cmdb.HostTunnelPptp;
import com.wl4g.dopaas.cmdb.data.HostNetcardDao;
import com.wl4g.dopaas.cmdb.data.HostTunnelOpenvpnDao;
import com.wl4g.dopaas.cmdb.data.HostTunnelPptpDao;
import com.wl4g.dopaas.cmdb.service.HostNetcardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class HostNetcardServiceImpl implements HostNetcardService {

	private @Autowired HostNetcardDao appHostNetCardDao;

	private @Autowired HostTunnelOpenvpnDao hostTunnelOpenvpnDao;

	private @Autowired HostTunnelPptpDao hostTunnelPptpDao;

	@Override
	public PageHolder<HostNetcard> page(PageHolder<HostNetcard> pm, Long hostId, String name) {
		pm.useCount().bind();
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