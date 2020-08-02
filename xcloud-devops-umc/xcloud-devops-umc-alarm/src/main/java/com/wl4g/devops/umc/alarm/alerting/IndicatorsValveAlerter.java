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

import com.wl4g.devops.umc.alarm.metric.MetricAggregateWrapper;

/**
 * Collection metric valve alerter.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public interface IndicatorsValveAlerter {

	/**
	 * Aggregate data alerts based on metrics.
	 * 
	 * @param wrap
	 */
	void alarm(MetricAggregateWrapper wrap);

}