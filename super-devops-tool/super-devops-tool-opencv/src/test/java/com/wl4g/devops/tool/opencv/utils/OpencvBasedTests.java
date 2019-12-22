package com.wl4g.devops.tool.opencv.utils;

import com.wl4g.devops.tool.opencv.library.OpenCvNativeLibraryLoader;

public class OpencvBasedTests {

	@Before
	public void initOpencvNativeLibrary() {
		OpenCvNativeLibraryLoader.loadLibrarys();
	}

}
