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
package com.wl4g.devops.dguid.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Random;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link NetUtils}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年2月10日
 * @since
 */
public class NetUtils {
	public static Logger logger = LoggerFactory.getLogger(NetUtils.class);

	public static final String ANYHOST = "0.0.0.0";

	public static final String LOCALHOST = "127.0.0.1";

	private static final int RND_PORT_START = 30000;

	private static final int RND_PORT_RANGE = 10000;

	private static final Random RANDOM = new Random(System.currentTimeMillis());

	private static final int MAX_PORT = 65535;

	private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

	/**
	 * 获取本地地址
	 */
	public static InetAddress getLocalInetAddress() {
		InetAddress localAddress;
		try {
			localAddress = InetAddress.getLocalHost();
			if (isValidAddress(localAddress)) {
				return localAddress;
			}
		} catch (UnknownHostException e) {
			logger.error("本地地址获取失败: " + e.getMessage());
		}
		return getLocalLanAddress();
	}

	/**
	 * 获取Lan地址
	 */
	public static InetAddress getLocalLanAddress() {
		try {
			// 1、遍历所有的网络接口
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			if (interfaces != null) {
				while (interfaces.hasMoreElements()) {
					NetworkInterface network = interfaces.nextElement();
					if (network.isLoopback()) {
						// 排除loopback类型地址
						continue;
					}
					Enumeration<InetAddress> addresses = network.getInetAddresses();
					if (addresses == null) {
						continue;
					}
					// 2、在所有的接口下再遍历IP
					while (addresses.hasMoreElements()) {
						InetAddress address = addresses.nextElement();
						if (isValidAddress(address)) {
							return address;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("无法确定Lan地址: " + e.getMessage());
		}
		return null;
	}

	/**
	 * 获取机器码
	 */
	public static byte[] getMachineNum() {
		try {
			InetAddress ip = NetUtils.getLocalInetAddress();
			NetworkInterface network = NetworkInterface.getByInetAddress(ip);
			if (network != null) {
				byte[] mac = network.getHardwareAddress();
				if (null != mac) {
					return mac;
				}
			}
		} catch (Exception e) {
			logger.error("机器码获取失败：" + e.getMessage());
		}
		return null;
	}

	/**
	 * 是否有效地址
	 */
	private static boolean isValidAddress(InetAddress address) {
		if (address == null || address.isLoopbackAddress()) {
			return false;
		}
		String name = address.getHostAddress();
		return (name != null && !ANYHOST.equals(name) && !LOCALHOST.equals(name) && IP_PATTERN.matcher(name).matches());
	}

	public static int getRandomPort() {
		return RND_PORT_START + RANDOM.nextInt(RND_PORT_RANGE);
	}

	public static int getAvailablePort() {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket();
			ss.bind(null);
			return ss.getLocalPort();
		} catch (IOException e) {
			return getRandomPort();
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static int getAvailablePort(int port) {
		if (port <= 0) {
			return getAvailablePort();
		}
		for (int i = port; i < MAX_PORT; i++) {
			ServerSocket ss = null;
			try {
				ss = new ServerSocket(i);
				return i;
			} catch (IOException e) {
				// continue
			} finally {
				if (ss != null) {
					try {
						ss.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return port;
	}
}