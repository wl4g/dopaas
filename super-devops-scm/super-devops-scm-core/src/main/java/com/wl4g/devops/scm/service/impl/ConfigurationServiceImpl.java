/*
 * Copyright 2015 the original author or authors.
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wl4g.devops.dao.scm.AppGroupDao;
import com.wl4g.devops.dao.scm.ConfigurationDao;
import com.wl4g.devops.dao.scm.HistoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.wl4g.devops.common.bean.scm.*;
import com.wl4g.devops.common.bean.scm.model.GetRelease;
import com.wl4g.devops.common.bean.scm.model.PreRelease;
import com.wl4g.devops.common.bean.scm.model.ReportInfo;
import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseInstance;
import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseMeta;
import com.wl4g.devops.scm.context.ConfigContextHandler;
import com.wl4g.devops.scm.service.ConfigurationService;

/**
 * DevOps configuration core service implement.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月6日
 * @since
 */
@Service
@Transactional
public class ConfigurationServiceImpl implements ConfigurationService {

	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private HistoryDao historyDao;
	@Autowired
	private AppGroupDao appGroupDao;
	@Autowired
	private ConfigContextHandler configServerService;

	@Override
	public void configure(VersionOfDetail vd) {
		// 校验历史版本签名，签名相同的实例版本则排除后面相关操作
		List<String> nodeIdList = vd.getNodeIdList();
		if (nodeIdList == null || nodeIdList.isEmpty()) {// 如果实例id列表为空则不进行操作
			return;
		}
		String sign = this.signatureVersionContent(vd);
		List<AppInstance> nodeList = new ArrayList<>();
		for (String nodeId : nodeIdList) {
			AppInstance instance = this.appGroupDao.getAppInstance(nodeId);
			String hisVersionId = instance.getVersionId();
			if (hisVersionId != null) {
				Version version = new Version();
				version.setId(Integer.parseInt(hisVersionId));
				version = this.historyDao.versionselect(version);
				String hisSign = version.getSign();
				if (sign.equals(hisSign)) {
					continue;
				}
			}
			nodeList.add(instance);
		}
		if (nodeList.isEmpty()) {
			return;
		}
		// Save version information.
		vd.preInsert();
		vd.setSign(sign);
		vd.setSigntype("MD5");
		this.configurationDao.insert(vd);
		int versionId = vd.getId();

		// Save version details information.
		if (null != vd.getConfigGurations() && !vd.getConfigGurations().isEmpty()) {
			Map<String, Object> vMap = new HashMap<>();
			vMap.put("vid", vd.getId());
			vMap.put("configGurations", vd.getConfigGurations());
			this.configurationDao.insertDetail(vMap);
		}

		// Save release history information.
		HistoryOfDetail historyOfDetail = new HistoryOfDetail();
		historyOfDetail.setVersionid(versionId);
		historyOfDetail.setRemark(vd.getRemark());
		historyOfDetail.setCreateBy(vd.getCreateBy());
		historyOfDetail.setCreateDate(vd.getCreateDate());
		this.historyDao.insert(historyOfDetail);

		// Define release instance list.
		List<ReleaseInstance> instances = new ArrayList<>();
		for (AppInstance instance : nodeList) {
			// Save release history details information.
			ReleaseDetail releaseDetail = new ReleaseDetail();
			releaseDetail.setReleaseId(historyOfDetail.getId());
			releaseDetail.setResult("暂无结果");
			releaseDetail.setInstanceId(instance.getId());
			this.historyDao.insertDetail(releaseDetail);

			// Update instance/node reference version information.
			Map<String, Object> nMap = new HashMap<>();
			nMap.put("vid", vd.getId());
			nMap.put("nodeid", instance.getId());
			nMap.put("updateBy", vd.getUpdateBy());
			nMap.put("updateDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(vd.getUpdateDate()));
			this.configurationDao.updateNode(nMap);

			// Get application instance information.
			ReleaseInstance releaseInstance = new ReleaseInstance();
			releaseInstance.setHost(instance.getHost());
			releaseInstance.setPort(instance.getPort());
			instances.add(releaseInstance);
		}
		// Get application environment information.
		String envName = this.appGroupDao.selectEnvName(String.valueOf(vd.getEnvId()));
		// Get application group information.
		AppGroup appGroup = this.appGroupDao.getAppGroup(String.valueOf(vd.getGroupId()));

		// Request configuration source send to client.
		//
		PreRelease preRelease = new PreRelease();
		preRelease.setGroup(appGroup.getName());
		preRelease.setProfile(envName);
		ReleaseMeta meta = new ReleaseMeta(String.valueOf(historyOfDetail.getId()), String.valueOf(versionId));
		preRelease.setMeta(meta);
		preRelease.setInstances(instances);
		this.configServerService.release(preRelease);
	}

	@Override
	public boolean update(ConfigVersion cof) {
		cof.preUpdate();
		return configurationDao.update(cof);
	}

	@Override
	public List<ConfigVersionList> list(ConfigVersionList agl) {
		return configurationDao.list(agl);
	}

	@Override
	public boolean deleteGuration(String id) {
		return configurationDao.deleteConfigGuration(id);
	}

	@Override
	public boolean updateGuration(VersionContentBean guration) {
		guration.preUpdate();
		return configurationDao.updateGuration(guration);
	}

	@Override
	public List<VersionContentBean> selectVersion(int id) {
		return configurationDao.selectVersion(id);
	}

	@Override
	public ConfigSourceBean findSource(GetRelease getRelease) {
		return this.configurationDao.findSource(getRelease);
	}

	@Override
	public void updateReleaseDetail(ReportInfo report) {
		this.configurationDao.updateReleaseDetail(report);
	}

	/**
	 * Signature version file content.
	 * 
	 * @return
	 */
	private String signatureVersionContent(VersionOfDetail vd) {
		StringBuffer plain = new StringBuffer();
		if (vd.getConfigGurations() != null) {
			vd.getConfigGurations().forEach(c -> plain.append(c.getContent()));
			// Computational signature
			return Hashing.md5().newHasher(32).putString(plain, Charsets.UTF_8).hash().toString();
		}
		return null;
	}

}