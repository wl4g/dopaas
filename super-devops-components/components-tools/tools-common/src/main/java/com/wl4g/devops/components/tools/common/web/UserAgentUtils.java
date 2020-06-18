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
package com.wl4g.devops.components.tools.common.web;

import javax.servlet.http.HttpServletRequest;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.BrowserType;
import nl.bitwalker.useragentutils.DeviceType;
import nl.bitwalker.useragentutils.UserAgent;

/**
 * User Agent String Recognition Tool
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月26日
 * @since
 */
public class UserAgentUtils {

	/**
	 * Get the user agent object
	 * 
	 * @param request
	 * @return
	 */
	public static UserAgent getUserAgent(HttpServletRequest request) {
		String uaString = request.getHeader("User-Agent");
		return uaString == null ? null : UserAgent.parseUserAgentString(uaString);
	}

	/**
	 * Get device type
	 * 
	 * @param request
	 * @return
	 */
	public static DeviceType getDeviceType(HttpServletRequest request) {
		UserAgent ua = getUserAgent(request);
		return (ua == null ? null : (ua.getOperatingSystem() == null ? null : ua.getOperatingSystem()).getDeviceType());
	}

	/**
	 * Is it a PC?
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isComputer(HttpServletRequest request) {
		DeviceType dt = getDeviceType(request);
		return dt != null && DeviceType.COMPUTER.equals(dt);
	}

	/**
	 * Is it a cell phone?
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isMobile(HttpServletRequest request) {
		DeviceType dt = getDeviceType(request);
		return dt != null && DeviceType.MOBILE.equals(dt);
	}

	/**
	 * Is it a flat panel?
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isTablet(HttpServletRequest request) {
		DeviceType dt = getDeviceType(request);
		return dt != null && DeviceType.TABLET.equals(dt);
	}

	/**
	 * Are they mobile phones and tablets?
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isMobileOrTablet(HttpServletRequest request) {
		DeviceType dt = getDeviceType(request);
		return (dt != null && (DeviceType.MOBILE.equals(dt) || DeviceType.TABLET.equals(dt)));
	}

	/**
	 * Is it a browser?
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isBrowser(HttpServletRequest request) {
		Browser br = getBrowser(request);
		return (br != null && br.getBrowserType() != null && br.getBrowserType() != BrowserType.UNKNOWN);
	}

	/**
	 * Get the browsing type
	 * 
	 * @param request
	 * @return
	 */
	public static Browser getBrowser(HttpServletRequest request) {
		UserAgent ua = getUserAgent(request);
		return (ua != null && ua.getBrowser() != null && ua.getBrowser() != Browser.UNKNOWN) ? ua.getBrowser() : null;
	}

	/**
	 * Get the browsing type name
	 * 
	 * @param request
	 * @return
	 */
	public static String getBrowserName(HttpServletRequest request) {
		Browser browser = getBrowser(request);
		return browser == null ? null : browser.getName();
	}

	/**
	 * Whether the IE version is less than or equal to IE8
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isLteIE8(HttpServletRequest request) {
		Browser br = getBrowser(request);
		return (br != null
				&& (Browser.IE5.equals(br) || Browser.IE6.equals(br) || Browser.IE7.equals(br) || Browser.IE8.equals(br)));
	}

}