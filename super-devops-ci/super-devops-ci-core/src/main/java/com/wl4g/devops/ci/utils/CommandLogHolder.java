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
package com.wl4g.devops.ci.utils;

import static java.lang.ThreadLocal.withInitial;
import static java.util.Collections.synchronizedMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.Assert.hasText;

import java.util.HashMap;
import java.util.Map;

import com.google.common.annotations.Beta;

/**
 * Command logs holders.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月23日
 * @since
 */
@Beta
public abstract class CommandLogHolder {

	/** Current logs cache. */
	final private static ThreadLocal<Map<String, StringBuffer>> logCache = withInitial(() -> synchronizedMap(new HashMap<>(8)));

	/**
	 * Append log message to current buffer cache.
	 * 
	 * @param message
	 */
	public static void logAdd(String key, String message) {
		Map<String, StringBuffer> cache = logCache.get();
		StringBuffer buffer = cache.get(key);
		if (isNull(buffer)) {
			cache.putIfAbsent(key, (buffer = new StringBuffer(64)));
		}
		buffer.append(message);
	}

	/**
	 * Get message buffer cache by key.
	 * 
	 * @param key
	 * @return
	 */
	public static String getCleanup(String key) {
		String text = null;
		StringBuffer buffer = logCache.get().get(key);
		if (nonNull(buffer)) {
			text = buffer.toString();
			buffer.setLength(0);
		}
		return text;
	}

	/**
	 * Cleanup multi buffer cache by key.
	 * 
	 * @param key
	 */
	public static void cleanup(String key) {
		StringBuffer buffer = logCache.get().get(key);
		if (nonNull(buffer)) {
			buffer.setLength(0);
		}
	}

	/**
	 * Cleanup multi buffer cache all.
	 */
	public static void cleanup() {
		logCache.get().clear();
	}

	/**
	 * Get or create log appender.
	 * 
	 * @param key
	 * @return
	 */
	public static LogAppender getLogAppender(String key) {
		return new LogAppender(key);
	}

	/**
	 * Log appender.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年10月23日
	 * @since
	 */
	public static class LogAppender {

		/** Log appender key-name */
		final private String key;

		public LogAppender(String key) {
			hasText(key, "Log appender key must not be empty.");
			this.key = key;
		}

		public String getKey() {
			return key;
		}

		/**
		 * Append log message to current buffer cache.
		 * 
		 * @param message
		 */
		public void logAdd(String message) {
			CommandLogHolder.logAdd(key, message);
		}

		/**
		 * Get message buffer cache by key.
		 * 
		 * @param key
		 * @return
		 */
		public String getCleanup(String key) {
			return CommandLogHolder.getCleanup(key);
		}

	}

	public static void main(String[] args) {
		LogAppender appender = CommandLogHolder.getLogAppender("test1");
		appender.logAdd("asasdfasdf");
		appender.logAdd("2rerwqsadfa");
		appender.logAdd("6734665347");
		System.out.println(appender.getCleanup("test1"));
	}

}
