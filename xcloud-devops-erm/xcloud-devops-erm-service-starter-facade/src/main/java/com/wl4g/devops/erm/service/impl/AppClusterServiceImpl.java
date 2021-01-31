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
package com.wl4g.devops.erm.service.impl;

import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.component.common.serialize.JacksonUtils;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.common.bean.erm.AppCluster;
import com.wl4g.devops.common.bean.erm.AppEnvironment;
import com.wl4g.devops.common.bean.erm.AppInstance;
import com.wl4g.devops.common.bean.erm.DockerRepository;
import com.wl4g.devops.erm.data.AppClusterDao;
import com.wl4g.devops.erm.data.AppEnvironmentDao;
import com.wl4g.devops.erm.data.AppInstanceDao;
import com.wl4g.devops.erm.service.AppClusterService;
import com.wl4g.iam.common.bean.Dict;
import com.wl4g.iam.service.DictService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.component.core.bean.BaseBean.DEL_FLAG_DELETE;
import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCodes;

@Service
public class AppClusterServiceImpl implements AppClusterService {

	protected SmartLogger log = getLogger(getClass());

	@Autowired
	private AppClusterDao appClusterDao;

	@Autowired
	private AppInstanceDao appInstanceDao;

	@Autowired
	private AppEnvironmentDao appEnvironmentDao;

	@Autowired
	private DictService dictService;

	@Override
	public Map<String, Object> list(PageHolder<?> pm, String clusterName, Integer deployType) {
		Map<String, Object> data = new HashMap<>();
		pm.count().startPage();
		// Page<AppCluster> page = PageHelper.startPage(pm.getPageNum(),
		// pm.getPageSize(), true);
		List<AppCluster> list = appClusterDao.list(getRequestOrganizationCodes(), clusterName, deployType);
		pm.setTotal(PageHolder.current().getTotal());

		for (AppCluster appCluster : list) {
			Long count = appInstanceDao.countByClusterId(appCluster.getId());
			appCluster.setInstanceCount(count);
		}
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
			List<Dict> appEnvTypes = dictService.getByType(DICT_APP_ENV_TYPE);
			for (Dict envType : appEnvTypes) {
				AppEnvironment environment = new AppEnvironment();
				environment.preInsert();
				environment.setOrganizationCode(appCluster.getOrganizationCode());
				environment.setClusterId(appCluster.getId());
				environment.setEnvType(envType.getValue());
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

	public void del(Long clusterId) {
		AppCluster appCluster = new AppCluster();
		appCluster.setId(clusterId);
		appCluster.setDelFlag(DEL_FLAG_DELETE);
		appClusterDao.updateByPrimaryKeySelective(appCluster);
	}

	@Override
	public AppCluster detail(Long clusterId) {
		Assert.notNull(clusterId, "clusterId is null");
		AppCluster appCluster = appClusterDao.selectByPrimaryKey(clusterId);

		List<AppEnvironment> environments = appEnvironmentDao.selectByClusterId(clusterId);
		if (CollectionUtils.isEmpty(environments)) {
			environments = new ArrayList<>();
			List<Dict> appEnvTypes = dictService.getByType(DICT_APP_ENV_TYPE);
			for (Dict envType : appEnvTypes) {
				AppEnvironment environment = new AppEnvironment();
				environment.preInsert();
				environment.setOrganizationCode(appCluster.getOrganizationCode());
				environment.setClusterId(appCluster.getId());
				environment.setEnvType(envType.getValue());
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
		return appCluster;
	}

	@Override
	public List<AppInstance> getInstancesByClusterIdAndEnvType(Long clusterId, String envType) {
		Assert.notNull(clusterId, "clusterId is null");
		Assert.notNull(envType, "envType is null");
		return appInstanceDao.selectByClusterIdAndEnvType(clusterId, envType);
	}

	@Override
	public AppEnvironment getAppClusterEnvironment(Long clusterId, String envType) {
		return appEnvironmentDao.selectByClusterIdAndEnv(clusterId, envType);
	}

	public static final String DICT_APP_ENV_TYPE = "app_ns_type";

}