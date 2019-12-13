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

import static java.lang.Thread.sleep;
import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * Java thread utility tools.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月22日
 * @since
 */
public abstract class ThreadUtils {

	/**
	 * Random sleep current thread.
	 * 
	 * @param least
	 *            the least value returned
	 * @param bound
	 *            the upper bound (exclusive)
	 * @throws IllegalStateException
	 */
	public static void sleepRandom(long least, long bound) throws IllegalStateException {
		try {
			sleep(current().nextLong(least, bound));
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

}