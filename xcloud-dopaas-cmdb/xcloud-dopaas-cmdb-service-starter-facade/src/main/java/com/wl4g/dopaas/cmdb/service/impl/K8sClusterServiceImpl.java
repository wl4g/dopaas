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

import com.wl4g.component.common.id.SnowflakeIdGenerator;
import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.page.PageHolder;
import com.wl4g.dopaas.common.bean.cmdb.K8sCluster;
import com.wl4g.dopaas.common.bean.cmdb.K8sInstance;
import com.wl4g.dopaas.cmdb.data.K8sClusterDao;
import com.wl4g.dopaas.cmdb.data.K8sInstanceDao;
import com.wl4g.dopaas.cmdb.service.K8sClusterService;

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
public class K8sClusterServiceImpl implements K8sClusterService {

	@Autowired
	private K8sClusterDao k8sClusterDao;

	@Autowired
	private K8sInstanceDao k8sInstanceDao;

	@Override
	public PageHolder<K8sCluster> page(PageHolder<K8sCluster> pm, String name) {
		pm.useCount().bindPage();
		pm.setRecords(k8sClusterDao.list(getRequestOrganizationCodes(), name));
		return pm;
	}

	@Override
	public List<K8sCluster> getForSelect() {
		return k8sClusterDao.list(getRequestOrganizationCodes(), null);
	}

	public void save(K8sCluster k8sCluster) {
		if (isNull(k8sCluster.getId())) {
			k8sCluster.preInsert(getRequestOrganizationCode());
			insert(k8sCluster);
		} else {
			k8sCluster.preUpdate();
			update(k8sCluster);
		}
	}

	private void insert(K8sCluster k8sCluster) {
		k8sClusterDao.insertSelective(k8sCluster);
		List<Long> hostIds = k8sCluster.getHostIds();
		if (!CollectionUtils.isEmpty(hostIds)) {
			List<K8sInstance> k8sInstances = new ArrayList<>();
			for (Long hostId : hostIds) {
				K8sInstance k8sInstance = new K8sInstance();
				k8sInstance.preInsert();
				k8sInstance.setId(SnowflakeIdGenerator.getDefault().nextId());
				k8sInstance.setHostId(hostId);
				k8sInstance.setK8sId(k8sCluster.getId());
				k8sInstances.add(k8sInstance);
			}
			k8sInstanceDao.insertBatch(k8sInstances);
		}
	}

	private void update(K8sCluster k8sCluster) {
		k8sClusterDao.updateByPrimaryKeySelective(k8sCluster);

		k8sInstanceDao.deleteByK8sId(k8sCluster.getId());
		List<Long> hostIds = k8sCluster.getHostIds();
		if (!CollectionUtils.isEmpty(hostIds)) {
			List<K8sInstance> k8sInstances = new ArrayList<>();
			for (Long hostId : hostIds) {
				K8sInstance k8sInstance = new K8sInstance();
				k8sInstance.preInsert();
				k8sInstance.setId(SnowflakeIdGenerator.getDefault().nextId());
				k8sInstance.setHostId(hostId);
				k8sInstance.setK8sId(k8sCluster.getId());
				k8sInstances.add(k8sInstance);
			}
			k8sInstanceDao.insertBatch(k8sInstances);
		}
	}

	public K8sCluster detail(Long id) {
		Assert.notNull(id, "id is null");
		K8sCluster k8sCluster = k8sClusterDao.selectByPrimaryKey(id);
		List<Long> hostIds = k8sInstanceDao.selectHostIdByK8sId(id);
		k8sCluster.setHostIds(hostIds);
		return k8sCluster;
	}

	public void del(Long id) {
		Assert.notNull(id, "id is null");
		K8sCluster k8sCluster = new K8sCluster();
		k8sCluster.setId(id);
		k8sCluster.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		k8sClusterDao.updateByPrimaryKeySelective(k8sCluster);
	}

}