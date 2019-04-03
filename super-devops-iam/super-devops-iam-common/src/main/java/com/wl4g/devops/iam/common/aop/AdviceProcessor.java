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
