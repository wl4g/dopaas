package com.wl4g.devops.umc.receiver;

import com.wl4g.devops.common.bean.umc.model.proto.MetricModel;
import com.wl4g.devops.umc.alarm.IndicatorsValveAlerter;
import com.wl4g.devops.umc.alarm.MetricAggregateWrapper;
import com.wl4g.devops.umc.store.MetricStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract collection receiver
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public abstract class AbstractCollectReceiver implements CollectReceiver {

	@Autowired
	private IndicatorsValveAlerter indicatorsValveAlerter;

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** metric store adapter. */
	final protected MetricStore store;

	public AbstractCollectReceiver(MetricStore store) {

		super();
		this.store = store;

	}

	/**
	 * Puts to metrics aggreate.
	 * 
	 * @param aggregate
	 */
	protected void putMetrics(MetricModel.MetricAggregate aggregate) {
		store.save(aggregate);
	}


	protected void alarm(MetricModel.MetricAggregate aggregate){
		MetricAggregateWrapper wrap = new MetricAggregateWrapper();
		wrap.setCollectId(aggregate.getInstance());
		wrap.setTimeStamp(aggregate.getTimestamp());
		wrap.setClassify(aggregate.getClassify());
		List<MetricAggregateWrapper.Metric> metrics = new ArrayList<>();
		for(MetricModel.Metric metric : aggregate.getMetricsList()){
			MetricAggregateWrapper.Metric metric1 = new MetricAggregateWrapper.Metric();
			metric1.setMetric(metric.getMetric());
			metric1.setValue(metric.getValue());
			metric1.setTags(metric.getTagsMap());
			metrics.add(metric1);
		}
		wrap.setMetrics(metrics);
		indicatorsValveAlerter.alarm(wrap);
	}

}
