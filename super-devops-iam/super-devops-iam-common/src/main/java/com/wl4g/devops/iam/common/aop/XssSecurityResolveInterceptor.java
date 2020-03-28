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
package com.wl4g.devops.iam.common.aop;

import com.wl4g.devops.iam.common.annotation.UnsafeXss;
import com.wl4g.devops.iam.common.config.XssProperties;
import com.wl4g.devops.iam.common.security.xss.XssSecurityResolver;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.wl4g.devops.tool.common.bean.BeanUtils2.deepCopyFieldState;
import static com.wl4g.devops.tool.common.reflect.ReflectionUtils2.isCompatibleType;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.ReflectionUtils.makeAccessible;

/**
 * XSS security resolve aspect intercept handle
 *
 * @author wangl.sir
 * @version v1.0 2019年2月28日
 * @since
 */
public class XssSecurityResolveInterceptor implements MethodInterceptor {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * XSS properties configuration
	 */
	final protected XssProperties config;

	/**
	 * XSS resolve processor
	 */
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
		if (isNull(argument) || isCompatibleType(ServletRequest.class, argument.getClass())
				|| isCompatibleType(ServletResponse.class, argument.getClass())) {
			return;
		}

		// Recursive traversal and XSS encoding.
		deepCopyFieldState(argument, argument, (target, tf, sf, sourcePropertyValue) -> {
			if (nonNull(sourcePropertyValue)) {
				if (CharSequence.class.isAssignableFrom(tf.getType())) {
					makeAccessible(tf);
					tf.set(target, resolver.doResolve(controller, method, index, sourcePropertyValue.toString()));
				} else {
					tf.set(target, sourcePropertyValue);
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