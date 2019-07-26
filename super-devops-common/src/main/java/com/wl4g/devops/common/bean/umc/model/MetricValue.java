package com.wl4g.devops.common.bean.umc.model;

/**
 * Metric value information
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月26日
 * @since
 */
public class MetricValue implements Comparable<MetricValue> {

	public MetricValue() {

	}

	public MetricValue(long timestamp, double value) {
		this.timestamp = timestamp;
		this.value = value;
	}

	private long timestamp;

	private double value;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timeStamp) {
		this.timestamp = timeStamp;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public int compareTo(MetricValue o) {
		if (this.getTimestamp() > (o.getTimestamp())) {
			return 1;
		} else if (this.getTimestamp() < (o.getTimestamp())) {
			return -1;
		}
		return 0;
	}

}
