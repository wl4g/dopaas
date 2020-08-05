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

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.components.common.serialize.JacksonUtils;
import com.wl4g.components.core.bean.erm.AppCluster;
import com.wl4g.components.core.bean.erm.AppEnvironment;
import com.wl4g.components.core.bean.erm.AppInstance;
import com.wl4g.components.core.bean.erm.DockerRepository;
import com.wl4g.components.core.bean.iam.Dict;
import com.wl4g.devops.dao.erm.AppClusterDao;
import com.wl4g.devops.dao.erm.AppEnvironmentDao;
import com.wl4g.devops.dao.erm.AppInstanceDao;
import com.wl4g.devops.dao.iam.DictDao;
import com.wl4g.devops.erm.service.AppClusterService;
import com.wl4g.devops.page.PageModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.wl4g.components.core.bean.BaseBean.DEL_FLAG_DELETE;
import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCodes;

@Service
@Transactional
public class AppClueterServiceImpl implements AppClusterService {

	@Autowired
	private AppClusterDao appClusterDao;

	@Autowired
	private AppInstanceDao appInstanceDao;

	@Autowired
	private AppEnvironmentDao appEnvironmentDao;

	@Autowired
	private DictDao dictDao;

	@Override
	public Map<String, Object> list(PageModel pm, String clusterName, Integer deployType) {
		Map<String, Object> data = new HashMap<>();
		Page<AppCluster> page = PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true);
		List<AppCluster> list = appClusterDao.list(getRequestOrganizationCodes(), clusterName, deployType);
		for (AppCluster appCluster : list) {
			int count = appInstanceDao.countByClusterId(appCluster.getId());
			appCluster.setInstanceCount(count);
		}
		pm.setTotal(page.getTotal());
		data.put("page", pm);
		data.put("list", list);
		return data;
	}

	@Override
	public List<AppCluster> clusters() {
		return appClusterDao.list(getRequestOrganizationCodes(), null, null);
	}

	@Override
	public void save(AppCluster appCluster) {
		if (appCluster.getId() == null) {
			insert(appCluster);
		} else {
			update(appCluster);
		}
	}

	private void insert(AppCluster appCluster) {
		appCluster.preInsert(getRequestOrganizationCode());
		appClusterDao.insertSelective(appCluster);
		saveEnvironments(appCluster);
	}

	private void saveEnvironments(AppCluster appCluster) {
		List<AppEnvironment> environments = appCluster.getEnvironments();
		appEnvironmentDao.deleteByClusterId(appCluster.getId());
		if (!CollectionUtils.isEmpty(environments)) {
			for (AppEnvironment environment : environments) {
				environment.preInsert();
				environment.setOrganizationCode(appCluster.getOrganizationCode());
				environment.setClusterId(appCluster.getId());
				if (Objects.nonNull(environment.getDockerRepository())) {
					environment.setCustomRepositoryConfig(JacksonUtils.toJSONString(environment.getDockerRepository()));
				}
			}
			appEnvironmentDao.insertBatch(environments);
		} else {
			environments = new ArrayList<>();
			List<Dict> appNsTypes = dictDao.selectByType("app_ns_type");
			for (Dict appNsType : appNsTypes) {
				AppEnvironment environment = new AppEnvironment();
				environment.preInsert();
				environment.setOrganizationCode(appCluster.getOrganizationCode());
				environment.setClusterId(appCluster.getId());
				environment.setEnvType(appNsType.getValue());
				environments.add(environment);
			}
			appEnvironmentDao.insertBatch(environments);
		}

	}

	private void update(AppCluster appCluster) {
		appCluster.preUpdate();
		appClusterDao.updateByPrimaryKeySelective(appCluster);

		saveEnvironments(appCluster);
	}

	public void del(Integer clusterId) {
		AppCluster appCluster = new AppCluster();
		appCluster.setId(clusterId);
		appCluster.setDelFlag(DEL_FLAG_DELETE);
		appClusterDao.updateByPrimaryKeySelective(appCluster);
	}

	@Override
	public AppCluster detail(Integer clusterId) {
		Assert.notNull(clusterId, "clusterId is null");
		AppCluster appCluster = appClusterDao.selectByPrimaryKey(clusterId);

		List<AppEnvironment> environments = appEnvironmentDao.selectByClusterId(clusterId);
		if (CollectionUtils.isEmpty(environments)) {
			environments = new ArrayList<>();
			List<Dict> appNsTypes = dictDao.selectByType("app_ns_type");
			for (Dict appNsType : appNsTypes) {
				AppEnvironment environment = new AppEnvironment();
				environment.preInsert();
				environment.setOrganizationCode(appCluster.getOrganizationCode());
				environment.setClusterId(appCluster.getId());
				environment.setEnvType(appNsType.getValue());
				environments.add(environment);
			}
		}
		for (AppEnvironment appEnvironment : environments) {
			if (StringUtils.isNotBlank(appEnvironment.getCustomRepositoryConfig())) {
				DockerRepository dockerRepository = JacksonUtils.parseJSON(appEnvironment.getCustomRepositoryConfig(),
						DockerRepository.class);
				appEnvironment.setDockerRepository(dockerRepository);
			}
		}
		appCluster.setEnvironments(environments);
		// List<AppInstance> appInstances =
		// appInstanceDao.selectByClusterId(clusterId);
		// List<InstanceDtoModel> instanceDtoModels =
		// InstanceDtoModel.instanesToDtoModels(appInstances);
		// appCluster.setInstances(appInstances);
		// appCluster.setInstanceDtoModels(instanceDtoModels);
		return appCluster;
	}

	@Override
	public List<AppInstance> getInstancesByClusterIdAndEnvType(Integer clusterId, String envType) {
		Assert.notNull(clusterId, "clusterId is null");
		Assert.notNull(envType, "envType is null");
		return appInstanceDao.selectByClusterIdAndEnvType(clusterId, envType);
	}

}