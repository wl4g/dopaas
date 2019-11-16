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

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.ci.pipeline.timing.TimingPipelineManager;
import com.wl4g.devops.ci.service.TriggerService;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.utils.lang.DateUtils;
import com.wl4g.devops.dao.ci.TriggerDao;
import com.wl4g.devops.page.PageModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_NORMAL;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_TYPE_TIMMING;

/**
 * @author vjay
 * @date 2019-05-17 11:07:00
 */
@Service
public class TriggerServiceImpl implements TriggerService {

	@Autowired
	private TriggerDao triggerDao;

	@Autowired
	private TimingPipelineManager timingManager;

	@Override
	public PageModel list(PageModel pm, Integer id, String name, Integer taskId, Integer enable, String startDate,
			String endDate) {
		String endDateStr = null;
		if (StringUtils.isNotBlank(endDate)) {
			endDateStr = DateUtils.formatDate(DateUtils.addDays(DateUtils.parseDate(endDate), 1));
		}
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(triggerDao.list(id, name, taskId, enable, startDate, endDateStr));
		return pm;
	}

	@Override
	public void save(Trigger trigger) {
		checkTriggerCron(trigger);
		if (null != trigger.getId() && trigger.getId() > 0) {
			trigger.preUpdate();
			trigger = update(trigger);
		} else {
			trigger.preInsert();
			trigger.setDelFlag(DEL_FLAG_NORMAL);
			trigger = insert(trigger);
		}
		if (trigger.getType() != null && trigger.getType() == TASK_TYPE_TIMMING) {
			restart(trigger.getId());
		}
	}

	@Transactional
	public Trigger insert(Trigger trigger) {
		trigger.preInsert();
		triggerDao.insertSelective(trigger);
		return trigger;
	}

	@Transactional
	public Trigger update(Trigger trigger) {
		trigger.preUpdate();
		triggerDao.updateByPrimaryKeySelective(trigger);
		return trigger;
	}

	@Override
	@Transactional
	public int delete(Integer id) {
		timingManager.stopPipeline(id.toString());
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

	@Override
	public Trigger getById(Integer id) {
		Assert.notNull(id, "id can not be null");
		return triggerDao.selectByPrimaryKey(id);
	}

	/**
	 * Check form
	 *
	 * @param trigger
	 */
	private void checkTriggerCron(Trigger trigger) {
		Assert.notNull(trigger, "trigger can not be null");
		Assert.notNull(trigger.getType(), "type can not be null");
		Assert.notNull(trigger.getAppClusterId(), "project can not be null");
		if (trigger.getType() == TASK_TYPE_TIMMING) {
			Assert.notNull(trigger.getCron(), "cron can not be null");
		}
	}

	/**
	 * Restart Cron -- when modify or create the timing task , restart the cron
	 *
	 * @param triggerId
	 */
	private void restart(Integer triggerId) {
		Trigger trigger = triggerDao.selectByPrimaryKey(triggerId);
		timingManager.refreshPipeline(trigger.getId().toString(), trigger.getCron(), trigger);
	}

}