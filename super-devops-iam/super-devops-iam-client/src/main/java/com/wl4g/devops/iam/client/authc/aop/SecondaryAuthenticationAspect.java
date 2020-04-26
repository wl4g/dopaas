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
package com.wl4g.devops.iam.client.authc.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.wl4g.devops.iam.client.annotation.SecondAuthenticate;
import com.wl4g.devops.iam.common.aop.AdviceProcessor;

/**
 * Secondary authenticate aspect
 * 
 * @author wangl.sir
 * @version v1.0 2019年2月28日
 * @since
 */
@Aspect
public class SecondaryAuthenticationAspect {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Secondary authentication aspect advice processor
	 */
	final AdviceProcessor<SecondAuthenticate> processor;

	public SecondaryAuthenticationAspect(AdviceProcessor<SecondAuthenticate> processor) {
		Assert.notNull(processor, "'adviceProcessor' must not be null");
		this.processor = processor;
	}

	@Pointcut("@annotation(com.wl4g.devops.iam.client.annotation.SecondAuthenticate)")
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
	public Object intercept(ProceedingJoinPoint jp, SecondAuthenticate annotation) throws Throwable {
		return this.processor.doIntercept(jp, annotation);
	}

}