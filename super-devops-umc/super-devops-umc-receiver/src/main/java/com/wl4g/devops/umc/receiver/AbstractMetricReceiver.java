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

import com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric;
import com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate;
import com.wl4g.devops.umc.alarm.alerting.IndicatorsValveAlerter;
import com.wl4g.devops.umc.alarm.metric.MetricAggregateWrapper;
import com.wl4g.devops.umc.alarm.metric.MetricAggregateWrapper.MetricWrapper;
import com.wl4g.devops.umc.store.MetricStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract metric collect receiver.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public abstract class AbstractMetricReceiver implements MetricReceiver {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** Indicator valve alerter. */
	final protected IndicatorsValveAlerter alerter;

	/** Metric store adapter. */
	final protected MetricStore store;

	public AbstractMetricReceiver(IndicatorsValveAlerter alerter, MetricStore store) {
		Assert.notNull(alerter, "IndicatorsValveAlerter must not be null.");
		Assert.notNull(store, "MetricStore must not be null.");
		this.alerter = alerter;
		this.store = store;
	}

	/**
	 * Puts to metrics aggregate.
	 * 
	 * @param aggregate
	 */
	protected void putMetrics(MetricAggregate aggregate) {
		store.save(aggregate);
	}

	/**
	 * Alarm metrics aggregate.
	 * 
	 * @param aggregate
	 */
	protected void alarm(MetricAggregate aggregate) {
		MetricAggregateWrapper wrap = new MetricAggregateWrapper();
		wrap.setHost(aggregate.getHost());
		wrap.setEndpoint(aggregate.getEndpoint());
		wrap.setTimestamp(aggregate.getTimestamp());
		wrap.setClassify(aggregate.getClassify());

		List<MetricWrapper> metrics = new ArrayList<>();
		for (Metric metric : aggregate.getMetricsList()) {
			MetricWrapper metric1 = new MetricWrapper();
			metric1.setMetric(metric.getMetric());
			metric1.setValue(metric.getValue());
			metric1.setTags(metric.getTagsMap());
			metrics.add(metric1);
		}
		wrap.setMetrics(metrics);

		// Do alarm alerter.
		alerter.alarm(wrap);
	}

}