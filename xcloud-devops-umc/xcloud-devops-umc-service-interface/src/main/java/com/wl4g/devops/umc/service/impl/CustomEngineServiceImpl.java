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

import com.github.pagehelper.PageHelper;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.common.bean.umc.CustomEngine;
import com.wl4g.devops.dao.umc.CustomEngineDao;
import com.wl4g.devops.umc.service.CustomEngineService;
import com.wl4g.devops.umc.timing.EngineTaskScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public PageModel<CustomEngine> list(PageModel<CustomEngine> pm, String name) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
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