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

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.umc.alarm.ServiceRuleConfigurer;
import com.wl4g.devops.umc.handler.AlarmConfigurer;

/**
 * Service alarm auto configuration.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
@Configuration
@ImportAutoConfiguration(UmcAlarmAutoConfiguration.class)
public class ServiceAlarmAutoConfiguration {

	@Bean
	public AlarmConfigurer serviceRuleConfigurer() {
		return new ServiceRuleConfigurer();
	}

}