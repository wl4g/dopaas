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
package com.wl4g.devops.iam.common.utils.cumulate;

import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;

import com.wl4g.devops.iam.common.cache.IamCache;

/**
 * Accumulator tools
 *
 * @author wangl.sir
 * @version v1.0 2019年4月19日
 * @since
 */
public abstract class CumulateHolder {

	/**
	 * New create default/(distributed caching) accumulator
	 *
	 * @param cache
	 * @param expireMs
	 *            Expired milliseconds
	 * @return
	 */
	public static Cumulator newCumulator(IamCache cache, long expireMs) {
		notNullOf(cache, "defaultCumulator.cache");
		return new DefaultCumulator(cache, expireMs);
	}

	/**
	 * New create session cache accumulator.
	 *
	 * @param name
	 *            cululator name of sessionKey prefix
	 * @param expireMs
	 *            Expired milliseconds
	 * @return
	 */
	public static Cumulator newSessionCumulator(String name, long expireMs) {
		hasTextOf(name, "sessionCumulator.name");
		return new SessionCumulator(name, expireMs);
	}

}