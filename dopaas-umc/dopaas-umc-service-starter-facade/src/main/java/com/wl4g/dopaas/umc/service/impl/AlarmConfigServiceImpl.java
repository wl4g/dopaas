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
package com.wl4g.dopaas.umc.service.impl;

import com.wl4g.iam.common.bean.ContactGroup;
import com.wl4g.iam.service.ContactGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.wl4g.infra.core.bean.BaseBean;
import com.wl4g.infra.core.page.PageHolder;
import com.wl4g.dopaas.cmdb.service.AppInstanceService;
import com.wl4g.dopaas.common.bean.cmdb.AppInstance;
import com.wl4g.dopaas.common.bean.umc.AlarmConfig;
import com.wl4g.dopaas.common.bean.umc.AlarmTemplate;
import com.wl4g.dopaas.umc.data.AlarmConfigDao;
import com.wl4g.dopaas.umc.data.AlarmTemplateDao;
import com.wl4g.dopaas.umc.service.AlarmConfigService;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-09 14:06:00
 */
@Service
public class AlarmConfigServiceImpl implements AlarmConfigService {

	private @Autowired AlarmConfigDao alarmConfigDao;
	private @Autowired AlarmTemplateDao alarmTemplateDao;
	private @Autowired AppInstanceService appInstanceService;
	private @Autowired ContactGroupService contactGroupService;

	@Override
	public PageHolder<AlarmConfig> list(PageHolder<AlarmConfig> pm, Long templateId, Long contactGroupId) {
		pm.bind();
		List<AlarmConfig> list = alarmConfigDao.list(templateId, contactGroupId);
		for(AlarmConfig alarmConfig : list){
			ContactGroup contactGroup = contactGroupService.getById(alarmConfig.getContactGroupId());
			alarmConfig.setContactGroupName(contactGroup.getName());
		}
		pm.setRecords(list);
		return pm;
	}

	@Override
	public void save(AlarmConfig alarmConfig) {
		if (alarmConfig.getId() != null) {
			alarmConfig.preUpdate();
			alarmConfigDao.updateByPrimaryKeySelective(alarmConfig);
		} else {
			alarmConfig.preInsert();
			alarmConfigDao.insertSelective(alarmConfig);
		}
	}

	@Override
	public void del(Long id) {
		AlarmConfig alarmConfig = new AlarmConfig();
		alarmConfig.setId(id);
		alarmConfig.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		alarmConfig.preUpdate();
		alarmConfigDao.updateByPrimaryKeySelective(alarmConfig);
	}

	@Override
	public AlarmConfig detail(Long id) {
		Assert.notNull(id, "id is null");
		AlarmConfig alarmConfig = alarmConfigDao.selectByPrimaryKey(id);
		Assert.notNull(alarmConfig, "not found alarmConfig");
		AlarmTemplate alarmTemplate = alarmTemplateDao.selectByPrimaryKey(alarmConfig.getTemplateId());
		Assert.notNull(alarmTemplate, "not found alarmTemplate");
		alarmConfig.setClassify(alarmTemplate.getClassify());
		AppInstance appInstance = appInstanceService.detail(alarmConfig.getCollectId());
		alarmConfig.setGroup(appInstance.getClusterId());
		alarmConfig.setEnvType(appInstance.getEnvType());
		return alarmConfig;
	}

}