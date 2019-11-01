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

import java.util.List;

/**
 * @author vjay
 * @date 2019-07-05 10:53:00
 */
public class TemplateHisInfo {

	private List<Point> points;

	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public static class Point implements Comparable<Point> {

		public Point() {

		}

		public Point(long timeStamp, double value) {
			this.timeStamp = timeStamp;
			this.value = value;
		}

		private long timeStamp;

		private double value;

		public long getTimeStamp() {
			return timeStamp;
		}

		public void setTimeStamp(long timeStamp) {
			this.timeStamp = timeStamp;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		@Override
		public int compareTo(Point arg0) {
			if (this.getTimeStamp() > (arg0.getTimeStamp())) {
				return 1;
			} else if (this.getTimeStamp() < (arg0.getTimeStamp())) {
				return -1;
			}
			return 0; // 这里定义你排序的规则。
		}
	}

}