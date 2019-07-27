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
