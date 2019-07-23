/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.bean.ci.TriggerDetail;
import com.wl4g.devops.common.bean.scm.BaseBean;
import com.wl4g.devops.dao.ci.TriggerDao;
import com.wl4g.devops.dao.ci.TriggerDetailDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vjay
 * @date 2019-05-17 11:07:00
 */
@Service
public class TriggerServiceImpl implements TriggerService {

	@Autowired
	private TriggerDao triggerDao;
	@Autowired
	private TriggerDetailDao triggerDetailDao;

	@Override
	@Transactional
	public Trigger insert(Trigger trigger, Integer[] instanceIds) {
		trigger.preInsert();
		int result = triggerDao.insertSelective(trigger);
		int triggerId = trigger.getId();
		Assert.notEmpty(instanceIds,"instance can not be null");
		List<TriggerDetail> triggerDetails = new ArrayList<>();
		for (Integer instanceId : instanceIds) {
			TriggerDetail triggerDetail = new TriggerDetail();
			triggerDetail.setInstanceId(instanceId);
			triggerDetail.preInsert();
			triggerDetail.setTriggerId(triggerId);
			triggerDetailDao.insertSelective(triggerDetail);
			triggerDetails.add(triggerDetail);
		}
		trigger.setTriggerDetails(triggerDetails);

		return trigger;
	}

	@Override
	@Transactional
	public Trigger update(Trigger trigger, Integer[] instanceIds) {
		trigger.preUpdate();
		int result = triggerDao.updateByPrimaryKeySelective(trigger);
		int triggerId = trigger.getId();
		Assert.notEmpty(instanceIds,"instance can not be null");
		List<TriggerDetail> triggerDetails = new ArrayList<>();
		triggerDetailDao.deleteByTriggerId(triggerId);
		for (Integer instanceId : instanceIds) {
			TriggerDetail triggerDetail = new TriggerDetail();
			triggerDetail.setInstanceId(instanceId);
			triggerDetail.preInsert();
			triggerDetail.setTriggerId(triggerId);
			triggerDetailDao.insertSelective(triggerDetail);
			triggerDetails.add(triggerDetail);
		}
		trigger.setTriggerDetails(triggerDetails);
		return trigger;
	}

	@Override
	@Transactional
	public int delete(Integer id) {
		triggerDetailDao.deleteByTriggerId(id);
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
	public void updateSha(Integer id,String sha){
		Trigger trigger = new Trigger();
		trigger.setId(id);
		trigger.setSha(sha);
		triggerDao.updateByPrimaryKeySelective(trigger);
	}


	@Override
	public List<TriggerDetail> getDetailByTriggerId(Integer triggerId) {
		return triggerDetailDao.getDetailByTriggerId(triggerId);
	}

}