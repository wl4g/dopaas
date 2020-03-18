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

import java.util.Arrays;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

/**
 * 透视变换工具类 因为我透视变换做的也不是很好，就仅提供一个大概的函数...
 */
public class WarpPerspectiveUtils {

	/**
	 * 透视变换
	 * 
	 * @param src
	 * @param points
	 * @return
	 */
	public static Mat warpPerspective(Mat src, Point[] points) {

		// 点的顺序[左上 ，右上 ，右下 ，左下]
		List<Point> listSrcs = Arrays.asList(points[0], points[1], points[2], points[3]);
		Mat srcPoints = Converters.vector_Point_to_Mat(listSrcs, CvType.CV_32F);

		List<Point> listDsts = Arrays.asList(new Point(0, 0), new Point(src.width(), 0), new Point(src.width(), src.height()),
				new Point(0, src.height()));

		Mat dstPoints = Converters.vector_Point_to_Mat(listDsts, CvType.CV_32F);

		Mat perspectiveMmat = Imgproc.getPerspectiveTransform(srcPoints, dstPoints);

		Mat dst = new Mat();

		Imgproc.warpPerspective(src, dst, perspectiveMmat, src.size(), Imgproc.INTER_AREA, 1, new Scalar(0));

		return dst;

	}

}