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

import org.junit.Test;
import org.opencv.core.Mat;

public class CutUtilsTests extends OpencvBasedTests {

	@Test
	public void testPre() {
		for (int i = 1; i <= 6; i++) {
			String imgPath = "C:/Users/X240/Desktop/opencv/web/cut/b-" + i + ".jpg";
			String destPath = "C:/Users/X240/Desktop/opencv/web/cut/cut/";

			Mat src = GeneralUtils.matFactory(imgPath);
			// 灰度化
			src = GrayUtils.grayColByAdapThreshold(src);
			// 二值化
			src = BinaryUtils.binaryzation(src);
			List<Mat> list = CutUtils.cutUtils(src);

			for (int j = 0; j < list.size(); j++) {
				GeneralUtils.saveImg(list.get(j), destPath + "cut-" + i + "-" + j + ".jpg");
			}
		}

	}

	@Test
	/**
	 * 测试垂直切割
	 */
	public void testCutY() {
		for (int i = 1; i <= 6; i++) {
			String imgPath = "C:/Users/X240/Desktop/opencv/web/cut/cut-" + i + "-0.jpg";
			String destPath = "C:/Users/X240/Desktop/opencv/web/cut/";
			Mat src = GeneralUtils.matFactory(imgPath);
			// 灰度化
			src = GrayUtils.grayColByAdapThreshold(src);
			// 二值化
			src = BinaryUtils.binaryzation(src);
			GeneralUtils.saveImg(src, destPath + "y" + i + ".jpg");
		}
	}

}