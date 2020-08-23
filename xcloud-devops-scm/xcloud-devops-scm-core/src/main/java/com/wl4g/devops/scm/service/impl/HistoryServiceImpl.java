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
package com.wl4g.devops.scm.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.components.core.bean.erm.AppCluster;
import com.wl4g.components.core.bean.erm.AppInstance;
import com.wl4g.components.core.bean.iam.Dict;
import com.wl4g.devops.scm.bean.*;
import com.wl4g.devops.scm.common.model.*;
import com.wl4g.devops.scm.common.model.GenericConfigInfo.ConfigMeta;
import com.wl4g.devops.scm.common.model.GenericConfigInfo.ConfigNode;
import com.wl4g.devops.dao.erm.AppClusterDao;
import com.wl4g.devops.dao.erm.AppInstanceDao;
import com.wl4g.devops.dao.iam.DictDao;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.scm.dao.ConfigurationDao;
import com.wl4g.devops.scm.dao.HistoryDao;
import com.wl4g.devops.scm.handler.CentralConfigServerHandler;
import com.wl4g.devops.scm.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HistoryServiceImpl implements HistoryService {

	@Autowired
	private HistoryDao historyDao;
	@Autowired
	private ConfigurationDao configGurationDao;
	@Autowired
	private AppClusterDao appClusterDao;
	@Autowired
	private CentralConfigServerHandler configServerService;
	@Autowired
	private DictDao dictDao;
	@Autowired
	private AppInstanceDao appInstanceDao;

	@Override
	public void insert(HistoryOfDetail historyOfDetail) {
		historyOfDetail.preInsert();
		historyDao.insert(historyOfDetail);
	}

	@Override
	public void insertDetail(ReleaseDetail detail) {
		detail.preInsert();
		historyDao.insertDetail(detail);
	}

	@Override
	public boolean delete(ReleaseHistory history) {
		history.preUpdate();
		return historyDao.delete(history);
	}

	public boolean versionDelete(Version history) {
		history.preUpdate();
		return historyDao.versionDelete(history);
	}

	public boolean versionUpdate(Version history) {
		history.preUpdate();
		return historyDao.versionUpdate(history);
	}

	@Override
	public List<ReleaseHistory> select(String of_id, String of_type, String updateDate, String createDate, int status) {
		return historyDao.select(of_id, of_type, updateDate, createDate, status);
	}

	@Override
	public List<ConfigVersionList> list(ConfigVersionList agl) {
		return historyDao.list(agl);
	}

	public PageModel versionList(PageModel pm,Map<String, Object> param) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(historyDao.versionList(param));
		return pm;
	}

	@Override
	public List<ReleaseHistoryList> historylist(ReleaseHistoryList agl) {
		return historyDao.historylist(agl);
	}

	@Override
	public boolean updateHistory(ReleaseDetail detail) {
		detail.preUpdate();
		return historyDao.updateHistory(detail);
	}

	@Override
	public boolean insertReleDetail(ReleaseDetail detail) {
		detail.preInsert();
		return historyDao.insertReleDetail(detail);
	}

	@Override
	public ReleaseDetail reledetailselect(ReleaseDetail releaseDetail) {
		return historyDao.reledetailselect(releaseDetail);
	}

	@Override
	public void releaseRollback(ConfigVersionList agl) {
		agl.preInsert();
		// 添加一条历史版本
		HistoryOfDetail historyOfDetail = new HistoryOfDetail();
		Version version = new Version();
		version.setId(agl.getId());
		Version versionselect = historyDao.versionselect(version);
		if (versionselect == null) {
			throw new RuntimeException("版本不存在");
		} else if (versionselect.getDelFlag() == Version.DEL_FLAG_DELETE) {
			throw new RuntimeException("版本已删除");
		}
		historyOfDetail.preInsert();
		historyOfDetail.setVersionid(agl.getId());
		historyOfDetail.setCreateDate(agl.getCreateDate());
		historyOfDetail.setCreateBy(agl.getCreateBy());
		historyOfDetail.setRemark(agl.getRemark());
		historyOfDetail.setType(HistoryOfDetail.type.ROLLBACK.getValue());
		historyDao.insert(historyOfDetail);
		ReleaseDetail releaseDetail = new ReleaseDetail();
		releaseDetail.preInsert();
		releaseDetail.setReleaseId(historyOfDetail.getId());
		releaseDetail.setInstanceId(Integer.parseInt(agl.getInstanceId()));
		releaseDetail.setResult("暂无结果");
		historyDao.insertDetail(releaseDetail);
		Map<String, Object> nMap = new HashMap<>();
		nMap.put("vid", agl.getId());
		nMap.put("nodeid", agl.getInstanceId());
		nMap.put("updateBy", agl.getUpdateBy());
		nMap.put("updateDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(agl.getUpdateDate()));
		configGurationDao.updateNode(nMap);

		// Get application group information.
		AppCluster appCluster = this.appClusterDao.selectByPrimaryKey(agl.getAppClusterId());

		// Get application nodeList information
		List<AppInstance> nodeList = appInstanceDao.selectByClusterIdAndEnvType(agl.getAppClusterId(), agl.getEnvType());
		// Define release instance list.
		List<ConfigNode> instances = new ArrayList<>();
		for (AppInstance instance : nodeList) {
			// Get application instance information.
			ConfigNode releaseInstance = new ConfigNode();
			releaseInstance.setHost(instance.getHostname());
			releaseInstance.setEndpoint(instance.getEndpoint());
			instances.add(releaseInstance);
		}

		List<VersionContentBean> versionContentBeans = configGurationDao.selectVersion(agl.getId());
		List<String> namespaces = new ArrayList<>();
		for (VersionContentBean versionContentBean : versionContentBeans) {
			Dict dict = dictDao.selectByPrimaryKey(versionContentBean.getNamespaceId());
			String namespace = dict.getValue();
			namespace = "application-" + namespace + ".yml";
			namespaces.add(namespace);
		}

		PreFetchCommand preRelease = new PreFetchCommand();
		preRelease.setCluster(appCluster.getName());
		preRelease.setProfiles(namespaces);
		String releaseId = String.valueOf(historyOfDetail.getId());
		String versionId = String.valueOf(agl.getId());
		ConfigMeta meta = new ConfigMeta(releaseId, versionId);
		preRelease.setMeta(meta);
		preRelease.setNodes(instances);
		this.configServerService.release(preRelease);
	}

}