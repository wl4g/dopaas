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
package com.wl4g.devops.umc.alarm;

import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.GenericTaskRunner.RunProperties;
import com.wl4g.devops.umc.config.AlarmProperties;
import com.wl4g.devops.umc.notification.AlarmNotifier.SimpleAlarmMessage;
import com.wl4g.devops.umc.notification.CompositeAlarmNotifierAdapter;
import com.wl4g.devops.umc.rule.RuleConfigManager;
import com.wl4g.devops.umc.rule.handler.RuleConfigHandler;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract collection metric valve alerter.
 *
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 */
public abstract class AbstractIndicatorsValveAlerter extends GenericTaskRunner<RunProperties> implements IndicatorsValveAlerter {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected RuleConfigManager ruleConfigManager;

	@Autowired
	protected RuleConfigHandler ruleConfigHandler;

	@Autowired
	protected CompositeAlarmNotifierAdapter notifier;

	public AbstractIndicatorsValveAlerter(AlarmProperties config) {
		super(config);
	}

	@Override
	public void run() {
		// Ignore
	}

	@Override
	public void alarm(MetricAggregateWrapper wrap) {
		getWorker().execute(() -> doAlarmHandling(wrap));
	}

	/**
	 * Do alarm handling.
	 * 
	 * @param aggWrap
	 */
	protected abstract void doAlarmHandling(MetricAggregateWrapper aggWrap);

	/**
	 * Send msg by template , found sent to who by template
	 */
	protected void notification(AlarmTemplate alarmTemplate, List<AlarmConfig> alarmConfigs) {
		for (AlarmConfig alarmConfig : alarmConfigs) {
			if (isBlank(alarmConfig.getAlarmMember())) {
				continue;
			}

			// Alarm notifier members.
			String[] alarmMembers = alarmConfig.getAlarmMember().split(",");
			if (log.isInfoEnabled()) {
				log.info("Alarm notification for templateId: {}, notifierType: {}, notifierTo: {}, content: {}",
						alarmTemplate.getId(), alarmConfig.getAlarmType(), alarmConfig.getAlarmMember(),
						alarmConfig.getAlarmContent());
			}

			// Alarm to multiple notifiers.
			notifier.simpleNotify(
					new SimpleAlarmMessage(alarmConfig.getAlarmContent(), alarmConfig.getAlarmType(), alarmMembers));
		}
	}

}