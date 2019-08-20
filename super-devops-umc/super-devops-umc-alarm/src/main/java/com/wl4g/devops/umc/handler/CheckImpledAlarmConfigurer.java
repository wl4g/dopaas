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

import com.wl4g.devops.common.bean.umc.*;
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
	public List<AlarmConfig> findAlarmConfigByEndpoint(String collectId) {
		return null;
	}

	@Override
	public List<AlarmConfig> findAlarmConfig(Integer templateId, String collectId) {
		return null;
	}

	@Override
	public void saveAlarmRecord(Integer templateId, String collectId, Long gatherTime, List<AlarmRule> rules,Integer notificationId) {
	}

	@Override
	public void saveNotification(AlarmNotification alarmNotification) {

	}

	@Override
	public void updateNotification(AlarmNotification alarmNotification) {

	}

	@Override
	public List<AlarmContact> getContactByGroupIds(List<Integer> groupIds) {
		return null;
	}

}