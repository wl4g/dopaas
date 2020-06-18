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
package com.wl4g.devops.iam.client.authc.secondary;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.iam.client.annotation.SecondaryAuthenticate;

/**
 * Secondary authenticate aspect
 * 
 * @author wangl.sir
 * @version v1.0 2019年2月28日
 * @since
 */
@Aspect
public class SecondaryAuthenticationAdvice {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Secondary authentication aspect advice processor
	 */
	final protected SecondaryAuthenticationHandler<SecondaryAuthenticate> handler;

	public SecondaryAuthenticationAdvice(SecondaryAuthenticationHandler<SecondaryAuthenticate> processor) {
		notNullOf(processor, "adviceProcessor");
		this.handler = processor;
	}

	@Pointcut("@annotation(com.wl4g.devops.iam.client.annotation.SecondaryAuthenticate)")
	private void pointcut() {
	}

	/**
	 * AOP section interception, controller interface required secondary
	 * authentication
	 * 
	 * @param jp
	 * @param annotation
	 * @return
	 * @throws Throwable
	 */
	@Around("pointcut()&&@annotation(annotation)")
	public Object intercept(ProceedingJoinPoint jp, SecondaryAuthenticate annotation) throws Throwable {
		return handler.doIntercept(jp, annotation);
	}

}