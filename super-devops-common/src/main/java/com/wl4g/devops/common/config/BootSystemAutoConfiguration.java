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

import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

import static java.lang.reflect.Modifier.*;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.TaskThreadFactory;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.support.AbstractGenericPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import com.wl4g.devops.common.framework.operator.AroundAutoHandleOperatorInterceptor;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.common.framework.operator.EmptyOperator;
import com.wl4g.devops.common.framework.operator.Operator;
import com.wl4g.devops.common.logging.TraceLoggingMDCFilter;
import com.wl4g.devops.common.web.RespBase.ErrorPromptMessageBuilder;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * System properties auto configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年2月20日
 * @since
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class BootSystemAutoConfiguration implements ApplicationContextAware {

	final private static SmartLogger log = getLogger(BootSystemAutoConfiguration.class);

	/**
	 * API prompt max length.
	 */
	final private static int PROMPT_MAX_LEN = 4;

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
		// Set API message prompt
		initErrorPrompt(env);
	}

	/**
	 * Initialzing API error prompt.
	 * 
	 * @param env
	 */
	protected void initErrorPrompt(Environment env) {
		String appName = env.getRequiredProperty("spring.application.name");
		if (appName.length() < PROMPT_MAX_LEN) {
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
	public FilterRegistrationBean defaultTraceLoggingMDCFilterBean(TraceLoggingMDCFilter filter) {
		FilterRegistrationBean filterBean = new FilterRegistrationBean(filter);
		filterBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		// Cannot use '/*' or it will not be added to the container chain (only
		// '/**')
		filterBean.addUrlPatterns("/*");
		return filterBean;
	}

	// --- C U S T O M A T I O N _ S E R V L E T _ C O N T A I N E R. ---

	/**
	 * 
	 * Customization servlet container configuring. </br>
	 * 
	 * @see {@link EmbeddedServletContainerAutoConfiguration}
	 * 
	 * @return
	 */
	@Bean
	public EmbeddedServletContainerCustomizer customEmbeddedServletContainerCustomizer() {
		return container -> {
			// Tomcat container customization
			if (container instanceof TomcatEmbeddedServletContainerFactory) {
				TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
				tomcat.addConnectorCustomizers(connector -> {
					ProtocolHandler handler = connector.getProtocolHandler();
					if (handler instanceof AbstractProtocol) {
						AbstractProtocol<?> protocol = (AbstractProtocol<?>) handler;
						/**
						 * {@link org.apache.tomcat.util.net.NioEndpoint#startInternal()}
						 * {@link org.apache.tomcat.util.net.NioEndpoint#createExecutor()}
						 */
						protocol.setExecutor(customTomcatExecutor(protocol));
					}
				});
			} else {
				log.warn("Skip using custom servlet container, EmbeddedServletContainer: {}", container);
			}
		};

	}

	/**
	 * Custom tomcat executor
	 * 
	 * @param protocol
	 * @return
	 */
	private Executor customTomcatExecutor(AbstractProtocol<?> protocol) {
		TaskThreadFactory tf = new TaskThreadFactory(protocol.getName() + "-exe-", true, protocol.getThreadPriority());
		TaskQueue taskqueue = new TaskQueue();
		Executor executor = new ThreadPoolExecutor(protocol.getMinSpareThreads(), protocol.getMaxThreads(), 60, TimeUnit.SECONDS,
				taskqueue, tf);
		taskqueue.setParent((ThreadPoolExecutor) executor);
		return executor;
	}

	// --- E N H A N C E D _ F R A M E W O R K. ---

	@Bean
	@ConditionalOnMissingBean(Operator.class)
	public Operator<Enum<?>> emptyOperator() {
		return new EmptyOperator();
	}

	@Bean
	@ConditionalOnBean(Operator.class)
	public AroundAutoHandleOperatorInterceptor aroundAutoHandleOperatorInterceptor() {
		return new AroundAutoHandleOperatorInterceptor();
	}

	@Bean
	@ConditionalOnBean(AroundAutoHandleOperatorInterceptor.class)
	public PointcutAdvisor compositeOperatorAspectJExpressionPointcutAdvisor(AroundAutoHandleOperatorInterceptor advice) {
		AbstractGenericPointcutAdvisor advisor = new AbstractGenericPointcutAdvisor() {
			private static final long serialVersionUID = 1L;

			@Override
			public Pointcut getPointcut() {
				return new Pointcut() {

					@Override
					public MethodMatcher getMethodMatcher() {
						return MethodMatcher.TRUE;
					}

					@Override
					public ClassFilter getClassFilter() {
						return new ClassFilter() {
							@Override
							public boolean matches(Class<?> clazz) {
								return Operator.class.isAssignableFrom(clazz)
										&& !GenericOperatorAdapter.class.isAssignableFrom(clazz)
										&& !isAbstract(clazz.getModifiers()) && !isInterface(clazz.getModifiers());
							}
						};
					}
				};
			}
		};
		advisor.setAdvice(advice);
		return advisor;
	}

}