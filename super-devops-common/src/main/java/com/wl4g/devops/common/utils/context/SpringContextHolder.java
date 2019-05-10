package com.wl4g.devops.common.utils.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
	final private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

	private static ApplicationContext applicationContext = null;

	/**
	 * Implement the ApplicationContextAware interface, injecting the Context
	 * into a static variable.
	 */
	public void setApplicationContext(ApplicationContext actx) {
		logger.debug("Inject the ApplicationContext into the SpringContextHolder:" + actx);
		if (applicationContext != null) {
			logger.warn("The ApplicationContext in the SpringContextHolder is overridden. The original ApplicationContext is:"
					+ applicationContext);
		}
		applicationContext = actx; // NOSONAR
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
		return applicationContext;
	}

	/**
	 * Get the bean from the static variable applicationContext, automatically
	 * transform to the type of the assigned object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * Get the bean from the static variable applicationContext, automatically
	 * transform to the type of the assigned object.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		return (T) applicationContext.getBean(requiredType);
	}

	/**
	 * Clear the ApplicationContext in the SpringContextHolder to Null.
	 */
	public static void clear() {
		logger.debug("Clear the ApplicationContext in the SpringContextHolder:" + applicationContext);
		applicationContext = null;
	}

	/**
	 * Check that the ApplicationContext is not empty.
	 */
	private static void assertContextInjected() {
		if (applicationContext == null) {
			throw new IllegalStateException("applicaitonContext未注入,请在springmvc-servlet.xml中定义SpringContextHolder");
		}
	}

}
