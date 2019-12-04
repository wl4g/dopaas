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
package com.wl4g.devops.share.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.share.AppCluster;
import com.wl4g.devops.common.bean.share.AppInstance;
import com.wl4g.devops.dao.share.AppClusterDao;
import com.wl4g.devops.dao.share.AppInstanceDao;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.share.service.AppClusterService;
import com.wl4g.devops.tool.common.crypto.AES;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_DELETE;

@Service
@Transactional
public class AppClueterServiceImpl implements AppClusterService {

	@Autowired
	private AppClusterDao appClusterDao;

	@Autowired
	private AppInstanceDao appInstanceDao;

	@Value("${cipher-key}")
	protected String cipherKey;

	@Override
	public Map<String, Object> list(PageModel pm, String clusterName) {
		Map<String, Object> data = new HashMap<>();

		Page<AppCluster> page = PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true);
		List<AppCluster> list = appClusterDao.list(clusterName);
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
		return appClusterDao.list(null);
	}

	@Override
	public void save(AppCluster appCluster) {
		if (appCluster.getId() == null) {
			insert(appCluster, cipherKey);
		} else {
			update(appCluster, cipherKey);
		}
	}

	private void insert(AppCluster appCluster, String cipherKey) {
		appCluster.preInsert();
		appClusterDao.insertSelective(appCluster);
		Integer clusterId = appCluster.getId();
		List<AppInstance> instances = appCluster.getInstances();
		for (AppInstance appInstance : instances) {
			appInstance.preInsert();
			appInstance.setClusterId(clusterId);
			if (StringUtils.isNotBlank(appInstance.getSshKey())) {
				try {
					AES aes = new AES(cipherKey);
					String encrypt = aes.encrypt(appInstance.getSshKey());
					appInstance.setSshKey(encrypt);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			appInstanceDao.insertSelective(appInstance);
		}
	}

	private void update(AppCluster appCluster, String cipherKey) {
		appCluster.preUpdate();
		appClusterDao.updateByPrimaryKeySelective(appCluster);
		List<AppInstance> appInstances = appInstanceDao.selectByClusterId(appCluster.getId());
		List<AppInstance> noDelInstances = new ArrayList<>();
		for (AppInstance appInstance : appCluster.getInstances()) {
			if (StringUtils.isNotBlank(appInstance.getSshKey())) {
				try {
					AES aes = new AES(cipherKey);
					String encrypt = aes.encrypt(appInstance.getSshKey());
					appInstance.setSshKey(encrypt);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (appInstance.getId() == null) {// insert
				appInstance.preInsert();
				appInstance.setClusterId(appCluster.getId());
				appInstanceDao.insertSelective(appInstance);
			} else {// update
				appInstance.preUpdate();
				appInstanceDao.updateByPrimaryKeySelective(appInstance);
			}
			if (appInstance.getId() != null) {
				for (AppInstance instance : appInstances) {// if new data not
															// include old data
															// , remove
					if (instance.getId().intValue() == appInstance.getId().intValue()) {
						noDelInstances.add(instance);
						break;
					}
				}
			}
		}
		appInstances.removeAll(noDelInstances);
		for (AppInstance appInstance : appInstances) {
			appInstance.setDelFlag(DEL_FLAG_DELETE);
			appInstanceDao.updateByPrimaryKeySelective(appInstance);
		}
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
		List<AppInstance> appInstances = appInstanceDao.selectByClusterId(clusterId);
		for (AppInstance appInstance : appInstances) {
			if (StringUtils.isNotBlank(appInstance.getSshKey())) {
				try {
					char[] sshkeyPlain = new AES(cipherKey).decrypt(appInstance.getSshKey()).toCharArray();
					appInstance.setSshKey(String.valueOf(sshkeyPlain));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		appCluster.setInstances(appInstances);
		return appCluster;
	}

	@Override
	public List<AppInstance> getInstancesByClusterIdAndEnvType(Integer clusterId, String envType) {
		Assert.notNull(clusterId, "clusterId is null");
		Assert.notNull(envType, "envType is null");
		return appInstanceDao.selectByClusterIdAndEnvType(clusterId, envType);
	}

}