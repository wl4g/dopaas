/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.uci.utils;

import com.google.common.annotations.Beta;
import com.wl4g.component.common.annotation.Reserved;

import java.util.HashMap;
import java.util.Map;

import static java.lang.ThreadLocal.withInitial;
import static java.util.Collections.synchronizedMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.*;

/**
 * Command logs holders.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月23日
 * @since
 */
@Beta
@Reserved
public abstract class LogHolder {

	/** Current default logs appender ID. */
	final private static String DEFAULT_LOG_APPENDER = "DefaultLogAppenderID";

	/** Current logs cache. */
	final private static ThreadLocal<Map<String, LogAppender>> logCache = withInitial(() -> synchronizedMap(new HashMap<>(8)));

	/**
	 * Append log message to current buffer cache.
	 * 
	 * @param format
	 * @param args
	 * @return
	 */
	public static LogAppender logDefault(String format, Object... args) {
		return getDefault().log(format, args);
	}

	/**
	 * Append log message to key-buffer cache.
	 * 
	 * @param key
	 * @param message
	 * @return
	 */
	public static StringBuffer addLog(String key, String message) {
		return getLogAppender(key).getMessage().append(message);
	}

	/**
	 * Get default message buffer cache by key.
	 * 
	 * @return
	 */
	public static String cleanupDefault() {
		return cleanup(DEFAULT_LOG_APPENDER);
	}

	/**
	 * Cleanup multiple buffer cache by key.
	 * 
	 * @param key
	 * @return
	 */
	public static String cleanup(String key) {
		StringBuffer buffer = null;
		try {
			LogAppender appender = getLogAppender(key);
			buffer = appender.getMessage();
			if (nonNull(buffer)) {
				return buffer.toString();
			}
			return null;
		} finally {
			if (nonNull(buffer)) {
				buffer.setLength(0);
			}
			logCache.get().remove(key);
		}
	}

	/**
	 * Cleanup multiple buffer cache all.
	 */
	public static void cleanupAll() {
		logCache.get().clear();
	}

	/**
	 * Get or create log appender.
	 * 
	 * @param key
	 * @return
	 */
	public static LogAppender getDefault() {
		return getLogAppender(DEFAULT_LOG_APPENDER);
	}

	/**
	 * Get or create log appender.
	 * 
	 * @param key
	 * @return
	 */
	public static LogAppender getLogAppender(Number key) {
		notNull(key, "Log appender key must not be null.");
		return getLogAppender(String.valueOf(key));
	}

	/**
	 * Get or create log appender.
	 * 
	 * @param key
	 * @return
	 */
	public static LogAppender getLogAppender(String key) {
		hasText(key, "Log appender key must not be empty.");
		Map<String, LogAppender> cache = logCache.get();
		LogAppender appender = cache.get(key);
		if (isNull(appender)) {
			state(isNull(cache.putIfAbsent(key, (appender = new LogAppender(key)))),
					String.format("Already log appender with key: %s", key));
		}
		return appender;
	}

	/**
	 * Log appender.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年10月23日
	 * @since
	 */
	public static class LogAppender {

		/** Log appender key-name. */
		final private String key;

		/** Log appended message. */
		final private StringBuffer message = new StringBuffer(64);

		public LogAppender(String key) {
			hasText(key, "Log appender key must not be empty.");
			this.key = key;
		}

		public String getKey() {
			return key;
		}

		public StringBuffer getMessage() {
			return message;
		}

		/**
		 * Append log message to current buffer cache.
		 * 
		 * @param format
		 * @param args
		 * @return
		 */
		public LogAppender log(String format, Object... args) {
			LogHolder.addLog(key, String.format(format, args));
			return this;
		}

		/**
		 * Get message buffer cache by key.
		 * 
		 * @param key
		 * @return
		 */
		public String cleanup(String key) {
			return LogHolder.cleanup(key);
		}

	}

	public static void main(String[] args) {
		LogAppender appender1 = LogHolder.getLogAppender("test1");
		appender1.log("asasdfasdf");
		appender1.log("6734665347");
		LogAppender appender2 = LogHolder.getLogAppender("test2");
		appender2.log("2rerwqsadfa");
		System.out.println(appender1.cleanup("test1"));
		System.out.println(appender2.cleanup("test2"));
	}

}