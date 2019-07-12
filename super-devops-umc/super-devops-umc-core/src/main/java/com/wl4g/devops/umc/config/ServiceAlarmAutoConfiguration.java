package com.wl4g.devops.umc.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.umc.alarm.ServiceRuleConfigHandler;
import com.wl4g.devops.umc.rule.handler.RuleConfigHandler;

/**
 * Service alarm auto configuration.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
@Configuration
@ImportAutoConfiguration(AlarmAutoConfiguration.class)
public class ServiceAlarmAutoConfiguration {

	@Bean
	public RuleConfigHandler serviceRuleHandler() {
		return new ServiceRuleConfigHandler();
	}

}
