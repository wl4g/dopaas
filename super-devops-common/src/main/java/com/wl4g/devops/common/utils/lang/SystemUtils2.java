/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.common.utils.lang;

import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.split;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.SystemUtils;

/**
 * System utility tools
 * 
 * @author wangl.sir
 * @version v1.0 2018年5月28日
 * @since
 */
public abstract class SystemUtils2 extends SystemUtils {

	/**
	 * System Unique Identification
	 */
	public static String HOST_SERIAL;

	/**
	 * Unique identity of current application
	 */
	public static String APP_SERIAL;

	/**
	 * Unique Identification of Current Application Processe ID
	 */
	public static String PROCESS_ID;

	/**
	 * Unique Identification of Current Application Processes
	 */
	public static String PROCESS_SERIAL;

	static {
		try {
			HOST_SERIAL = localMacString();
			String packagePath = SystemUtils2.class.getProtectionDomain().getCodeSource().getLocation().toString();
			APP_SERIAL = new StringBuffer(HOST_SERIAL).append("-")
					.append(Hex.encodeHexString((HOST_SERIAL + packagePath).getBytes(Charset.forName("UTF-8")))).substring(16, 26)
					.toString();
			PROCESS_ID = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
			PROCESS_SERIAL = APP_SERIAL + "-" + PROCESS_ID;
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize local device resource information.", e);
		}
	}

	private static String localMacString() throws IOException {
		// Getting MAC Address Information of Network Card
		byte[] mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			// Byte to integer
			int temp = mac[i] & 0xff;
			String str = Integer.toHexString(temp);
			if (str.length() == 1)
				sb.append("0" + str);
			else
				sb.append(str);
		}
		return sb.toString().toLowerCase().replaceAll("-", "");
	}

	/**
	 * System path invalid path cleanup. </br>
	 * WINDOWS:
	 * 
	 * <pre>
	 * cleanSystemPath("E:\\dir\\") == E:\dir
	 * cleanSystemPath("E:\\log\\a.log\\") == E:\log\a.log
	 * </pre>
	 * 
	 * UNIX:
	 * 
	 * <pre>
	 * cleanSystemPath("/var/dir//") == /var/dir
	 * cleanSystemPath("/var/log//a.log/") == /var/log/a.log
	 * </pre>
	 * 
	 * @param systemPath
	 * @return
	 */
	public static String cleanSystemPath(String systemPath) {
		if (isBlank(systemPath) || !contains(systemPath, File.separator)) {
			return systemPath;
		}

		// Clean invalid suffix path separator.
		StringBuffer path = new StringBuffer();
		Iterator<String> it = Arrays.asList(split(systemPath, File.separator)).iterator();
		while (it.hasNext()) {
			String part = it.next();
			if (!isBlank(part)) {
				path.append(part);
				if (it.hasNext()) {
					path.append(File.separator);
				}
			}
		}
		return path.toString();
	}

	public static void main(String[] args) {
		System.out.println(cleanSystemPath("E:\\dir\\"));
		System.out.println(cleanSystemPath("E:\\log\\a.log\\"));
		System.out.println(cleanSystemPath("/var/log//a.log/"));
	}

}