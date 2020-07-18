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
package com.wl4g.devops.coss.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wl4g.devops.coss.common.exception.ServiceException;
import com.wl4g.devops.coss.common.internal.define.COSSResultCode;

/**
 * {@link LogUtils}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年6月29日 v1.0.0
 * @see
 */
public class LogUtils {

	private static final Log log = LogFactory.getLog("com.wl4g.devops.coss");

	public static Log getLog() {
		return log;
	}

	public static <ExType> void logException(String messagePrefix, ExType ex) {
		logException(messagePrefix, ex, true);
	}

	public static <ExType> void logException(String messagePrefix, ExType ex, boolean logEnabled) {
		assert (ex instanceof Exception);

		String detailMessage = messagePrefix + ((Exception) ex).getMessage();
		if (ex instanceof ServiceException && errorCodeFilterList.contains(((ServiceException) ex).getErrorCode())) {
			if (logEnabled) {
				log.debug(detailMessage);
			}
		} else {
			if (logEnabled) {
				log.warn(detailMessage);
			}
		}
	}

	/**
	 * Set logger level to INFO specially if reponse error code is 404 in order
	 * to prevent from dumping a flood of logs when trying access to
	 * none-existent resources.
	 */
	private static List<String> errorCodeFilterList = new ArrayList<>();

	static {
		errorCodeFilterList.add(COSSResultCode.NO_SUCH_BUCKET);
		errorCodeFilterList.add(COSSResultCode.NO_SUCH_KEY);
		errorCodeFilterList.add(COSSResultCode.NO_SUCH_UPLOAD);
		errorCodeFilterList.add(COSSResultCode.NO_SUCH_CORS_CONFIGURATION);
		errorCodeFilterList.add(COSSResultCode.NO_SUCH_WEBSITE_CONFIGURATION);
		errorCodeFilterList.add(COSSResultCode.NO_SUCH_LIFECYCLE);
	}

}