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
public class SecondAuthenticateAspect {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Secondary authentication aspect advice processor
	 */
	final AdviceProcessor<SecondAuthenticate> processor;

	public SecondAuthenticateAspect(AdviceProcessor<SecondAuthenticate> processor) {
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