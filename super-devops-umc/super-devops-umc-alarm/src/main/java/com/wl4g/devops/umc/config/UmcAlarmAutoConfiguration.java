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
package com.wl4g.devops.umc.config;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.support.notification.MessageNotifier;
import com.wl4g.devops.support.notification.MessageNotifier.NotifierKind;
import com.wl4g.devops.support.redis.JedisService;
import com.wl4g.devops.umc.alarm.alerting.DefaultIndicatorsValveAlerter;
import com.wl4g.devops.umc.alarm.alerting.IndicatorsValveAlerter;
import com.wl4g.devops.umc.alarm.alerting.SimulateIndicatorsValveAleter;
import com.wl4g.devops.umc.console.AlarmConsole;
import com.wl4g.devops.umc.handler.CheckImpledAlarmConfigurer;
import com.wl4g.devops.umc.handler.AlarmConfigurer;
import com.wl4g.devops.umc.rule.RuleConfigManager;
import com.wl4g.devops.umc.rule.inspect.AvgRuleInspector;
import com.wl4g.devops.umc.rule.inspect.CompositeRuleInspectorAdapter;
import com.wl4g.devops.umc.rule.inspect.LatestRuleInspector;
import com.wl4g.devops.umc.rule.inspect.MaxRuleInspector;
import com.wl4g.devops.umc.rule.inspect.MinRuleInspector;
import com.wl4g.devops.umc.rule.inspect.RuleInspector;
import com.wl4g.devops.umc.rule.inspect.SumRuleInspector;

/**
 * Alarm auto configuration.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class UmcAlarmAutoConfiguration {

	final public static String KEY_ALARM_PREFIX = "spring.cloud.devops.umc.alarm";

	final public static String BEAN_DEFAULT_VALVE_ALERTER = "defaultIndicatorsValveAlerter";

	final public static String BEAN_SIMULATE_VALVE_ALERTER = "simulateIndicatorsValveAlerter";

	@Bean
	@ConfigurationProperties(prefix = KEY_ALARM_PREFIX)
	public AlarmProperties alarmProperties() {
		AlarmProperties alarmProperties = new AlarmProperties();
		alarmProperties.setConcurrency(2);
		return alarmProperties;
	}

	@Bean(BEAN_DEFAULT_VALVE_ALERTER)
	public IndicatorsValveAlerter defaultIndicatorsValveAlerter(JedisService jedisService, JedisLockManager lockManager,
			AlarmProperties config, AlarmConfigurer configurer, RuleConfigManager ruleManager,
			CompositeRuleInspectorAdapter inspector, GenericOperatorAdapter<NotifierKind, MessageNotifier> notifierAdapter) {
		return new DefaultIndicatorsValveAlerter(jedisService, lockManager, config, configurer, ruleManager, inspector,
				notifierAdapter);
	}

	@Bean(BEAN_SIMULATE_VALVE_ALERTER)
	public IndicatorsValveAlerter simulateIndicatorsValveAlerter(JedisService jedisService, JedisLockManager lockManager,
			AlarmProperties config, AlarmConfigurer configurer, RuleConfigManager ruleManager,
			CompositeRuleInspectorAdapter inspector, GenericOperatorAdapter<NotifierKind, MessageNotifier> notifierAdapter) {
		return new SimulateIndicatorsValveAleter(jedisService, lockManager, config, configurer, ruleManager, inspector,
				notifierAdapter);
	}

	@Bean
	@ConditionalOnMissingBean(AlarmConfigurer.class)
	public AlarmConfigurer checkImpledAlarmPropertiesConfigurer() {
		return new CheckImpledAlarmConfigurer();
	}

	@Bean
	public RuleConfigManager ruleConfigManager() {
		return new RuleConfigManager();
	}

	//
	// --- Alarm rule inspector. ---
	//

	@Bean
	public AvgRuleInspector avgRuleInspector() {
		return new AvgRuleInspector();
	}

	@Bean
	public MinRuleInspector minRuleInspector() {
		return new MinRuleInspector();
	}

	@Bean
	public MaxRuleInspector maxRuleInspector() {
		return new MaxRuleInspector();
	}

	@Bean
	public LatestRuleInspector latestRuleInspector() {
		return new LatestRuleInspector();
	}

	@Bean
	public SumRuleInspector sumRuleInspector() {
		return new SumRuleInspector();
	}

	@Bean
	public CompositeRuleInspectorAdapter compositeRuleInspectorAdapter(List<RuleInspector> inspectors) {
		return new CompositeRuleInspectorAdapter(inspectors);
	}

	//
	// --- Alarm console. ---
	//

	@Bean
	public AlarmConsole alarmConsole() {
		return new AlarmConsole();
	}
}