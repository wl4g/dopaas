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
package com.wl4g.devops.tool.common.lang;

import org.apache.commons.lang3.SystemUtils;

import com.wl4g.devops.tool.common.log.SmartLogger;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.hash.Hashing.md5;
import static com.wl4g.devops.tool.common.lang.Assert2.hasText;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.net.NetworkInterface.getNetworkInterfaces;
import static java.util.Collections.list;
import static java.util.Collections.sort;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * System utility tools
 * 
 * @author wangl.sir
 * @version v1.0 2018年5月28日
 * @since
 */
public abstract class SystemUtils2 extends SystemUtils {

	final private static SmartLogger log = getLogger(SystemUtils2.class);

	// --- OS platform info. ---

	/**
	 * Current operating system is Android.
	 */
	final public static boolean IS_ANDRIOD = isAndroid0();

	/**
	 * Check whether the current operating system is Android.
	 */
	private static boolean isAndroid0() {
		return System.getProperty("java.runtime.name", "").toLowerCase().contains("android");
	}

	// --- Runtime system info. ---

	/**
	 * Global unique host hardware MAC identification.
	 */
	final public static String DEFAULT_GLOBAL_HOST_SERIAL = defaultGlobalHostSerial0();

	/**
	 * Global unique identity of current application.
	 */
	final public static String GLOBAL_APP_SERIAL = globalAppSerial0();

	/**
	 * Local current application process ID
	 */
	final public static String LOCAL_PROCESS_ID = localProcessId0();

	/**
	 * Global unique identification of current application process
	 */
	final public static String GLOBAL_PROCESS_SERIAL = globalProcessSerial0();

	/**
	 * (Default)Obtain local host hardware MAC address identity.
	 * 
	 * @return
	 */
	private static String defaultGlobalHostSerial0() {
		// Gets MAC address information of network card
		try {
			List<NetworkInterface> nis = list(getNetworkInterfaces());
			// Ascii dict sort.
			sort(nis, (o1, o2) -> o1.getName().compareTo(o2.getName()));
			byte[] mac = null;
			for (NetworkInterface ni : nis) {
				if (nonNull(ni) && !ni.isLoopback() && ni.isUp()) {
					mac = ni.getHardwareAddress();
					if (nonNull(mac)) {
						break;
					}
				}
			}
			if (isNull(mac)) { // No configured network?
				log.error("=>> Failed to get network card info. the OS not configured network or connected to the network?");
				return "Unknown Network";
			}

			StringBuffer sb = new StringBuffer(32);
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
		} catch (SocketException e) {
			throw new IllegalStateException(e);
		}

	}

	/**
	 * Obtain global application identity.
	 * 
	 * @return
	 */
	private static String globalAppSerial0() {
		hasText(DEFAULT_GLOBAL_HOST_SERIAL, "HostSerial is empty.");
		String packagePath = SystemUtils2.class.getProtectionDomain().getCodeSource().getLocation().toString();
		return md5().hashString(DEFAULT_GLOBAL_HOST_SERIAL + packagePath, UTF_8).toString();
	}

	/**
	 * Obtain local application processId.
	 * 
	 * @return
	 */
	private static String localProcessId0() {
		return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	}

	/**
	 * Obtain global application identity.
	 * 
	 * @return
	 */
	private static String globalProcessSerial0() {
		hasText(GLOBAL_APP_SERIAL, "LocalAppSerial is empty.");
		hasText(LOCAL_PROCESS_ID, "LocalProcessId is empty.");
		return GLOBAL_APP_SERIAL + String.format("%05d", Integer.parseInt(LOCAL_PROCESS_ID));
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

}