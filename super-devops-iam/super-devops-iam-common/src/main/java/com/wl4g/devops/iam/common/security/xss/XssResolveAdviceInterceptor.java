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
package com.wl4g.devops.iam.common.security.xss;

import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.iam.common.annotation.UnsafeXss;
import com.wl4g.devops.iam.common.config.XssProperties;
import com.wl4g.devops.iam.common.security.xss.resolve.XssSecurityResolver;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.wl4g.devops.components.tools.common.bean.BeanUtils2.deepCopyFieldState;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.components.tools.common.reflect.ReflectionUtils2.isCompatibleType;
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
public class XssResolveAdviceInterceptor implements MethodInterceptor {
	final protected SmartLogger log = getLogger(getClass());

	/**
	 * XSS properties configuration
	 */
	final protected XssProperties config;

	/**
	 * XSS resolve processor
	 */
	final protected XssSecurityResolver resolver;

	public XssResolveAdviceInterceptor(XssProperties config, XssSecurityResolver resolver) {
		Assert.notNull(config, "config is null, please check configure");
		Assert.notNull(resolver, "resolver is null, please check configure");
		this.config = config;
		this.resolver = resolver;
	}

	@Override
	public Object invoke(MethodInvocation invc) throws Throwable {
		Object controller = invc.getThis();
		Method md = invc.getMethod();
		try {
			// Type or method exist @UnsafeXss ignore?
			if (controller.getClass().isAnnotationPresent(UnsafeXss.class) || md.isAnnotationPresent(UnsafeXss.class)) {
				return invc.proceed();
			}

			Object[] args = invc.getArguments();
			if (!isNull(args)) {
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
			log.error("XSS resolving failure. causes at: ", e);
		}

		// Sets XSS protection headers.
		setXssProtectionHeadersIfNecessary(controller, md);

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
			wrap.setRequest(resolver.newXssRequestWrapper((HttpServletRequest) wrap.getRequest()));
		} else if (argument instanceof HttpServletRequest) {
			argument = resolver.newXssRequestWrapper((HttpServletRequest) argument);
		}

		return argument;
	}

	/**
	 * Sets XSS protection headers.
	 * 
	 * @param controller
	 * @param md
	 */
	private void setXssProtectionHeadersIfNecessary(Object controller, Method md) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		if (isNull(request) || isNull(response))
			return;

		/**
		 * X-XSS-Protection:
		 * 
		 * As the name suggests, this response header is used to guard against
		 * XSS. It first appeared in IE8. Now the mainstream browsers support
		 * it, and XSS protection is turned on by default. You can turn it off
		 * with this header. It has several configurations:
		 * 
		 * X-XSS-Protection:0 - Disable XSS protection.</br>
		 * X-XSS-Protection:1 - Enable XSS protection.</br>
		 * X-XSS-Protection:1;Mode=block - Enable XSS protection and stop
		 * rendering the page when XSS attack is detected (e.g, in IE8, when the
		 * attack is detected, the whole page will be replaced by one).
		 */
		response.setHeader("X-Xss-Protection", "1; mode=block");

		/**
		 * X-Content-Type-Options:
		 * 
		 * There are various types of resources on the Internet, and browsers
		 * usually distinguish their types according to the content type field
		 * of the response header. For example: "text/html" represents HTML
		 * document, "image/png" is PNG picture, "text/css" is CSS style
		 * document. However, some resources have wrong or undefined content
		 * types. At this time, some browsers will enable mime sniffing to guess
		 * the type of the resource, parse the content and execute.
		 * 
		 * For example, even if we specify the content type of an HTML document
		 * as "text/plain", the document will still be parsed as HTML in IE8,
		 * with this feature of the browser, an attacker can even parse a
		 * request that should have been parsed as a picture into JavaScript.
		 * The following response header can disable the browser's type guessing
		 * behavior.
		 */
		response.setHeader("X-Content-Type-Options", "nosniff");

	}

}