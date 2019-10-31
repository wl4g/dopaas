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

import com.wl4g.devops.common.bean.umc.AlarmContactGroup;
import com.wl4g.devops.dao.umc.AlarmContactGroupDao;
import com.wl4g.devops.share.service.ContactGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_NORMAL;
import static com.wl4g.devops.common.bean.BaseBean.ENABLED;

/**
 * @author vjay
 * @date 2019-08-05 18:16:00
 */
@Service
public class ContactGroupServiceImpl implements ContactGroupService {

	@Autowired
	private AlarmContactGroupDao alarmContactGroupDao;

	@Override
	public void save(AlarmContactGroup alarmContactGroup) {
		Assert.notNull(alarmContactGroup, "alarmContactGroup is null");
		if (alarmContactGroup.getId() != null) {
			alarmContactGroup.preUpdate();
			alarmContactGroupDao.updateByPrimaryKeySelective(alarmContactGroup);
		} else {
			alarmContactGroup.preInsert();
			alarmContactGroup.setDelFlag(DEL_FLAG_NORMAL);
			alarmContactGroup.setEnable(ENABLED);
			alarmContactGroupDao.insertSelective(alarmContactGroup);
		}
	}

	@Override
	public void del(Integer id) {
		AlarmContactGroup alarmContactGroup = new AlarmContactGroup();
		alarmContactGroup.preUpdate();
		alarmContactGroup.setId(id);
		alarmContactGroup.setDelFlag(1);
		alarmContactGroupDao.updateByPrimaryKeySelective(alarmContactGroup);
	}
}