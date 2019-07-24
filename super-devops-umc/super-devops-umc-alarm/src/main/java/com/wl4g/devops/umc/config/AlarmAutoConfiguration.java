package com.wl4g.devops.umc.config;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.wl4g.devops.umc.alarm.DefaultIndicatorsValveAlerter;
import com.wl4g.devops.umc.alarm.IndicatorsValveAlerter;
import com.wl4g.devops.umc.notification.AlarmNotifier;
import com.wl4g.devops.umc.notification.CompositeAlarmNotifierAdapter;
import com.wl4g.devops.umc.notification.bark.BarkNotifier;
import com.wl4g.devops.umc.notification.email.EmailNotifier;
import com.wl4g.devops.umc.notification.sms.SmsNotifier;
import com.wl4g.devops.umc.notification.wechat.WeChatNotifier;
import com.wl4g.devops.umc.rule.RuleConfigManager;
import com.wl4g.devops.umc.rule.handler.MustImpledRuleConfigHandler;
import com.wl4g.devops.umc.rule.handler.RuleConfigHandler;

/**
 * Alarm auto configuration.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class AlarmAutoConfiguration {

	final public static String KEY_ALARM_PREFIX = "spring.cloud.devops.umc.alarm";

	@Bean
	@ConfigurationProperties(prefix = KEY_ALARM_PREFIX)
	public AlarmProperties alarmProperties() {
		AlarmProperties alarmProperties = new AlarmProperties();
		alarmProperties.setConcurrency(2);
		return alarmProperties;
	}

	@Bean
	public IndicatorsValveAlerter indicatorsValveAlerter() {
		return new DefaultIndicatorsValveAlerter(alarmProperties());
	}

	@Bean
	@ConditionalOnMissingBean(RuleConfigHandler.class)
	public RuleConfigHandler ruleConfigHandler() {
		return new MustImpledRuleConfigHandler();
	}

	@Bean
	public RuleConfigManager ruleConfigManager() {
		return new RuleConfigManager();
	}

	//
	// Alarm notifiers.
	//

	@Bean
	public WeChatNotifier weChatNotifier() {
		return new WeChatNotifier();
	}

	@Bean
	public SmsNotifier smsNotifier() {
		return new SmsNotifier();
	}

	@Bean
	public EmailNotifier emailNotifier() {
		return new EmailNotifier();
	}

	@Bean
	public BarkNotifier barkNotifier() {
		return new BarkNotifier();
	}

	@Bean
	public CompositeAlarmNotifierAdapter compositeAlarmNotifierAdapter(List<AlarmNotifier> notifiers) {
		return new CompositeAlarmNotifierAdapter(notifiers);
	}

}
