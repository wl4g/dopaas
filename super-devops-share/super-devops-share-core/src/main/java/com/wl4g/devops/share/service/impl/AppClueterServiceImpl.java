/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.share.service.impl;

import com.wl4g.devops.common.bean.share.*;
import com.wl4g.devops.dao.scm.AppClusterDao;
import com.wl4g.devops.share.service.AppClusterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AppClueterServiceImpl implements AppClusterService {

	@Autowired
	private AppClusterDao appClusterDao;

	@Override
	public void insert(InstanceOfGroup iog) {
		iog.preInsert();
		appClusterDao.insert(iog);
		if (null != iog.getAppInstance() && !iog.getAppInstance().isEmpty()) {
			appClusterDao.insertInstance(iog);
		}
	}

	@Override
	public boolean delete(AppCluster group) {
		group.preUpdate();
		return appClusterDao.delete(group);
	}

	@Override
	public boolean deleteEnv(Environment group) {
		group.preUpdate();
		return appClusterDao.deleteEnv(group);
	}

	@Override
	public boolean update(AppCluster group) {
		group.preUpdate();
		return appClusterDao.update(group);
	}

	@Override
	public InstanceOfGroup select(AppCluster group) {
		return appClusterDao.select(group);
	}

	@Override
	public InstanceOfGroup selectEnv(AppCluster group) {
		return appClusterDao.selectEnv(group);
	}

	@Override
	public List<AppGroupList> list(AppGroupList agl) {
		return appClusterDao.list(agl);
	}

	public List<AppGroupList> groupEnvlist(AppGroupList agl) {
		return appClusterDao.groupEnvlist(agl);
	}

	@Override
	public boolean insertInstance(InstanceOfGroup iog) {
		iog.preInsert();
		return appClusterDao.insertInstance(iog);
	}

	public boolean insertEnvironment(InstanceOfGroup iog) {
		iog.preInsert();
		return appClusterDao.insertEnvironment(iog);
	}

	@Override
	public boolean deleteInstance(AppInstance instance) {
		instance.preUpdate();
		return appClusterDao.deleteInstance(instance);
	}

	@Override
	public boolean updateInstance(AppInstance instance) {
		instance.preUpdate();
		return appClusterDao.updateInstance(instance);
	}

	public boolean updateEnvironment(Environment instance) {
		instance.preUpdate();
		return appClusterDao.updateEnvironment(instance);
	}

	@Override
	public List<AppCluster> grouplist() {
		return appClusterDao.grouplist();
	}

	public List<Environment> environmentlist(String appClusterId) {
		return appClusterDao.environmentlist(appClusterId);
	}

	public List<AppInstance> instancelist(AppInstance appInstance) {
		return appClusterDao.instancelist(appInstance);
	}

	@Override
	public String selectEnvName(String envId) {
		return appClusterDao.selectEnvName(envId);
	}

	@Override
	public AppInstance getAppInstance(String id) {
		return appClusterDao.getAppInstance(id);
	}
}