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
package com.wl4g.devops.umc.handler;

import com.wl4g.devops.common.bean.scm.AppCluster;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * Check required implements rule handler.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class CheckImpledAlarmConfigurer implements AlarmConfigurer, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		throw new IllegalStateException(String.format("Rule handlers must be implemented '%s'", AlarmConfigurer.class));
	}

	@Override
	public List<AppInstance> instancelist(AppInstance appInstance) {
		return null;
	}

	@Override
	public AppCluster getAppGroupByName(String groupName) {
		return null;
	}

	@Override
	public List<AlarmTemplate> getAlarmTemplateByCollectId(Integer collectId) {
		return null;
	}

	@Override
	public List<AlarmTemplate> getAlarmTemplateByClusterId(Integer clusterId) {
		return null;
	}

	@Override
	public List<AlarmConfig> getAlarmConfigByCollectIdAndTemplateId(Integer templateId, String collectId) {
		return null;
	}

	@Override
	public void saveRecord(AlarmTemplate alarmTemplate, List<AlarmConfig> alarmConfigs, String collectId, Long gatherTime,
			List<AlarmRule> rules) {
	}

}