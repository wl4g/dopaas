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
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * 归一化工具类
 */
public class ResizeUtils {
	/**
	 * 把图片归一化到相同的大小
	 *
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static Mat resize(Mat src) {
		return resize(src, new Size(28, 28));
	}

	public static Mat resize(Mat src, Size dsize) {
		try {
			Mat temp = trimImg(src);
			src = temp;
		} catch (Exception e) {
			System.out.println(e);
		}

		src = clearWhite(src);

		Mat dst = new Mat();
		// 区域插值(INTER_AREA):图像放大时类似于线性插值，图像缩小时可以避免波纹出现。
		Imgproc.resize(src, dst, dsize, 0, 0, Imgproc.INTER_AREA);

		// 腐蚀
		dst = GeneralUtils.erode(dst);
		return dst;
	}

	/**
	 * 进一步去除空白 去除四周的空白
	 * 
	 * @param src
	 * @return
	 */
	private static Mat clearWhite(Mat src) {
		int width = GeneralUtils.getImgWidth(src), height = GeneralUtils.getImgHeight(src);
		int up, down, left, right, i, j;
		up = down = left = right = 0;
		boolean b = true;
		// left
		for (i = 0; i < width; i++) {
			if (b) {
				for (j = 0; j < height; j++) {
					if (GeneralUtils.getPixel(src, j, i) != GeneralUtils.getWHITE()) {
						b = false;
					}
				}
			} else {
				break;
			}
			left++;
		}

		// right
		b = true;
		for (i = width - 1; i >= 0; i--) {
			if (b) {
				for (j = 0; j < height; j++) {
					if (GeneralUtils.getPixel(src, j, i) != GeneralUtils.getWHITE()) {
						b = false;
					}
				}
			} else {
				break;
			}
			right++;
		}

		// up
		b = true;
		for (i = 0; i < height; i++) {
			if (b) {
				for (j = 0; j < width; j++) {
					if (GeneralUtils.getPixel(src, i, j) != GeneralUtils.getWHITE()) {
						b = false;
					}
				}
			} else {
				break;
			}
			up++;
		}

		// down
		b = true;
		for (i = height - 1; i >= 0; i--) {
			if (b) {
				for (j = 0; j < width; j++) {
					if (GeneralUtils.getPixel(src, i, j) != GeneralUtils.getWHITE()) {
						b = false;
					}
				}
			} else {
				break;
			}
			down++;
		}

		int w = width - (left + right) / 2;
		int h = height - (up + down) / 2;

		if (w > 0 && h > 0) {
			return new Mat(src, new Rect(left / 2, up / 2, w, h));
		} else {
			return src;
		}

	}

	/**
	 * 聚集目标
	 * 
	 * @param src
	 * @return
	 */
	public static Mat trimImg(Mat src) {
		// 寻找轮廓
		List<MatOfPoint> cons = ContoursUtils.findContours(GeneralUtils.canny(src));

		if (cons.size() <= 0) {
			// 没有寻找到轮廓直接返回
			return src;
		}

		int extend = 50;

		// 最大的矩形
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f(cons.get(cons.size() - 1).toArray());
		RotatedRect maxrect = Imgproc.minAreaRect(matOfPoint2f);
		Rect maxr = maxrect.boundingRect();
		int x = maxr.x;
		int y = maxr.y;
		int width = maxr.width;
		int height = maxr.height;

		for (int i = cons.size() - 2; i >= 0; i--) {
			matOfPoint2f = new MatOfPoint2f(cons.get(i).toArray());
			RotatedRect rect = Imgproc.minAreaRect(matOfPoint2f);
			Rect r = rect.boundingRect();

			boolean b = judgeRect(maxrect, rect);

			if (b == false) {
				break;
			}

			// 整合矩形
			if (r.x < x) {
				x = r.x;
			}
			if (r.y < y) {
				y = r.y;
			}
			if (r.x + r.width > x + width) {
				width = r.x + r.width - x;
			}
			if (r.y + r.height > y + height) {
				height = r.y + r.height - y;
			}

		}

		x = x - extend < 0 ? 0 : x - extend;
		y = y - extend < 0 ? 0 : y - extend;
		width = x + width + 2 * extend > src.width() ? src.width() - x : width + 2 * extend;
		height = y + height + 2 * extend > src.height() ? src.height() - y : height + 2 * extend;

		Mat dst = new Mat(src, new Rect(x, y, width, height));

		return dst;
	}

	// 判断矩形是否应该合并到最大矩形中
	public static boolean judgeRect(RotatedRect maxRect, RotatedRect rect) {
		// 先判断距离，再判断面积
		int dis = countDistance(maxRect, rect);
		if (dis == 0) {
			// 相交，应该合并一起
			return true;
		}

		// //大矩形的面积
		// double maxArea = maxRect.boundingRect().area();
		// 小矩形的面积
		double area = rect.boundingRect().area();

		// 最小矩形面积大于500，矩形之间的距离小于300，就应该合并在一起
		if (area > 500 && dis < 300) {
			return true;
		}

		return false;
	}

	// 判断两个矩形之间的最小距离
	public static int countDistance(RotatedRect maxRect, RotatedRect rect) {
		// 1、先判断矩形是否相交
		boolean b = inRect(maxRect.boundingRect(), rect.boundingRect());

		if (b == true) {
			// 相交，距离为0
			return 0;
		}

		// 2、判断最小距离
		int distence = Integer.MAX_VALUE;
		Point[] points = new Point[4];
		maxRect.points(points);

		Point[] points1 = new Point[4];
		rect.points(points1);

		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < points1.length; j++) {
				int temp = (int) (Math.pow(points[i].x - points1[j].x, 2) + Math.pow(points[i].y - points1[j].y, 2));
				if (distence * distence < temp) {
					distence = (int) Math.sqrt(temp);
				}
			}
		}

		return distence;
	}

	// 判断矩形是否在矩形内部
	public static boolean inRect(Rect maxRect, Rect rect) {
		if (inRect(maxRect, rect.x, rect.y)) {
			return true;
		}
		if (inRect(maxRect, rect.x + rect.width, rect.y)) {
			return true;
		}
		if (inRect(maxRect, rect.x + rect.width, rect.y + rect.height)) {
			return true;
		}
		if (inRect(maxRect, rect.x, rect.y + rect.height)) {
			return true;
		}
		return false;
	}

	// 判断一个点是否在矩形内部
	public static boolean inRect(Rect rect, int x, int y) {
		if (x < rect.x) {
			return false;
		}
		if (y < rect.y) {
			return false;
		}
		if (x > rect.x + rect.width) {
			return false;
		}
		if (y > rect.y + rect.height) {
			return false;
		}
		return true;
	}

}