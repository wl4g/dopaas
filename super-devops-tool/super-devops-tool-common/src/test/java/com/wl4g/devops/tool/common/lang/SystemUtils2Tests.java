package com.wl4g.devops.tool.common.lang;

public class SystemUtils2Tests {

	public static void main(String[] args) {
		System.out.println(SystemUtils2.DEFAULT_GLOBAL_HOST_SERIAL);
		System.out.println(SystemUtils2.GLOBAL_APP_SERIAL);
		System.out.println(SystemUtils2.LOCAL_PROCESS_ID);
		System.out.println(SystemUtils2.GLOBAL_PROCESS_SERIAL);
		System.out.println(SystemUtils2.cleanSystemPath("E:\\dir\\"));
		System.out.println(SystemUtils2.cleanSystemPath("E:\\log\\a.log\\"));
		System.out.println(SystemUtils2.cleanSystemPath("/var/log//a.log/"));
	}

}
