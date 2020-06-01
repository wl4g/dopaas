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
package com.wl4g.devops.tool.opencv.utils;

import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * 画图工具类
 */
public class PaintUtils {

	/**
	 *
	 * 画出所有的矩形
	 * 
	 * @param src
	 * @return
	 */
	public static Mat paintCon(Mat src) {
		Mat cannyMat = GeneralUtils.canny(src);
		List<MatOfPoint> contours = ContoursUtils.findContours(cannyMat);

		Mat rectMat = src.clone();
		Scalar scalar = new Scalar(0, 0, 255);
		for (int i = contours.size() - 1; i >= 0; i--) {
			MatOfPoint matOfPoint = contours.get(i);
			MatOfPoint2f matOfPoint2f = new MatOfPoint2f(matOfPoint.toArray());

			RotatedRect rect = Imgproc.minAreaRect(matOfPoint2f);

			Rect r = rect.boundingRect();

			System.out.println(r.area() + " --- " + i);

			rectMat = paintRect(rectMat, r, scalar);

		}

		return rectMat;
	}

	/**
	 *
	 * 画出最大的矩形
	 * 
	 * @param src
	 * @return
	 */
	public static Mat paintMaxCon(Mat src) {
		Mat cannyMat = GeneralUtils.canny(src);

		RotatedRect rect = ContoursUtils.findMaxRect(cannyMat);

		Rect r = rect.boundingRect();

		Mat rectMat = src.clone();
		Scalar scalar = new Scalar(0, 0, 255);

		rectMat = paintRect(rectMat, r, scalar);

		return rectMat;
	}

	/**
	 * 画矩形
	 *
	 * @param src
	 * @param r
	 * @param scalar
	 * @return
	 */
	public static Mat paintRect(Mat src, Rect r, Scalar scalar) {
		Point pt1 = new Point(r.x, r.y);
		Point pt2 = new Point(r.x + r.width, r.y);
		Point pt3 = new Point(r.x + r.width, r.y + r.height);
		Point pt4 = new Point(r.x, r.y + r.height);

		Imgproc.line(src, pt1, pt2, scalar, 5);
		Imgproc.line(src, pt2, pt3, scalar, 5);
		Imgproc.line(src, pt3, pt4, scalar, 5);
		Imgproc.line(src, pt4, pt1, scalar, 5);

		return src;
	}

	/**
	 * 画实心圆
	 *
	 * @param src
	 * @param point
	 *            点
	 * @param size
	 *            点的尺寸
	 * @param scalar
	 *            颜色
	 * @param path
	 *            保存路径
	 */
	public static Mat paintCircle(Mat src, Point[] point, int size, Scalar scalar, String path) {
		if (src == null || point == null) {
			throw new RuntimeException("Mat 或者 Point 数组不能为NULL");
		}
		for (Point p : point) {
			Imgproc.circle(src, p, size, scalar, -1);
		}

		if (path != null && !"".equals(path)) {
			GeneralUtils.saveImg(src, path);
		}

		return src;
	}

	/**
	 * 画实心圆
	 *
	 * @param src
	 * @param point
	 *            点
	 * @param size
	 *            点的尺寸
	 * @param scalar
	 *            颜色
	 */
	public static Mat paintCircle(Mat src, Point[] point, int size, Scalar scalar) {
		if (src == null || point == null) {
			throw new RuntimeException("Mat 或者 Point 数组不能为NULL");
		}
		return paintCircle(src, point, size, scalar, null);
	}

}