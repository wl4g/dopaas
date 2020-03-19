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

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * Automatic around execution handler method interceptor.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月11日
 * @since
 */
public class OperatorAutoHandleInterceptor implements MethodInterceptor {

	final protected SmartLogger log = getLogger(getClass());

	@Override
	public Object invoke(MethodInvocation invc) throws Throwable {
		Object targetObj = invc.getThis(); // Target object.
		// Method simple name.
		String targetMethodName = invc.getMethod().getName();
		// Method declaring class name.
		String declareClassName = invc.getMethod().getDeclaringClass().getName();

		if (!(targetObj instanceof Operator))
			throw new Error("Shouldn't be here");

		Operator<?> operator = (Operator<?>) targetObj;
		log.debug("Around operator targetObj: {}, method: {}#{}", targetObj, declareClassName, targetMethodName);

		// Call preprocessing
		if (!operator.preHandle(invc.getMethod(), invc.getArguments())) {
			log.warn("Rejected operation of {}#{}", targetObj, targetMethodName);
			return null;
		}
		// Invoke target
		Object returnObj = invc.proceed();

		// Call post processing
		operator.postHandle(invc.getMethod(), invc.getArguments(), returnObj);
		return returnObj;
	}

}
