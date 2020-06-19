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
package com.wl4g.devops.common.config;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static java.lang.reflect.Modifier.*;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.AbstractGenericPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import com.wl4g.devops.common.framework.operator.OperatorAutoHandleInterceptor;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.common.framework.operator.EmptyOperator;
import com.wl4g.devops.common.framework.operator.Operator;
import com.wl4g.devops.common.logging.TraceLoggingMDCFilter;
import com.wl4g.devops.common.web.RespBase.ErrorPromptMessageBuilder;
import com.wl4g.devops.components.tools.common.log.SmartLogger;

/**
 * System boot defaults auto configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年2月20日
 * @since
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SystemBootAutoConfiguration implements ApplicationContextAware {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * {@link ApplicationContext}
	 */
	protected ApplicationContext actx;

	@Override
	public void setApplicationContext(ApplicationContext actx) throws BeansException {
		notNullOf(actx, "applicationContext");
		this.actx = actx;
		initBootProperties(actx.getEnvironment());
	}

	/**
	 * Boot global properties initializing.
	 * 
	 * @param env
	 */
	protected void initBootProperties(Environment env) {
		// Sets API message prompt
		initErrorPrompt(env);
	}

	/**
	 * Initialzing API error prompt.
	 * 
	 * @param env
	 */
	protected void initErrorPrompt(Environment env) {
		String appName = env.getRequiredProperty("spring.application.name");
		if (appName.length() < DEFAULT_PROMPT_MAX_LENGTH) {
			ErrorPromptMessageBuilder.setPrompt(appName);
		} else {
			ErrorPromptMessageBuilder.setPrompt(appName.substring(0, 4));
		}
	}

	// --- C U S T O M A T I O N _ L O G G I N G _ M D C. ---

	@Bean
	@ConditionalOnMissingBean(TraceLoggingMDCFilter.class)
	public TraceLoggingMDCFilter defaultTraceLoggingMDCFilter(ApplicationContext context) {
		return new TraceLoggingMDCFilter(context) {
		};
	}

	@Bean
	@ConditionalOnBean(TraceLoggingMDCFilter.class)
	public FilterRegistrationBean<TraceLoggingMDCFilter> defaultTraceLoggingMDCFilterBean(TraceLoggingMDCFilter filter) {
		FilterRegistrationBean<TraceLoggingMDCFilter> filterBean = new FilterRegistrationBean<>(filter);
		filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		// Cannot use '/*' or it will not be added to the container chain (only
		// '/**')
		filterBean.addUrlPatterns("/*");
		return filterBean;
	}

	// --- C U S T O M A T I O N _ F R A M E W O R K. ---

	@Bean
	@ConditionalOnMissingBean(Operator.class)
	public Operator<Enum<?>> emptyOperator() {
		return new EmptyOperator();
	}

	@Bean
	@ConditionalOnBean(Operator.class)
	public OperatorAutoHandleInterceptor operatorAutoHandleInterceptor() {
		return new OperatorAutoHandleInterceptor();
	}

	@Bean
	@ConditionalOnBean(OperatorAutoHandleInterceptor.class)
	public PointcutAdvisor compositeOperatorAspectJExpressionPointcutAdvisor(OperatorAutoHandleInterceptor advice) {
		AbstractGenericPointcutAdvisor advisor = new AbstractGenericPointcutAdvisor() {
			final private static long serialVersionUID = 1L;

			@Override
			public Pointcut getPointcut() {
				return new Pointcut() {

					final private List<String> EXCLUDED_METHODS = new ArrayList<String>(4) {
						private static final long serialVersionUID = 3369346948736795743L;
						{
							addAll(asList(Operator.class.getDeclaredMethods()).stream().map(m -> m.getName()).collect(toList()));
							addAll(asList(GenericOperatorAdapter.class.getDeclaredMethods()).stream().map(m -> m.getName())
									.collect(toList()));
							addAll(asList(Object.class.getDeclaredMethods()).stream().map(m -> m.getName()).collect(toList()));
						}
					};

					@Override
					public MethodMatcher getMethodMatcher() {
						return new MethodMatcher() {

							@Override
							public boolean matches(Method method, Class<?> targetClass) {
								Class<?> declareClass = method.getDeclaringClass();
								return !isAbstract(method.getModifiers()) && isPublic(method.getModifiers())
										&& !isInterface(declareClass.getModifiers())
										&& !EXCLUDED_METHODS.contains(method.getName());
							}

							@Override
							public boolean isRuntime() {
								return false;
							}

							@Override
							public boolean matches(Method method, Class<?> targetClass, Object... args) {
								throw new Error("Shouldn't be here");
							}
						};
					}

					@Override
					public ClassFilter getClassFilter() {
						return clazz -> {
							return Operator.class.isAssignableFrom(clazz) && !GenericOperatorAdapter.class.isAssignableFrom(clazz)
									&& !isAbstract(clazz.getModifiers()) && !isInterface(clazz.getModifiers());
						};
					}
				};
			}
		};
		advisor.setAdvice(advice);
		return advisor;
	}

	// --- C U S T O M A T I O N _ S E R V L E T _ C O N T A I N E R. ---

	/**
	 * API prompt max length.
	 */
	final private static int DEFAULT_PROMPT_MAX_LENGTH = 4;

}