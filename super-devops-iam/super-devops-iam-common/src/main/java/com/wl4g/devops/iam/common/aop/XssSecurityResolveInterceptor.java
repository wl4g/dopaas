/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static java.lang.reflect.Modifier.*;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.annotation.Aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.util.ReflectionUtils.*;
import org.springframework.util.Assert;

import static com.wl4g.devops.common.utils.bean.BeanUtils2.*;
import com.wl4g.devops.common.utils.bean.BeanUtils2.FieldFilter;
import com.wl4g.devops.iam.common.annotation.UnsafeXss;
import com.wl4g.devops.iam.common.attacks.xss.XssSecurityResolver;
import com.wl4g.devops.iam.common.config.XssProperties;

/**
 * XSS security resolve aspect intercept handle
 * 
 * @author wangl.sir
 * @version v1.0 2019年2月28日
 * @since
 */
@Aspect
public class XssSecurityResolveInterceptor implements MethodInterceptor {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** XSS properties configuration */
	final protected XssProperties config;

	/** XSS resolve processor */
	final protected XssSecurityResolver resolver;

	public XssSecurityResolveInterceptor(XssProperties config, XssSecurityResolver resolver) {
		Assert.notNull(config, "config is null, please check configure");
		Assert.notNull(resolver, "resolver is null, please check configure");
		this.config = config;
		this.resolver = resolver;
	}

	@Override
	public Object invoke(MethodInvocation invc) throws Throwable {
		try {
			Object controller = invc.getThis();
			Method md = invc.getMethod();

			// Type or method exist @UnsafeXss ignore?
			if (controller.getClass().isAnnotationPresent(UnsafeXss.class) || md.isAnnotationPresent(UnsafeXss.class)) {
				return invc.proceed();
			}

			Object[] args = invc.getArguments();
			if (args != null) {
				next: for (int i = 0; i < args.length; i++) {
					if (args[i] == null)
						continue;

					// Parameter ignore?
					for (Annotation[] anns : md.getParameterAnnotations()) {
						for (Annotation an : anns) {
							if (an.annotationType() == UnsafeXss.class) {
								continue next;
							}
						}
					}

					// Parameter declared type ignore?
					if (args[i].getClass().isAnnotationPresent(UnsafeXss.class)) {
						continue next;
					}

					// Processing HttpServlet request(if necessary)
					args[i] = processHttpRequestIfNecessary(args[i]);

					if (args[i] instanceof String) {
						args[i] = stringXssEncode(controller, md, i, (String) args[i]);
					} else {
						objectXssEnode(controller, md, i, args[i]);
					}
				}
			}
		} catch (Throwable e) {
			log.error("XSS resolve processing failure. causes at: ", e);
		}

		return invc.proceed();
	}

	/**
	 * String argument XSS encoding.
	 * 
	 * @param controller
	 * @param method
	 * @param index
	 * @param argument
	 * @return
	 */
	private String stringXssEncode(final Object controller, final Method method, final int index, final String argument) {
		if (isBlank(argument)) {
			return argument;
		}
		return resolver.doResolve(controller, method, index, argument);
	}

	/**
	 * Object argument XSS encoding.
	 * 
	 * @param controller
	 * @param method
	 * @param index
	 * @param argument
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private void objectXssEnode(final Object controller, final Method method, final int index, final Object argument)
			throws IllegalArgumentException, IllegalAccessException {
		if (argument == null || ServletRequest.class.isAssignableFrom(argument.getClass())
				|| ServletResponse.class.isAssignableFrom(argument.getClass()))
			return;

		copyFullProperties(argument, argument, new FieldFilter() {
			@Override
			public boolean match(Field f, Object sourceProperty) {
				Class<?> clazz = f.getType();
				int mod = f.getModifiers();
				return String.class.isAssignableFrom(clazz) && !isFinal(mod) && !isStatic(mod) && !isTransient(mod)
						&& !isNative(mod) && !isVolatile(mod) && !isSynchronized(mod);
			}
		}, new FieldCopyer() {
			@Override
			public void doCopy(Object target, Field tf, Field sf, Object sourcePropertyValue)
					throws IllegalArgumentException, IllegalAccessException {
				if (sourcePropertyValue != null) {
					makeAccessible(tf);
					tf.set(target, resolver.doResolve(controller, method, index, (String) sourcePropertyValue));
				}
			}
		});

	}

	/**
	 * Processing HttpServlet request(if necessary)
	 * 
	 * @param argument
	 * @return
	 */
	private Object processHttpRequestIfNecessary(Object argument) {
		if (argument instanceof HttpServletRequestWrapper) { // ShiroHttpServletRequest?
			HttpServletRequestWrapper wrap = (HttpServletRequestWrapper) argument;
			/*
			 * Wrapping request with resolved XSS security.
			 */
			wrap.setRequest(resolver.newXssHttpRequestWrapper((HttpServletRequest) wrap.getRequest()));
		} else if (argument instanceof HttpServletRequest) {
			argument = resolver.newXssHttpRequestWrapper((HttpServletRequest) argument);
		}

		return argument;
	}

}