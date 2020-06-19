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
package com.wl4g.devops.components.tools.common.id;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.concurrent.ThreadLocalRandom;
import static java.util.UUID.*;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Simple ID generator utility tools.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年3月4日
 * @since
 */
public abstract class SimpleIdGenerator {

	/**
	 * Generate of specified minimum length UUID random strings
	 * 
	 * @param minSize
	 * @return
	 */
	public static String generateVariableUid(int minSize) {
		return generateVariableUid("g", minSize);
	}

	/**
	 * Generate of specified minimum length UUID random strings
	 * 
	 * @param prefix
	 * @param minSize
	 * @return
	 */
	public static String generateVariableUid(String prefix, int minSize) {
		// UUID origin
		StringBuffer uuids = new StringBuffer();
		int len = uuids.length();
		while ((len = uuids.length()) <= minSize) {
			// Generate random UUID
			uuids.append(randomUUID().toString().replaceAll("-", ""));
		}
		isTrue((minSize < (len - 1)), "Minimum length (%s) greater than UUID length (%s)", minSize, len);

		// Create randomer
		ThreadLocalRandom current = ThreadLocalRandom.current();
		int start = current.nextInt(0, len - minSize);
		int end = current.nextInt(start + minSize, len);
		// Sub random UUID
		String res = uuids.substring(min(start, end), max(start, end));
		// Append prefix
		if (!isBlank(prefix)) {
			res = prefix.concat(res.substring(2));
		}
		return res;
	}

}