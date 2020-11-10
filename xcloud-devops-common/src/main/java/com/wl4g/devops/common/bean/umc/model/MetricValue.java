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