package com.wl4g.devops.tool.opencv.utils;

import org.junit.Test;
import org.opencv.core.Mat;

public class RemoveNOiseUtilsTests extends OpencvBasedTests {

	@Test
	public void testPre() {
		for (int i = 1; i <= 6; i++) {
			String imgPath = "C:/Users/X240/Desktop/opencv/web/binary/b-" + i + ".jpg";
			String destPath = "C:/Users/X240/Desktop/opencv/web/binary/";
			Mat src = GeneralUtils.matFactory(imgPath);
			src = GrayUtils.grayColByAdapThreshold(src);
			src = BinaryUtils.binaryzation(src);
			src = RemoveNoiseUtils.connectedRemoveNoise(src, 50);
			GeneralUtils.saveImg(src, destPath + "noise-" + i + ".jpg");
		}
	}

}
