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
package com.wl4g.devops.components.tools.common.log;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

/**
 * Enhanced logger factory for intelligent/dynamic/humanized wrapper.
 * {@link SmartLogger}
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-12-13
 * @since
 * @see {@link SmartLogger}
 */
public class SmartLoggerFactory {

	/**
	 * Return a logger named according to the name parameter using the
	 * statically bound {@link ILoggerFactory} instance.
	 * 
	 * @param name
	 *            The name of the logger.
	 * @return logger
	 */
	public static SmartLogger getLogger(String name) {
		return new SmartLogger(LoggerFactory.getLogger(name));
	}

	/**
	 * Return a logger named corresponding to the class passed as parameter,
	 * using the statically bound {@link ILoggerFactory} instance.
	 * 
	 * <p>
	 * In case the the <code>clazz</code> parameter differs from the name of the
	 * caller as computed internally by SLF4J, a logger name mismatch warning
	 * will be printed but only if the
	 * <code>slf4j.detectLoggerNameMismatch</code> system property is set to
	 * true. By default, this property is not set and no warnings will be
	 * printed even in case of a logger name mismatch.
	 * 
	 * @param clazz
	 *            the returned logger will be named after clazz
	 * @return logger
	 * 
	 * 
	 * @see <a href=
	 *      "http://www.slf4j.org/codes.html#loggerNameMismatch">Detected logger
	 *      name mismatch</a>
	 */
	public static SmartLogger getLogger(Class<?> clazz) {
		return new SmartLogger(LoggerFactory.getLogger(clazz));
	}

}