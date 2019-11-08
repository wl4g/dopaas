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
package com.wl4g.devops.common.utils.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Spring container context holder.</br>
 * 
 * Deprecated use classes, because there may be injection disorder leading to
 * exceptions when using @{@link Configuration} and @{@link Component}
 * or @{@link Service} etc.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-07-12
 * @since
 */
@Component("devopsUtilitySpringContainerContexts")
@Lazy(false)
@Deprecated
public class SpringContexts implements ApplicationContextAware, DisposableBean {
	final private static Logger logger = LoggerFactory.getLogger(SpringContexts.class);

	private static ApplicationContext _actx;

	/**
	 * Implement the ApplicationContextAware interface, injecting the Context
	 * into a static variable.
	 */
	@Override
	public void setApplicationContext(ApplicationContext actx) {
		logger.debug("Inject the ApplicationContext into the SpringContextHolder:" + actx);
		if (_actx != null) {
			logger.warn("The ApplicationContext in the SpringContextHolder is overridden. The original ApplicationContext is:"
					+ _actx);
		}
		_actx = actx; // NOSONAR

	}

	/**
	 * Implement the DisposableBean interface to clean up static variables when
	 * the Context is closed.
	 */
	public void destroy() throws Exception {
		clear();
	}

	/**
	 * Get the ApplicationContext stored in a static variable.
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return _actx;
	}

	/**
	 * Get the bean from the static variable applicationContext, automatically
	 * transform to the type of the assigned object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) _actx.getBean(name);
	}

	/**
	 * Get the bean from the static variable applicationContext, automatically
	 * transform to the type of the assigned object.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		return (T) _actx.getBean(requiredType);
	}

	/**
	 * Clear the ApplicationContext in the SpringContextHolder to Null.
	 */
	public static void clear() {
		logger.debug("Clear the ApplicationContext in the SpringContextHolder:" + _actx);
		_actx = null;
	}

	/**
	 * Check that the ApplicationContext is not empty.
	 */
	private static void assertContextInjected() {
		if (_actx == null) {
			throw new IllegalStateException("applicaitonContext未注入,请在springmvc-servlet.xml中定义SpringContextHolder");
		}
	}

}