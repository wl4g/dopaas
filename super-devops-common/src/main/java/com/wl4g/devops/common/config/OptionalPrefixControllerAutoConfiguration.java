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

import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static org.springframework.util.Assert.notNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Abstract optional prefix request mapping controller configuration.
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月10日
 * @since
 */
public abstract class OptionalPrefixControllerAutoConfiguration implements ApplicationContextAware {

	/**
	 * {@link ApplicationContext}
	 */
	protected ApplicationContext actx;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		notNull(applicationContext, "'applicationContext' must not be null");
		this.actx = applicationContext;
	}

	/**
	 * New create prefix handler mapping.
	 * 
	 * @param mappingPrefix
	 * @param handlers
	 * @return
	 */
	protected PrefixHandlerMapping newPrefixHandlerMapping(@NotBlank String mappingPrefix, @NotNull Object... handlers) {
		hasTextOf(mappingPrefix, "mappingPrefix");
		notNullOf(handlers, "handlers");

		PrefixHandlerMapping mapping = new PrefixHandlerMapping(handlers);
		mapping.setPrefix(mappingPrefix);
		return mapping;
	}

	/**
	 * New create prefix handler mapping.
	 * 
	 * @param mappingPrefix
	 * @param annotationClass
	 * @return
	 */
	protected PrefixHandlerMapping newPrefixHandlerMapping(@NotBlank String mappingPrefix,
			@NotNull Class<? extends Annotation> annotationClass) {
		hasTextOf(mappingPrefix, "mappingPrefix");
		notNullOf(annotationClass, "annotationClass");

		Map<String, Object> beans = actx.getBeansWithAnnotation(annotationClass);
		PrefixHandlerMapping mapping = new PrefixHandlerMapping(beans.values().toArray(new Object[beans.size()]));
		mapping.setPrefix(mappingPrefix);
		return mapping;
	}

	/**
	 * {@link HandlerMapping} to map {@code @RequestMapping} on objects and
	 * prefixes them. The semantics of {@code @RequestMapping} should be
	 * identical to a normal {@code @Controller}, but the Objects should not be
	 * annotated as {@code @Controller} (otherwise they will be mapped by the
	 * normal MVC mechanisms).
	 *
	 * @author Johannes Edmeier
	 */
	public static class PrefixHandlerMapping extends RequestMappingHandlerMapping {
		private String prefix = "";
		private final Object handlers[];

		public PrefixHandlerMapping(Object... handlers) {
			this.handlers = handlers.clone();
			setOrder(-50);
		}

		@Override
		public void afterPropertiesSet() {
			super.afterPropertiesSet();
			for (Object handler : handlers) {
				detectHandlerMethods(handler);
			}
		}

		@Override
		protected boolean isHandler(Class<?> beanType) {
			return false;
		}

		@Override
		protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping) {
			if (mapping == null) {
				return;
			}
			super.registerHandlerMethod(handler, method, withPrefix(mapping));
		}

		private RequestMappingInfo withPrefix(RequestMappingInfo mapping) {
			List<String> newPatterns = getPatterns(mapping);

			PatternsRequestCondition patterns = new PatternsRequestCondition(newPatterns.toArray(new String[newPatterns.size()]));
			return new RequestMappingInfo(patterns, mapping.getMethodsCondition(), mapping.getParamsCondition(),
					mapping.getHeadersCondition(), mapping.getConsumesCondition(), mapping.getProducesCondition(),
					mapping.getCustomCondition());
		}

		private List<String> getPatterns(RequestMappingInfo mapping) {
			List<String> newPatterns = new ArrayList<String>(mapping.getPatternsCondition().getPatterns().size());
			for (String pattern : mapping.getPatternsCondition().getPatterns()) {
				newPatterns.add(prefix + pattern);
			}
			return newPatterns;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getPrefix() {
			return prefix;
		}

	}

}