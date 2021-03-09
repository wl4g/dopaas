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
package com.wl4g.devops.umc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.common.bean.umc.CustomEngine;
import com.wl4g.devops.umc.dao.CustomEngineDao;
import com.wl4g.devops.umc.service.CustomEngineService;
import com.wl4g.devops.umc.timing.EngineTaskScheduler;

/**
 * @author vjay
 * @date 2019-08-09 14:06:00
 */
@Service
public class CustomEngineServiceImpl implements CustomEngineService {

	@Autowired
	private CustomEngineDao customEngineDao;

	@Autowired
	private EngineTaskScheduler engineTaskScheduler;

	@Override
	public PageHolder<CustomEngine> list(PageHolder<CustomEngine> pm, String name) {
		pm.startPage();
		pm.setRecords(customEngineDao.list(name));
		return pm;
	}

	@Override
	public CustomEngine detal(Long id) {
		CustomEngine customEngine = customEngineDao.selectByPrimaryKey(id);
		return customEngine;
	}

	@Override
	public void save(CustomEngine customEngine) {

		if (customEngine.getId() != null) {
			customEngine.preUpdate();
			customEngineDao.updateByPrimaryKeySelective(customEngine);
		} else {
			customEngine.preInsert();
			customEngine.setStatus(1);
			customEngineDao.insertSelective(customEngine);
		}

		engineTaskScheduler.refreshTimingPipeline(customEngine);
	}

	@Override
	public void del(Long id) {
		CustomEngine customEngine = new CustomEngine();
		customEngine.setId(id);
		customEngine.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		customEngine.preUpdate();
		customEngineDao.updateByPrimaryKeySelective(customEngine);

		engineTaskScheduler.stopTimingPipeline(customEngine);
	}

}