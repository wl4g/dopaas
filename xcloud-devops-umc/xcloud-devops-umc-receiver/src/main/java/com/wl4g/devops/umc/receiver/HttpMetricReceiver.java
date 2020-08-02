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
package com.wl4g.devops.umc.receiver;

import com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate;
import com.wl4g.devops.umc.alarm.alerting.IndicatorsValveAlerter;
import com.wl4g.devops.umc.store.MetricStore;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.URI_HTTP_RECEIVER_ENDPOINT;

/**
 * HTTP collection receiver
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
@ResponseBody
@com.wl4g.devops.umc.annotation.HttpCollectReceiver
public class HttpMetricReceiver extends AbstractMetricReceiver {

	public HttpMetricReceiver(IndicatorsValveAlerter alerter, MetricStore store) {
		super(alerter, store);
	}

	/**
	 * Receiving of HTTP metrics message.
	 */
	@RequestMapping(URI_HTTP_RECEIVER_ENDPOINT)
	public void metricReceive(@RequestBody byte[] body) {
		try {
			MetricAggregate aggregate = MetricAggregate.parseFrom(body);
			// Storage metrics.
			putMetrics(aggregate);

			// Metrics alarm.
			alarm(aggregate);
		} catch (Exception e) {
			log.error("Failed to receive metric handling.", e);
		}
	}

}