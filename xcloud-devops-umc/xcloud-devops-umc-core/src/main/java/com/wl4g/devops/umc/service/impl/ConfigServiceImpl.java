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
package com.wl4g.devops.umc.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.core.bean.erm.AppInstance;
import com.wl4g.components.core.bean.umc.AlarmConfig;
import com.wl4g.components.core.bean.umc.AlarmTemplate;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dao.erm.AppInstanceDao;
import com.wl4g.devops.dao.umc.AlarmConfigDao;
import com.wl4g.devops.dao.umc.AlarmTemplateDao;
import com.wl4g.devops.umc.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author vjay
 * @date 2019-08-09 14:06:00
 */
@Service
public class ConfigServiceImpl implements ConfigService {

	@Autowired
	private AlarmConfigDao alarmConfigDao;

	@Autowired
	private AlarmTemplateDao alarmTemplateDao;

	@Autowired
	private AppInstanceDao appInstanceDao;

	@Override
	public PageModel<?> list(PageModel<?> pm, Long templateId, Long contactGroupId) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(alarmConfigDao.list(templateId, contactGroupId));
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
		AppInstance appInstance = appInstanceDao.selectByPrimaryKey(alarmConfig.getCollectId());
		alarmConfig.setGroup(appInstance.getClusterId());
		alarmConfig.setEnvType(appInstance.getEnvType());
		return alarmConfig;
	}

}