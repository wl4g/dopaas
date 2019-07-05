package com.wl4g.devops.umc.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.umc.alarm.ServiceRuleHandler;
import com.wl4g.devops.umc.rule.RuleHandler;

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
	public RuleHandler serviceRuleHandler() {
		return new ServiceRuleHandler();
	}

}
