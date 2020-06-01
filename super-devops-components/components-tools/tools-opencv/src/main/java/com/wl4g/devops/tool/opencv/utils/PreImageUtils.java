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

import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;

/**
 * 预处理类
 */
public class PreImageUtils {

	/**
	 * 把矫正后的图像切割出来
	 *
	 * @param src
	 *            图像矫正后的Mat矩阵
	 */
	public static Mat cutRect(Mat src, RotatedRect rect) {
		Rect r = rect.boundingRect();
		int extend = r.width > r.height ? r.height / 64 : r.width / 64;
		int x = r.x - extend < 0 ? 0 : r.x - extend;
		int y = r.y - extend < 0 ? 0 : r.y - extend;
		int width = x + r.width + 2 * extend >= src.width() ? src.width() - x : r.width + 2 * extend;
		int height = y + r.height + 2 * extend >= src.height() ? src.height() - y : r.height + 2 * extend;
		Mat temp = new Mat(src, new Rect(x, y, width, height));
		return temp;
	}

	/**
	 * 截取出图像中的有效片段
	 * 
	 * @param src
	 * @return
	 */
	public static Mat cutFragment(Mat src) {
		// 灰度化
		Mat grayMat = GrayUtils.grayColByPartAdapThreshold(src);
		// 获取最大矩形
		RotatedRect maxRotatedRect = ContoursUtils.findMaxRect(GeneralUtils.canny(grayMat));
		// 裁剪有效区域
		Mat dst = cutRect(grayMat, maxRotatedRect);
		return dst;
	}

	/**
	 * 对图像进行预处理 旋转变换
	 * 
	 * @param src
	 */
	public static Mat preHandleUtils(Mat src) {
		return preHandleUtils(src, null);
	}

	/**
	 * 对图像进行预处理 旋转变换
	 * 
	 * @param src
	 */
	public static Mat preHandleUtils(Mat src, String path) {
		// 截取出图像中的有效片段
		src = cutFragment(src);

		/**
		 * 1、还未作透视变换
		 */

		// 保存图像
		if (path != null && !"".equals(path)) {
			GeneralUtils.saveImg(src, path);
		}

		return src;
	}

}