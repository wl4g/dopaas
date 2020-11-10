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

import com.wl4g.devops.common.bean.erm.AppCluster;
import com.wl4g.devops.common.bean.erm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;

import java.util.Date;
import java.util.List;

/**
 * Rule handler.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public interface RuleConfigHandler {

	List<AppInstance> instancelist(AppInstance appInstance);

	AppCluster getAppGroupByName(String groupName);

	List<AlarmTemplate> getAlarmTemplateByCollectId(Integer collectId);

	List<AlarmTemplate> getAlarmTemplateByClusterId(Integer clusterId);

	List<AlarmConfig> getAlarmConfigByCollectIdAndTemplateId(Integer templateId, Integer collectId);

	void saveRecord(AlarmTemplate alarmTemplate, List<AlarmConfig> alarmConfigs, Integer collectId, Long gatherTime, Date nowDate,
			List<AlarmRule> rules);

	List<AlarmConfig> getAlarmConfigByClusterIdAndTemplateId(Integer templateId, Integer clusterId);

}