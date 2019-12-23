package com.wl4g.devops.tool.opencv.utils;

import org.junit.Test;
import org.opencv.core.Mat;

public class PreImageUtilsTests extends OpencvBasedTests {

	@Test
	public void testPre() {
		for (int i = 1; i <= 6; i++) {
			String imgPath = "C:/Users/X240/Desktop/opencv/web/p" + i + ".jpg";
			String destPath = "C:/Users/X240/Desktop/opencv/web/";
			Mat src = GeneralUtils.matFactory(imgPath);
			src = PreImageUtils.preHandleUtils(src);
			GeneralUtils.saveImg(src, destPath + "b-" + i + ".jpg");
		}

	}

}
