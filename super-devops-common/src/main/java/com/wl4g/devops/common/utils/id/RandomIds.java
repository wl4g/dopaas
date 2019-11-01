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
package com.wl4g.devops.common.utils.id;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Random IDS
 * 
 * @author wangl.sir
 * @version v1.0 2019年3月1日
 * @since
 */
public class RandomIds {

	/**
	 * Secure generation of specified minimum length UUID random strings
	 * 
	 * @param minLen
	 * @return
	 */
	public static String genVariableMeaningUUID(int minLen) {
		return genVariableMeaningUUID("g_", minLen);
	}

	/**
	 * Secure generation of specified minimum length UUID random strings
	 * 
	 * @param prefix
	 * @param minLen
	 * @return
	 */
	public static String genVariableMeaningUUID(String prefix, int minLen) {
		// UUID origin
		StringBuffer uuids = new StringBuffer();
		int len = uuids.length();
		while ((len = uuids.length()) <= minLen) {
			// Generate random UUID
			uuids.append(UUID.randomUUID().toString().replaceAll("-", ""));
		}
		Assert.isTrue((minLen < (len - 1)), String.format("Minimum length (%s) greater than UUID length (%s)", minLen, len));

		// Random
		ThreadLocalRandom current = ThreadLocalRandom.current();
		int start = current.nextInt(0, len - minLen);
		int end = current.nextInt(start + minLen, len);
		// Sub random UUID
		String res = uuids.substring(Math.min(start, end), Math.max(start, end));
		// Append prefix
		if (!StringUtils.isEmpty(prefix)) {
			res = prefix + res.substring(2);
		}
		return res;
	}

}