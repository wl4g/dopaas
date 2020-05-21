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
package com.wl4g.devops.iam.common.utils;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;

import com.wl4g.devops.tool.common.bean.KeyValue;

/**
 * Iam organization tools utils.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月21日
 * @since
 */
public abstract class IamSystemHandlerUtils extends IamAuthenticatingUtils {

	/**
	 * Parameters local cache.
	 */
	final private static ThreadLocal<List<KeyValue>> paramsLocal = new ThreadLocal<>();

	// --- System handler parameters. ---

	/**
	 * Gets current request systems parameters.
	 * 
	 * @return
	 */
	public static List<KeyValue> getCurrentRequestParameters() {
		List<KeyValue> params = paramsLocal.get();
		return isEmpty(params) ? emptyList() : params;
	}

	/**
	 * Sets current request parameters.
	 * 
	 * @param params
	 * @return
	 */
	public static List<KeyValue> setCurrentRequestParameters(List<KeyValue> params) {
		List<KeyValue> _params = getCurrentRequestParameters();
		if (!isEmpty(params)) {
			_params = new ArrayList<>(_params);
			_params.addAll(params);
		}
		return _params;
	}

	/**
	 * Adds current request parameters.
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static List<KeyValue> setCurrentRequestParameters(String key, String value) {
		return setCurrentRequestParameters(singletonList(new KeyValue(key, value)));
	}

	// --- Organication tools. ---

}
