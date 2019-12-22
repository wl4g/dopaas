package com.wl4g.devops.tool.common.natives;

import static org.apache.commons.lang3.SystemUtils.OS_ARCH;
import static org.apache.commons.lang3.SystemUtils.OS_NAME;

import java.io.IOException;

public class PathPatternNativeLibraryLoaderTests {

	public static void main(String[] args) throws Exception {
		test1();
		test2();
	}

	public static void test1() {
		System.out.println("/a sdf ".split("/").length);
		System.out.println(OS_NAME);
		System.out.println(OS_ARCH);
	}

	public static void test2() throws IOException {
		new PathPatternNativeLibraryLoader("/org/xerial/snappy/native/**/*.*").loadLibrarys();
	}

}
