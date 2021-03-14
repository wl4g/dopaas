/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.paas.umc.opentsdb;

import com.wl4g.paas.common.bean.umc.model.proto.MetricModel;
import com.wl4g.paas.umc.opentsdb.client.OpenTSDBClient;
import com.wl4g.paas.umc.opentsdb.client.bean.request.Point;
import com.wl4g.paas.umc.store.MetricStore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * OpenTSDB metric store.
 * 
 * @author Wangl.sir
 * @author vjay
 * @date 2019-06-20 16:12:00
 */
public class TsdbMetricStore implements MetricStore {

	final private Logger log = LoggerFactory.getLogger(getClass());

	final protected OpenTSDBClient client;

	public TsdbMetricStore(OpenTSDBClient client) {
		this.client = client;
	}

	@Override
	public boolean save(MetricModel.MetricAggregate aggregate) {
		long timestamp = aggregate.getTimestamp();
		int c = 0;
		for (MetricModel.Metric statMetric : aggregate.getMetricsList()) {
			statMetric.getTagsMap();
			if (StringUtils.isBlank(statMetric.getMetric())) {
				continue;
			}
			Point.MetricBuilder pointBuilder = Point.metric(statMetric.getMetric()).value(timestamp, statMetric.getValue());
			Map<String, String> tag = statMetric.getTagsMap();
			for (Map.Entry<String, String> entry : tag.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				pointBuilder.tag(key, value);
			}
			pointBuilder.tag("host", aggregate.getHost());
			pointBuilder.tag("endpoint", aggregate.getEndpoint());
			Point point = pointBuilder.build();

			try {
				client.put(point);
				c++;
			} catch (Exception e) {
				log.error("Failed to storage, caused by: ", e);
			}
		}

		if (log.isInfoEnabled()) {
			log.info("Stored metrics count for - {}", c);
		}
		return true;
	}

}