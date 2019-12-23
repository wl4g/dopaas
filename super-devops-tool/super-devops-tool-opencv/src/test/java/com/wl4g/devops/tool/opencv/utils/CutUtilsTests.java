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
