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


import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.devops.common.bean.umc.CustomAlarmEvent;
import com.wl4g.devops.umc.data.CustomAlarmEventDao;
import com.wl4g.devops.umc.service.CustomAlarmEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author vjay
 * @date 2019-08-09 14:06:00
 */
@Service
public class CustomAlarmEventServiceImpl implements CustomAlarmEventService {

	@Autowired
	private CustomAlarmEventDao customAlarmEventDao;

	@Override
	public PageHolder<CustomAlarmEvent> list(PageHolder<CustomAlarmEvent> pm, String name) {
		pm.startPage();
		pm.setRecords(customAlarmEventDao.list(name));
		return pm;
	}

	@Override
	public CustomAlarmEvent detal(Long id) {
		CustomAlarmEvent customAlarmEvent = customAlarmEventDao.selectByPrimaryKey(id);
		return customAlarmEvent;
	}

	@Override
	public void save(CustomAlarmEvent customAlarmEvent) {
		if (customAlarmEvent.getId() != null) {
			customAlarmEvent.preUpdate();
			customAlarmEventDao.updateByPrimaryKeySelective(customAlarmEvent);
		} else {
			customAlarmEvent.preInsert();
			customAlarmEventDao.insertSelective(customAlarmEvent);
		}
	}

	@Override
	public void del(Long id) {
		CustomAlarmEvent customAlarmEvent = new CustomAlarmEvent();
		customAlarmEvent.setId(id);
		customAlarmEvent.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		customAlarmEvent.preUpdate();
		customAlarmEventDao.updateByPrimaryKeySelective(customAlarmEvent);
	}

}