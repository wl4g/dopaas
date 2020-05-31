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

import org.junit.Test;
import org.opencv.core.Mat;

import java.util.List;

public class ResizeUtilsTests extends OpencvBasedTests {

	@Test
	public void testPre() {
		for (int i = 1; i <= 6; i++) {
			String imgPath = "C:/Users/X240/Desktop/opencv/web/p" + i + ".jpg";
			String destPath = "C:/Users/X240/Desktop/opencv/web/web/";
			Mat src = GeneralUtils.matFactory(imgPath);
			src = PreImageUtils.preHandleUtils(src);
			src = BinaryUtils.binaryzation(src);
			List<Mat> result = CutUtils.cutUtils(src);

			for (int j = 0; j < result.size(); j++) {
				Mat dst = ResizeUtils.resize(result.get(j), GeneralUtils.getDsize());
				GeneralUtils.saveImg(dst, destPath + "cut-" + i + "-" + j + ".jpg");
			}
		}

	}

	@Test
	public void test1() {
		String imgPath = "C:/Users/X240/Desktop/resize/1.jpg";
		String destPath = "C:/Users/X240/Desktop/resize/cut/";

		Mat src = GeneralUtils.matFactory(imgPath);

		src = PreImageUtils.preHandleUtils(src);

		src = BinaryUtils.binaryzation(src);

		List<Mat> result = CutUtils.cutUtils(src);

		for (int j = 0; j < result.size(); j++) {
			Mat dst = result.get(j);
			dst = ResizeUtils.resize(dst, GeneralUtils.getDsize());

			// dst = GeneralUtils.erode(dst);

			GeneralUtils.saveImg(dst, destPath + "cut-" + j + ".jpg");
		}

	}

}