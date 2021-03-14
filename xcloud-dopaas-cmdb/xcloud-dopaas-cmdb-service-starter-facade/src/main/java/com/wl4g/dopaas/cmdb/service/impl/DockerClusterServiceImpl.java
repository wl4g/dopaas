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
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.dopaas.common.bean.cmdb.DockerCluster;
import com.wl4g.dopaas.common.bean.cmdb.DockerInstance;
import com.wl4g.dopaas.cmdb.data.DockerClusterDao;
import com.wl4g.dopaas.cmdb.data.DockerInstanceDao;
import com.wl4g.dopaas.cmdb.service.DockerClusterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCodes;
import static java.util.Objects.isNull;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class DockerClusterServiceImpl implements DockerClusterService {

	@Autowired
	private DockerClusterDao dockerClusterDao;

	@Autowired
	private DockerInstanceDao dockerInstanceDao;

	@Override
	public PageHolder<DockerCluster> page(PageHolder<DockerCluster> pm, String name) {
		pm.count().startPage();
		pm.setRecords(dockerClusterDao.list(getRequestOrganizationCodes(), name));
		return pm;
	}

	@Override
	public List<DockerCluster> getForSelect() {
		return dockerClusterDao.list(getRequestOrganizationCodes(), null);
	}

	public void save(DockerCluster dockerCluster) {
		if (isNull(dockerCluster.getId())) {
			dockerCluster.preInsert(getRequestOrganizationCode());
			insert(dockerCluster);
		} else {
			dockerCluster.preUpdate();
			update(dockerCluster);
		}
	}

	private void insert(DockerCluster dockerCluster) {
		dockerClusterDao.insertSelective(dockerCluster);
		List<Long> hostIds = dockerCluster.getHostIds();
		if (!CollectionUtils.isEmpty(hostIds)) {
			List<DockerInstance> dockerInstances = new ArrayList<>();
			for (Long hostId : hostIds) {
				DockerInstance dockerInstance = new DockerInstance();
				dockerInstance.preInsert();
				dockerInstance.setHostId(hostId);
				dockerInstance.setDockerId(dockerCluster.getId());
				dockerInstances.add(dockerInstance);
			}
			dockerInstanceDao.insertBatch(dockerInstances);
		}
	}

	private void update(DockerCluster dockerCluster) {
		dockerClusterDao.updateByPrimaryKeySelective(dockerCluster);
		dockerInstanceDao.deleteByDockerId(dockerCluster.getId());
		List<Long> hostIds = dockerCluster.getHostIds();
		if (!CollectionUtils.isEmpty(hostIds)) {
			List<DockerInstance> dockerInstances = new ArrayList<>();
			for (Long hostId : hostIds) {
				DockerInstance dockerInstance = new DockerInstance();
				dockerInstance.preInsert();
				dockerInstance.setHostId(hostId);
				dockerInstance.setDockerId(dockerCluster.getId());
				dockerInstances.add(dockerInstance);
			}
			dockerInstanceDao.insertBatch(dockerInstances);
		}
	}

	public DockerCluster detail(Long id) {
		Assert.notNull(id, "id is null");
		DockerCluster dockerCluster = dockerClusterDao.selectByPrimaryKey(id);
		List<Long> hostIds = dockerInstanceDao.selectHostIdByDockerId(id);
		dockerCluster.setHostIds(hostIds);
		return dockerCluster;
	}

	public void del(Long id) {
		Assert.notNull(id, "id is null");
		DockerCluster dockerCluster = new DockerCluster();
		dockerCluster.setId(id);
		dockerCluster.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		dockerClusterDao.updateByPrimaryKeySelective(dockerCluster);
	}

}