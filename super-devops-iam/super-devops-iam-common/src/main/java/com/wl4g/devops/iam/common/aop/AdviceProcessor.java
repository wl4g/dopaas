/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.iam.common.aop;

import java.lang.annotation.Annotation;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Aspect advice intercept processor
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年3月9日
 * @since
 * @param <A>
 */
public interface AdviceProcessor<A extends Annotation> {

	/**
	 * Perform AOP section notification interception
	 * 
	 * @param jp
	 * @param annotation
	 * @return
	 * @throws Throwable
	 */
	Object doIntercept(ProceedingJoinPoint jp, A annotation) throws Throwable;

}