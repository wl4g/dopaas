package com.wl4g.devops.tool.common.natives;

import static org.apache.commons.lang3.SystemUtils.OS_ARCH;
import static org.apache.commons.lang3.SystemUtils.OS_NAME;

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

	public static void test2() throws Exception {
		new PathPatternNativeLibraryLoader().loadLibrarys("/org/xerial/snappy/native/**/*.*");
		System.out.println("Demo execution waiting... Observe whether temporary files will be cleared when exiting");
		Thread.sleep(3000L);
	}

}
