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
package com.wl4g.devops.components.tools.common.lang;

import static java.util.concurrent.ThreadLocalRandom.current;

import org.apache.commons.lang3.ThreadUtils;

/**
 * Java thread utility tools.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月22日
 * @since
 */
public abstract class ThreadUtils2 extends ThreadUtils {

	/**
	 * Random sleep current thread.
	 * 
	 * @param least
	 *            the least value returned
	 * @param bound
	 *            the upper bound (exclusive)
	 * @throws IllegalStateException
	 */
	public static void sleep(long sleepMillis) throws IllegalStateException {
		try {
			Thread.sleep(sleepMillis);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Random sleep current thread.
	 * 
	 * @param least
	 *            the least value returned
	 * @param bound
	 *            the upper bound (exclusive)
	 * @return Returns the duration of the previous sleep(ms)
	 * @throws IllegalStateException
	 */
	public static long sleepRandom(long least, long bound) throws IllegalStateException {
		long sleepMillis = current().nextLong(least, bound);
		sleep(sleepMillis);
		return sleepMillis;
	}

}