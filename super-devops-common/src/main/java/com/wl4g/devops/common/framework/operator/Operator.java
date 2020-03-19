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
package com.wl4g.devops.common.framework.operator;

import java.lang.reflect.Method;

/**
 * Generic operator adapter.
 * 
 * @param <K>
 * @author wanglsir@gmail.com, 983708408@qq.com
 * @version 2020年1月6日 v1.0.0
 * @see
 */
public interface Operator<K extends Enum<?>> {

	/**
	 * Get the type of operator (kind).
	 *
	 * @return
	 */
	K kind();

	/**
	 * Preprocessing, such as checking connections to remote services.
	 * 
	 * @param method
	 *            target method.
	 * @param args
	 *            Input formal parameters
	 * @return
	 */
	default boolean preHandle(Method method, Object[] args) {
		return true;
	}

	/**
	 * Post processing, such as checking the actual processing results.
	 * 
	 * @param method
	 *            target method.
	 * @param args
	 *            Input formal parameters
	 * @param returnObj
	 *            Execute return result object
	 */
	default void postHandle(Method method, Object[] args, Object returnObj) {

	}

}