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
public class BeanCurrentlyConfiguringInterceptor {

	private BeanRefresher beanRefresher;

	public BeanCurrentlyConfiguringInterceptor(BeanRefresher beanRefresher) {
		super();
		this.beanRefresher = beanRefresher;
	}

	@Pointcut("@within(com.wl4g.devops.scm.client.configure.RefreshBean)")
	public void pointcut() {
	}

	@Around("pointcut()")
	public Object intercept(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			Object target = joinPoint.getTarget();

			// Fail if we're already configuring this bean instance: We're
			// assumably within a circular reference.
			if (beanRefresher.isBeanCurrentlyInConfigure(target)) {
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