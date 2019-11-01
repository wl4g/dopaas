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
package com.wl4g.devops.common.bean.umc.model;

import com.wl4g.devops.common.bean.umc.AlarmTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Alarm rule information.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class AlarmRuleInfo {

	/** Metric collect point identity. */
	// private Integer collectId; // e.g. cf_instance.id

	// private Set<Integer> alarmTemplateId = new HashSet<>();

	private List<AlarmTemplate> alarmTemplates = new ArrayList<>();

	/*
	 * public Integer getCollectId() { return collectId; }
	 * 
	 * public void setCollectId(Integer collectId) { this.collectId = collectId;
	 * }
	 */

	public List<AlarmTemplate> getAlarmTemplates() {
		return alarmTemplates;
	}

	public void setAlarmTemplates(List<AlarmTemplate> alarmTemplates) {
		this.alarmTemplates = alarmTemplates;
	}

	/*
	 * public Set<Integer> getAlarmTemplateId() { return alarmTemplateId; }
	 * 
	 * public void setAlarmTemplateId(Set<Integer> alarmTemplateId) {
	 * this.alarmTemplateId = alarmTemplateId; }
	 */

}