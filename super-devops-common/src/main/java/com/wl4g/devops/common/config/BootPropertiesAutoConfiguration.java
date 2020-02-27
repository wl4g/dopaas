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

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.apache.coyote.AbstractProtocol;
import org.apache.coyote.ProtocolHandler;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.TaskThreadFactory;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import com.wl4g.devops.common.logging.TraceLoggingMDCFilter;
import com.wl4g.devops.common.web.RespBase.ErrorPromptMessageBuilder;

/**
 * System properties auto configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年2月20日
 * @since
 */
@Configuration
public class BootPropertiesAutoConfiguration implements EnvironmentAware {

	/**
	 * API prompt max length.
	 */
	final private static int PROMPT_MAX_LEN = 4;

	final protected Logger log = getLogger(getClass());

	@Override
	public void setEnvironment(Environment environment) {
		initBootProperties(environment);
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

}