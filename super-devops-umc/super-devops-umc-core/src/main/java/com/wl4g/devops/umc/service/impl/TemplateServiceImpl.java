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

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.dao.umc.AlarmRuleDao;
import com.wl4g.devops.dao.umc.AlarmTemplateDao;
import com.wl4g.devops.umc.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_DELETE;
import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_NORMAL;
import static com.wl4g.devops.common.bean.BaseBean.ENABLED;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.parseJSON;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author vjay
 * @date 2019-08-06 18:30:00
 */
@Service
public class TemplateServiceImpl implements TemplateService {

	@Autowired
	private AlarmTemplateDao alarmTemplateDao;

	@Autowired
	private AlarmRuleDao alarmRuleDao;

	@Override
	@Transactional
	public void save(AlarmTemplate tpl) {
		Assert.notNull(tpl, "alarmTemplate is null");

		List<Map<String, String>> tagMap = tpl.getTagMap();
		if (!CollectionUtils.isEmpty(tagMap)) {
			tpl.setTags(toJSONString(tagMap));
		}

		if (tpl.getId() != null) {// update
			tpl.preUpdate();
			alarmTemplateDao.updateByPrimaryKeySelective(tpl);
		} else {
			tpl.preInsert();
			tpl.setDelFlag(DEL_FLAG_NORMAL);
			tpl.setEnable(ENABLED);
			alarmTemplateDao.insertSelective(tpl);
		}

		// 找到旧的规则，如果发现新规则列表中没有旧的规则，则删掉缺少的旧规则
		List<AlarmRule> oldAlarmRules = alarmRuleDao.selectByTemplateId(tpl.getId());
		List<AlarmRule> temp = new ArrayList<>();
		if (!CollectionUtils.isEmpty(oldAlarmRules)) {
			for (AlarmRule oldAlarmRule : oldAlarmRules) {
				for (AlarmRule newAlarmRule : tpl.getRules()) {
					if (newAlarmRule.getId() != null && oldAlarmRule.getId().equals(newAlarmRule.getId())) {
						temp.add(oldAlarmRule);
					}
				}
			}
		}
		oldAlarmRules.removeAll(temp);
		for (AlarmRule alarmRule : oldAlarmRules) {
			alarmRule.setDelFlag(DEL_FLAG_DELETE);
			alarmRuleDao.updateByPrimaryKeySelective(alarmRule);
		}

		// rules
		List<AlarmRule> rules = tpl.getRules();
		Assert.notEmpty(rules, "rules is empty");
		for (AlarmRule _rule : rules) {
			if (_rule.getId() != null) {
				_rule.preUpdate();
				_rule.setTemplateId(tpl.getId());
				alarmRuleDao.updateByPrimaryKeySelective(_rule);
			} else {
				_rule.preInsert();
				_rule.setTemplateId(tpl.getId());
				alarmRuleDao.insertSelective(_rule);
			}
		}
	}

	@Override
	public AlarmTemplate detail(Integer id) {
		Assert.notNull(id, "AlarmTemplate id must not be null");
		AlarmTemplate tpl = alarmTemplateDao.selectByPrimaryKey(id);
		Assert.notNull(tpl, "alarmTemplate is null");
		List<AlarmRule> alarmRules = alarmRuleDao.selectByTemplateId(id);
		tpl.setRules(alarmRules);

		if (isNotBlank(tpl.getTags())) {
			List<Map<String, String>> list = parseJSON(tpl.getTags(), new TypeReference<List<Map<String, String>>>() {
			});
			tpl.setTagMap(list);
		}
		return tpl;
	}

	@Override
	public void del(Integer id) {
		Assert.notNull(id, "id is null");
		AlarmTemplate alarmTemplate = new AlarmTemplate();
		alarmTemplate.setId(id);
		alarmTemplate.setDelFlag(DEL_FLAG_DELETE);
		alarmTemplate.preUpdate();
		alarmTemplateDao.updateByPrimaryKeySelective(alarmTemplate);
	}
}