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
package com.wl4g.devops.common.logging;

import static org.springframework.boot.logging.LoggingSystem.SYSTEM_PROPERTY;

import org.springframework.boot.context.logging.LoggingApplicationListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.wl4g.devops.common.logging.logback.LogbackLoggingSystem;

/**
 * Enhanced logging system application listener.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年2月24日
 * @since
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class CustomLoggingApplicationListener extends LoggingApplicationListener {

	/**
	 * Automatic setting uses the enhanced spring log system. Refer to the
	 * source code: </br>
	 * {@link org.springframework.boot.logging.LoggingApplicationListener#onApplicationStartingEvent(ApplicationStartingEvent)}
	 * </br>
	 * {@link org.springframework.boot.logging.LoggingSystem#get(ClassLoader)}
	 * </br>
	 * {@link org.springframework.boot.logging.LoggingApplicationListener#onApplicationPreparedEvent(ApplicationPreparedEvent)}
	 * </br>
	 */
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		// Force priority use custom logging system
		System.setProperty(SYSTEM_PROPERTY, LogbackLoggingSystem.class.getName());
		super.onApplicationEvent(event);
	}

}