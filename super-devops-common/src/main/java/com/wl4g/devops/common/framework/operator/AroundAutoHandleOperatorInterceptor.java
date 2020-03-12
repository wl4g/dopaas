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
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import static java.lang.reflect.Modifier.*;
import java.util.ArrayList;
import java.util.List;

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
public class AroundAutoHandleOperatorInterceptor implements MethodInterceptor {

	final protected SmartLogger log = getLogger(getClass());

	@Override
	public Object invoke(MethodInvocation invo) throws Throwable {
		// Target object.
		Object targetObj = invo.getThis();
		// Method simple name.
		String targetMethodName = invo.getMethod().getName();
		// Method declaring class name.
		String declareClassName = invo.getMethod().getDeclaringClass().getName();

		if (!(targetObj instanceof Operator))
			throw new Error("Shouldn't be here");

		Operator<?> operator = (Operator<?>) targetObj;
		if (!EXCLUDED_CLASSES.contains(declareClassName) && !EXCLUDED_METHODS.contains(targetMethodName)
				&& isPublic(invo.getMethod().getModifiers())) {
			log.debug("Around operator targetObj: {}, method: {}#{}", targetObj, declareClassName, targetMethodName);

			// Call preprocessing
			operator.preHandle(invo.getArguments());
			// Invoke target
			Object returnObj = invo.proceed();
			// Call post processing
			operator.postHandle(invo.getArguments(), returnObj);
			return returnObj;
		}

		return invo.proceed();
	}

	final public static List<String> EXCLUDED_METHODS = new ArrayList<String>(4) {
		private static final long serialVersionUID = 3369346948736795743L;
		{
			addAll(asList(Operator.class.getDeclaredMethods()).stream().map(m -> m.getName()).collect(toList()));
			addAll(asList(GenericOperatorAdapter.class.getDeclaredMethods()).stream().map(m -> m.getName()).collect(toList()));
			addAll(asList(Object.class.getDeclaredMethods()).stream().map(m -> m.getName()).collect(toList()));
		}
	};

	final public static List<String> EXCLUDED_CLASSES = new ArrayList<String>(4) {
		private static final long serialVersionUID = 3369346948736795743L;
		{
			add(Operator.class.getName());
			add(GenericOperatorAdapter.class.getName());
		}
	};

}
