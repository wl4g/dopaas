package com.wl4g.devops.scm.client.configure;

import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import com.wl4g.devops.common.exception.scm.BeanCurrentlyInConfigureException;
import com.wl4g.devops.common.exception.scm.ScmException;
import com.wl4g.devops.scm.client.configure.refresh.BeanRefresher;

/**
 * Bean concurrency secure configuring advice. <br/>
 * http://www.cnblogs.com/larryzeal/p/5829877.html<br/>
 * https://blog.csdn.net/qq_36561843/article/details/80464535<br/>
 * https://my.oschina.net/u/3434392/blog/1625493
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年10月18日
 * @since
 * @see org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean
 */
@Aspect
public class BeanCurrentlyConfigureAspect {

	private BeanRefresher beanRefresher;

	public BeanCurrentlyConfigureAspect(BeanRefresher beanRefresher) {
		super();
		this.beanRefresher = beanRefresher;
	}

	@Pointcut("@within(com.wl4g.devops.scm.client.configure.RefreshBean)")
	public void pointcut() {
	}

	@Around("pointcut()")
	public Object advice(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object target = joinPoint.getTarget();

			// Fail if we're already configuring this bean instance: We're
			// assumably within a circular reference.
			if (this.beanRefresher.isBeanCurrentlyInConfigure(target)) {
				throw new BeanCurrentlyInConfigureException(
						String.format("Bean currently in configuring. %s", target.toString()));
			}

			// Method execution of normal condition to let go of the target.
			return joinPoint.proceed();
		} catch (Throwable e) {
			throw new ScmException(e);
		}
	}

}
