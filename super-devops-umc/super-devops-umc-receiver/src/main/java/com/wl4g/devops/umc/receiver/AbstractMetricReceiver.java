package com.wl4g.devops.umc.receiver;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric;
import com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate;
import com.wl4g.devops.umc.alarm.IndicatorsValveAlerter;
import com.wl4g.devops.umc.alarm.MetricAggregateWrapper;
import com.wl4g.devops.umc.alarm.MetricAggregateWrapper.MetricWrapper;
import com.wl4g.devops.umc.store.MetricStore;

/**
 * Abstract metric collect receiver.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public abstract class AbstractMetricReceiver implements MetricReceiver {

	@Autowired
	private IndicatorsValveAlerter indicatorsValveAlerter;

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** metric store adapter. */
	final protected MetricStore store;

	public AbstractMetricReceiver(MetricStore store) {

		super();
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
		wrap.setCollectId(aggregate.getInstance());
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
		indicatorsValveAlerter.alarm(wrap);
	}

}
