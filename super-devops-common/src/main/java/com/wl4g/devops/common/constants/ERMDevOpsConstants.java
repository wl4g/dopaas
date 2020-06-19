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
package com.wl4g.devops.common.constants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Based share constants
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-20 09:21:00
 */
public abstract class ERMDevOpsConstants extends DevOpsConstants {

	/** Logging level names define. */
	final public static List<String> LOG_LEVEL = Collections
			.unmodifiableList(Arrays.asList("TRACE", "DEBUG", "INFO", "WARN", "ERROR"));

	/** Elastic search default message name define. */
	final public static String KEY_DEFAULT_MSG = "message";

	/**
	 * System dictionaries cache.
	 */
	public static final String KEY_CACHE_SYS_DICT_INIT_CACHE = "dict_init_cache";

	/**
	 * Config dict cache time(second)
	 */
	public static final int CONFIG_DICT_CACHE_TIME_SECOND = 60;

}