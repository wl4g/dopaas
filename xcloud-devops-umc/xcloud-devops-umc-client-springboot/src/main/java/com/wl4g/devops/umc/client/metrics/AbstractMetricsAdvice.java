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
package com.wl4g.devops.umc.client.metrics;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * AOP mode service monitoring section based on spring boot admin.<br/>
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年5月30日
 * @since
 */
public abstract class AbstractMetricsAdvice implements MethodInterceptor {
	final private static Map<Method, String> methodSignCache = new ConcurrentHashMap<>();

	/**
	 * Production unique name based on method name
	 * 
	 * @param invocation
	 * @return
	 */
	protected String getMetricName(MethodInvocation invocation) {
		String metricName = methodSignCache.get(invocation.getMethod());
		if (metricName == null) {
			Method m = invocation.getMethod();
			StringBuffer sign = new StringBuffer(this.classNameForShort(invocation.getThis().getClass().getName()));
			sign.append(".");
			sign.append(m.getName());
			sign.append("(");
			Parameter[] params = m.getParameters();
			if (params != null) {
				for (Parameter p : params) {
					sign.append(this.paramTypeForShort(p.getType().getSimpleName()));
					sign.append(" ");
					sign.append(p.getName());
					sign.append(",");
				}
				if (sign.length() > 1) {
					sign.delete(sign.length() - 1, sign.length());
				}
				sign.append(")");
			}
			methodSignCache.put(m, metricName = sign.toString());
		}
		return metricName;
	}

	/**
	 * io.transport.common.cache.JedisService -> i.t.c.c.JedisService
	 * 
	 * @param methodName
	 * @return
	 */
	private String classNameForShort(String methodName) {
		StringBuffer name = new StringBuffer();
		String[] arr = methodName.split("\\.");
		for (int i = 0; i < arr.length - 1; i++) {
			if (i >= (arr.length - 1)) {
				name.append(arr[i]);
			} else {
				name.append(arr[i].substring(0, 1));
			}
			name.append(".");
		}
		name.append(arr[arr.length - 1]);
		return name.toString();
	}

	/**
	 * java.lang.String -> String <br/>
	 * Map<java.lang.String, java.lang.String> -> Map<String, String> <br/>
	 * List<java.lang.String> -> List<String> <br/>
	 * 
	 * @param paramTypeName
	 * @return
	 */
	private String paramTypeForShort(String paramTypeName) {
		String[] arr = paramTypeName.split("\\.");
		return arr[arr.length - 1];
	}

}