package com.wl4g.devops.common.bean.umc.model;

/**
 * Metric value information
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月26日
 * @since
 */
public class MetricValue implements Comparable<MetricValue> {

	private long gatherTime;

	private double value;

	public MetricValue() {

	}

	public MetricValue(long gatherTime, double value) {
		this.gatherTime = gatherTime;
		this.value = value;
	}

	public long getGatherTime() {
		return gatherTime;
	}

	public void setGatherTime(long gatherTime) {
		this.gatherTime = gatherTime;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public int compareTo(MetricValue o) {
		if (this.getGatherTime() > (o.getGatherTime())) {
			return 1;
		} else if (this.getGatherTime() < (o.getGatherTime())) {
			return -1;
		}
		return 0;
	}

}
