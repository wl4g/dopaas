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
package com.wl4g.devops.umc.rule.handler;

import com.wl4g.components.core.bean.erm.AppCluster;
import com.wl4g.components.core.bean.erm.AppInstance;
import com.wl4g.components.core.bean.umc.AlarmConfig;
import com.wl4g.components.core.bean.umc.AlarmRule;
import com.wl4g.components.core.bean.umc.AlarmTemplate;

import org.springframework.beans.factory.InitializingBean;

import java.util.Date;
import java.util.List;

/**
 * None must implements rule handler.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class MustImpledRuleConfigHandler implements RuleConfigHandler, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		throw new IllegalStateException(String.format("Rule handlers must be implemented '%s'", RuleConfigHandler.class));
	}

	@Override
	public List<AppInstance> instancelist(AppInstance appInstance) {
		throw new UnsupportedOperationException();
	}

	@Override
	public AppCluster getAppGroupByName(String groupName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<AlarmTemplate> getAlarmTemplateByCollectId(Integer collectId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<AlarmTemplate> getAlarmTemplateByClusterId(Integer clusterId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<AlarmConfig> getAlarmConfigByCollectIdAndTemplateId(Integer templateId, Integer collectId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveRecord(AlarmTemplate alarmTemplate, List<AlarmConfig> alarmConfigs, Integer collectId, Long gatherTime,
			Date nowDate, List<AlarmRule> rules) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<AlarmConfig> getAlarmConfigByClusterIdAndTemplateId(Integer templateId, Integer clusterId) {
		throw new UnsupportedOperationException();
	}

}