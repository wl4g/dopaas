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
package com.wl4g.devops.umc.alarm.alerting;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.KEY_CACHE_ALARM_METRIC_QUEUE_SIMULATE;

import org.springframework.util.Assert;

import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.support.notification.MessageNotifier;
import com.wl4g.devops.support.notification.MessageNotifier.NotifierKind;
import com.wl4g.devops.support.redis.JedisService;
import com.wl4g.devops.umc.config.AlarmProperties;
import com.wl4g.devops.umc.handler.AlarmConfigurer;
import com.wl4g.devops.umc.rule.RuleConfigManager;
import com.wl4g.devops.umc.rule.inspect.CompositeRuleInspectorAdapter;

/**
 * Simulate indicators valve alerter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-04
 * @since
 */
public class SimulateIndicatorsValveAleter extends DefaultIndicatorsValveAlerter {

	public SimulateIndicatorsValveAleter(JedisService jedisService, JedisLockManager lockManager, AlarmProperties config,
			AlarmConfigurer configurer, RuleConfigManager ruleManager, CompositeRuleInspectorAdapter inspector,
			GenericOperatorAdapter<NotifierKind, MessageNotifier> notifierAdapter) {
		super(jedisService, lockManager, config, configurer, ruleManager, inspector, notifierAdapter);
	}

	@Override
	protected String getTimeWindowQueueCacheKey(String collectAddr) {
		Assert.hasText(collectAddr, "Collect addr must not be empty");
		return KEY_CACHE_ALARM_METRIC_QUEUE_SIMULATE + collectAddr;
	}

}