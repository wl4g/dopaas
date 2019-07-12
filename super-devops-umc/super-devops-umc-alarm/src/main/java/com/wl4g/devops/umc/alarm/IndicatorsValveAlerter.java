package com.wl4g.devops.umc.alarm;

/**
 * Collection metric valve alerter.
 * 
 * @author wangl.sir
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
