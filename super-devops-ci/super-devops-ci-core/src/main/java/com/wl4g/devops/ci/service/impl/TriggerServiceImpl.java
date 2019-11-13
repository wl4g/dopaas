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
package com.wl4g.devops.ci.service.impl;

import com.wl4g.devops.ci.service.TriggerService;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.dao.ci.TriggerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author vjay
 * @date 2019-05-17 11:07:00
 */
@Service
public class TriggerServiceImpl implements TriggerService {

	@Autowired
	private TriggerDao triggerDao;

	@Override
	@Transactional
	public Trigger insert(Trigger trigger) {
		trigger.preInsert();
		triggerDao.insertSelective(trigger);
		return trigger;
	}

	@Override
	@Transactional
	public Trigger update(Trigger trigger) {
		trigger.preUpdate();
		triggerDao.updateByPrimaryKeySelective(trigger);
		return trigger;
	}

	@Override
	@Transactional
	public int delete(Integer id) {
		return triggerDao.deleteByPrimaryKey(id);
	}

	@Override
	public void enable(Integer id) {
		Trigger trigger = new Trigger();
		trigger.setId(id);
		trigger.preUpdate();
		trigger.setEnable(BaseBean.ENABLED);
		triggerDao.updateByPrimaryKeySelective(trigger);
	}

	@Override
	public void disable(Integer id) {
		Trigger trigger = new Trigger();
		trigger.setId(id);
		trigger.preUpdate();
		trigger.setEnable(BaseBean.DISABLED);
		triggerDao.updateByPrimaryKeySelective(trigger);
	}

	@Override
	public void updateSha(Integer id, String sha) {
		Trigger trigger = new Trigger();
		trigger.setId(id);
		trigger.setSha(sha);
		triggerDao.updateByPrimaryKeySelective(trigger);
	}

}