/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
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
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.devops.doc.plugin.swagger.springfox.plugin;

import static com.wl4g.component.common.collection.CollectionUtils2.isEmptyArray;
import static com.wl4g.component.common.collection.CollectionUtils2.safeArrayToList;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.component.common.reflect.ReflectionUtils2.findField;
import static com.wl4g.component.common.reflect.ReflectionUtils2.getField;
import static com.wl4g.component.common.reflect.ObjectInstantiators.newInstance;
import static springfox.documentation.RequestHandler.sortedPaths;
import static springfox.documentation.builders.BuilderDefaults.nullToEmptyList;
import static springfox.documentation.spi.service.contexts.Orderings.byOperationName;
import static springfox.documentation.spi.service.contexts.Orderings.byPatternsCondition;

import com.wl4g.component.common.collection.CollectionUtils2;
import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.component.core.web.versions.VersionConditionSupport;
import com.wl4g.component.core.web.versions.annotation.ApiVersionManagementWrapper;
import com.wl4g.component.core.web.versions.annotation.ApiVersionMapping;
import com.wl4g.component.core.web.versions.annotation.EnableApiVersionManagement;
import com.wl4g.component.core.web.versions.annotation.ApiVersionMappingWrapper.ApiVersionWrapper;
import com.wl4g.devops.doc.plugin.swagger.config.DocumentionHolder;
import com.wl4g.devops.doc.plugin.swagger.util.ScanReflections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;

import springfox.documentation.RequestHandler;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.service.RequestHandlerCombiner;
import springfox.documentation.spring.web.WebFluxRequestHandler;
import springfox.documentation.spring.web.WebMvcRequestHandler;
import springfox.documentation.spring.web.plugins.CombinedRequestHandler;
import springfox.documentation.spring.web.readers.operation.HandlerMethodResolver;

/**
 * This combiner can handle interface maps with the same version number and
 * different version numbers annotated by {@link ApiVersionMapping}. The
 * concrete realization logic has drawn lessons from
 * {@link springfox.documentation.spring.web.plugins.DefaultRequestHandlerCombiner}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-15
 * @sine v1.0
 * @see {@link springfox.documentation.spring.web.plugins.AbstractDocumentationPluginsBootstrapper#bootstrapDocumentationPlugins()}
 * @see {@link springfox.documentation.spring.web.plugins.AbstractDocumentationPluginsBootstrapper#buildContext()}
 * @see {@link springfox.documentation.spring.web.plugins.AbstractDocumentationPluginsBootstrapper#withDefaults()}
 */
public class ApiVersionPathsRequestHandlerCombiner implements RequestHandlerCombiner {

	protected final SmartLogger log = getLogger(getClass());

	/**
	 * Obtain target project spring application annotation of
	 * {@link EnableApiVersionManagement} metadata properties.
	 */
	private ApiVersionManagementWrapper metadataWrapper;

	@Autowired
	private ConfigurableEnvironment environment;

	@Override
	public List<RequestHandler> combine(List<RequestHandler> source) {
		List<RequestHandler> combined = new ArrayList<RequestHandler>();
		Map<String, List<RequestHandler>> byPath = new HashMap<>();
		log.debug("Combining total number of request handlers {}", nullToEmptyList(source).size());

		for (RequestHandler handler : nullToEmptyList(source)) {
			String pathKey = sortedPaths(handler.getPatternsCondition());

			// Gets (webflux|webmvc) request mapping info.
			Object mappingObj = getField(findField(handler.getClass(), "requestMapping"), handler, true);

			// Find custom condition.
			VersionConditionSupport customCondition = null;
			if (mappingObj instanceof RequestMappingInfo) { // Webmvc
				RequestMappingInfo mapping = (RequestMappingInfo) mappingObj;
				if (mapping.getCustomCondition() instanceof VersionConditionSupport) {
					customCondition = (VersionConditionSupport) mapping.getCustomCondition();
				}
			} else if (mappingObj instanceof org.springframework.web.reactive.result.method.RequestMappingInfo) { // Webflux
				org.springframework.web.reactive.result.method.RequestMappingInfo mapping = (org.springframework.web.reactive.result.method.RequestMappingInfo) mappingObj;
				if (mapping.getCustomCondition() instanceof VersionConditionSupport) {
					customCondition = (VersionConditionSupport) mapping.getCustomCondition();
				}
			} else { // otherwise, by defaults
				addRequestHandler(byPath, pathKey, handler);
				continue;
			}

			// Not enable API versions managment?
			// Not managed by API version?
			if (isNull(customCondition)) {
				addRequestHandler(byPath, pathKey, handler); // by-default
			} else {
				// Each add version info to mapping.
				ApiVersionManagementWrapper versionConfig = obtainScanningApiVersionManagementWrapper(customCondition);
				for (ApiVersionWrapper ver : customCondition.getVersionMapping().getApiVersions()) {
					if (isEmptyArray(ver.getGroups())) {
						addAllRequestHandlersWithApiVersionParams(versionConfig, null, ver.getValue(), byPath, pathKey, handler);
					} else {
						for (String versionGroup : ver.getGroups()) {
							addAllRequestHandlersWithApiVersionParams(versionConfig, versionGroup, ver.getValue(), byPath,
									pathKey, handler);
						}
					}
				}
			}
		}

		for (String key : byPath.keySet()) {
			combined.addAll(combined(byPath.get(key)));
		}
		log.debug("Combined number of request handlers {}", combined.size());

		return combined.stream().sorted(byPatternsCondition()).collect(toList());
	}

	private void addRequestHandler(Map<String, List<RequestHandler>> byPath, String pathKey, RequestHandler handler) {
		log.debug("Adding key: {}, {}", pathKey, handler.toString());
		List<RequestHandler> handlers = byPath.get(pathKey);
		if (isNull(handlers)) {
			byPath.put(pathKey, (handlers = new ArrayList<>(4)));
		}
		handlers.add(handler);
	}

	/**
	 * Add all request mapping handlers that meet API version management (will
	 * be grouped by version parameter names). </br>
	 * </br>
	 * 
	 * for example (RequestHandler1): GET
	 * /user/getUserInfo?_v=1.0.1&platform=ios </br>
	 * </br>
	 * for example (RequestHandler2): GET
	 * /user/getUserInfo?_v=1.2.1&platform=android </br>
	 * 
	 * @param versionConfig
	 * @param versionGroup
	 * @param version
	 * @param byPath
	 * @param pathKey
	 * @param handler
	 */
	private void addAllRequestHandlersWithApiVersionParams(ApiVersionManagementWrapper versionConfig, String versionGroup,
			String version, Map<String, List<RequestHandler>> byPath, String pathKey, RequestHandler handler) {
		for (String groupParam : versionConfig.getGroupParams()) {
			for (String versionParam : versionConfig.getVersionParams()) {
				addRequestHandler(byPath, pathKey,
						transformRequestHandler(handler, groupParam, versionParam, versionGroup, version));
			}
		}
	}

	/**
	 * Clone and transform the request mapping, add API version information as a
	 * parameter to the path pattern.
	 * 
	 * @param versionConfig
	 * @param handler
	 * @param versionGroup
	 * @param version
	 * @return
	 */
	private RequestHandler transformRequestHandler(RequestHandler handler, String groupParam, String versionParam,
			String versionGroup, String version) {
		if (handler instanceof WebMvcRequestHandler) {
			WebMvcRequestHandler mvcHandler = (WebMvcRequestHandler) handler;
			String contextPath = (String) getField(findField(handler.getClass(), "contextPath"), mvcHandler, true);
			HandlerMethodResolver resolver = getField(findField(handler.getClass(), "methodResolver"), mvcHandler, true);
			RequestMappingInfo mapping = (RequestMappingInfo) mvcHandler.getRequestMapping().getOriginalInfo();

			// Add apiVersion info to request mapping path pattern.
			final String addonParams = generateApiVersionParamString(groupParam, versionParam, versionGroup, version);

			// Cloning new request mapping info
			PatternsRequestCondition patternCondition = mapping.getPatternsCondition();
			String[] newPatterns = patternCondition.getPatterns().stream().map(p -> (p + addonParams)).toArray(String[]::new);
			PatternsRequestCondition newPatternCondition = new PatternsRequestCondition(newPatterns);
			RequestMappingInfo newMapping = new RequestMappingInfo(newPatternCondition, mapping.getMethodsCondition(),
					mapping.getParamsCondition(), mapping.getHeadersCondition(), mapping.getConsumesCondition(),
					mapping.getProducesCondition(), mapping.getCustomCondition());

			return new WebMvcRequestHandler(contextPath, resolver, newMapping, mvcHandler.getHandlerMethod());
		} else if (handler instanceof WebFluxRequestHandler) { // Webflux
			WebFluxRequestHandler webfluxHandler = (WebFluxRequestHandler) handler;
			springfox.documentation.spring.web.readers.operation.HandlerMethodResolver resolver = getField(
					findField(handler.getClass(), "methodResolver"), webfluxHandler, true);
			org.springframework.web.reactive.result.method.RequestMappingInfo origMapping = (org.springframework.web.reactive.result.method.RequestMappingInfo) webfluxHandler
					.getRequestMapping().getOriginalInfo();

			// Add apiVersion info to request mapping path pattern.
			final String addonParams = generateApiVersionParamString(groupParam, versionParam, versionGroup, version);

			// Cloning new request mapping info
			org.springframework.web.reactive.result.condition.PatternsRequestCondition patternCondition = origMapping
					.getPatternsCondition();

			PathPattern[] patterns = patternCondition.getPatterns().stream().map(p -> defaultParser.parse(p + addonParams))
					.toArray(PathPattern[]::new);
			org.springframework.web.reactive.result.condition.PatternsRequestCondition newPatternCondition = new org.springframework.web.reactive.result.condition.PatternsRequestCondition(
					patterns);
			org.springframework.web.reactive.result.method.RequestMappingInfo newMapping = new org.springframework.web.reactive.result.method.RequestMappingInfo(
					newPatternCondition, origMapping.getMethodsCondition(), origMapping.getParamsCondition(),
					origMapping.getHeadersCondition(), origMapping.getConsumesCondition(), origMapping.getProducesCondition(),
					origMapping.getCustomCondition());

			return new WebFluxRequestHandler(resolver, newMapping, webfluxHandler.getHandlerMethod());
		}

		// otherwise, no supportes api version same path.
		return handler;
	}

	/**
	 * Generate API version information as URI parameter string.
	 * 
	 * @param groupParam
	 * @param versionParam
	 * @param versionGroup
	 * @param version
	 * @return
	 */
	private String generateApiVersionParamString(String groupParam, String versionParam, String versionGroup, String version) {
		String versionInfo = "?".concat(versionParam).concat("=").concat(version);
		if (!isBlank(versionGroup)) {
			versionInfo = versionInfo.concat("&".concat(groupParam).concat("=").concat(versionGroup));
		}
		return versionInfo;
	}

	/**
	 * Scanning obtain {@link EnableApiVersionManagement} annotation metadata
	 * configuration properties. If no obtained by scanning, fackball the
	 * default properties of {@link EmbeddedSpringfoxBootstrap} is used.
	 * 
	 * @param customCondition
	 * @return
	 */
	private ApiVersionManagementWrapper obtainScanningApiVersionManagementWrapper(VersionConditionSupport customCondition) {
		if (nonNull(metadataWrapper)) {
			return metadataWrapper;
		}

		// First, you should scan the annotations of the spring application
		// startup class that uses the target project.
		Set<String> resourcePackages = new HashSet<>(DocumentionHolder.get().getResourcePackages());
		resourcePackages = resourcePackages.stream().map(r -> r.substring(0, r.lastIndexOf("."))).collect(toSet());

		Reflections reflections = ScanReflections.createDefaultResourceReflections(resourcePackages);
		Set<Class<?>> annos = reflections.getTypesAnnotatedWith(EnableApiVersionManagement.class);
		if (!CollectionUtils2.isEmpty(annos)) {
			Class<?> configClass = annos.iterator().next();
			EnableApiVersionManagement mgt = configClass.getAnnotation(EnableApiVersionManagement.class);

			/**
			 * [Notes]: If the configuration file for the target project is not
			 * in the default directory, it is possible that it cannot be loaded
			 * into the spring environment, and therefore the placeholder cannot
			 * be resolved.
			 */
			String[] versionParams = safeArrayToList(mgt.versionParams()).stream().filter(g -> !isBlank(g))
					.map(g -> environment.resolvePlaceholders(g)).toArray(String[]::new);
			String[] groupParams = safeArrayToList(mgt.groupParams()).stream().filter(g -> !isBlank(g))
					.map(g -> environment.resolvePlaceholders(g)).toArray(String[]::new);

			return (metadataWrapper = new ApiVersionManagementWrapper(mgt.sensitiveParams(), versionParams, groupParams,
					newInstance(mgt.versionComparator())));
		}

		/**
		 * Fallback, use default configuration. refer to
		 * {@link EmbeddedSpringfoxBootstrap}
		 */
		return (metadataWrapper = customCondition.getVersionMapping().getVersionConfig());
	}

	private Collection<RequestHandler> combined(Collection<RequestHandler> requestHandlers) {
		List<RequestHandler> source = new ArrayList<>(requestHandlers);
		if (source.size() == 0 || source.size() == 1) {
			return requestHandlers;
		}

		Map<PathAndParametersEquivalence.Wrapper, List<RequestHandler>> groupByEquality = safeGroupBy(source);
		List<RequestHandler> combined = new ArrayList<>();
		groupByEquality.keySet().stream().sorted(wrapperComparator()).forEachOrdered(path -> {
			List<RequestHandler> handlers = groupByEquality.get(path);
			RequestHandler toCombine = path.get();
			if (handlers.size() > 1) {
				for (RequestHandler each : handlers) {
					if (each.equals(toCombine)) {
						continue;
					}
					// noinspection ConstantConditions
					log.debug("Combining {} and {}", toCombine.toString(), each.toString());
					toCombine = combine(toCombine, each);
				}
			}
			combined.add(toCombine);
		});
		return combined;
	}

	private Comparator<PathAndParametersEquivalence.Wrapper> wrapperComparator() {
		return (first, second) -> byPatternsCondition().thenComparing(byOperationName()).compare(first.get(), second.get());
	}

	private Map<PathAndParametersEquivalence.Wrapper, List<RequestHandler>> safeGroupBy(List<RequestHandler> source) {
		try {
			return source.stream().collect(groupingBy(EQUIVALENCE::wrap, LinkedHashMap::new, toList()));
		} catch (Exception e) {
			log.error("Unable to index request handlers {}. Request handlers with issues{}", e.getMessage(), keys(source));
			return Collections.emptyMap();
		}
	}

	private String keys(List<RequestHandler> source) {
		final StringBuilder sb = new StringBuilder("Request Handlers with duplicate keys {");
		for (int i = 0; i < source.size(); i++) {
			sb.append('\t').append(i).append(". ").append(source.get(i).key());
		}
		sb.append('}');
		return sb.toString();
	}

	private RequestHandler combine(RequestHandler first, RequestHandler second) {
		if (first.compareTo(second) < 0) {
			return new CombinedRequestHandler(first, second);
		}
		return new CombinedRequestHandler(second, first);
	}

	private static final PathAndParametersEquivalence EQUIVALENCE = new PathAndParametersEquivalence();

	static class PathAndParametersEquivalence implements BiPredicate<RequestHandler, RequestHandler> {
		private static final ResolvedMethodParameterEquivalence RESOLVED_METHOD_PARAMETER_EQUIVALENCE = new ResolvedMethodParameterEquivalence();

		public boolean test(RequestHandler a, RequestHandler b) {
			return a.getPatternsCondition().equals(b.getPatternsCondition())
					&& a.supportedMethods().stream().anyMatch(item -> b.supportedMethods().contains(item))
					&& a.params().equals(b.params()) && Objects.equals(wrapped(a.getParameters()), wrapped(b.getParameters()));
		}

		private Set<ResolvedMethodParameterEquivalence.Wrapper> wrapped(List<ResolvedMethodParameter> parameters) {
			return parameters.stream().map(RESOLVED_METHOD_PARAMETER_EQUIVALENCE::wrap).collect(toSet());
		}

		public int doHash(RequestHandler requestHandler) {
			return Objects.hash(requestHandler.getPatternsCondition().getPatterns(), requestHandler.supportedMethods(),
					requestHandler.params(), wrapped(requestHandler.getParameters()));
		}

		Wrapper wrap(RequestHandler input) {
			return new Wrapper(input, this);
		}

		public static class Wrapper {
			private final RequestHandler requestHandler;
			private final PathAndParametersEquivalence equivalence;

			Wrapper(RequestHandler requestHandler, PathAndParametersEquivalence equivalence) {
				this.requestHandler = requestHandler;
				this.equivalence = equivalence;
			}

			@Override
			public int hashCode() {
				return equivalence.doHash(requestHandler);
			}

			@Override
			public boolean equals(Object o) {
				if (this == o) {
					return true;
				}
				if (o == null || getClass() != o.getClass()) {
					return false;
				}
				Wrapper wrapper = (Wrapper) o;
				return Objects.equals(equivalence, wrapper.equivalence)
						&& equivalence.test(requestHandler, wrapper.requestHandler);
			}

			public RequestHandler get() {
				return requestHandler;
			}
		}
	}

	static class ResolvedMethodParameterEquivalence implements BiPredicate<ResolvedMethodParameter, ResolvedMethodParameter> {
		@Override
		public boolean test(ResolvedMethodParameter a, ResolvedMethodParameter b) {
			return Objects.equals(a.defaultName(), b.defaultName())
					&& Objects.equals(a.getParameterIndex(), b.getParameterIndex())
					&& Objects.equals(a.getParameterType(), b.getParameterType());
		}

		public int doHash(ResolvedMethodParameter self) {
			return Objects.hash(self.defaultName(), self.getParameterIndex(), self.getParameterType());
		}

		Wrapper wrap(ResolvedMethodParameter input) {
			return new Wrapper(input, this);
		}

		public class Wrapper {
			private final ResolvedMethodParameter parameter;
			private final ResolvedMethodParameterEquivalence equivalence;

			Wrapper(ResolvedMethodParameter parameter, ResolvedMethodParameterEquivalence equivalence) {
				this.parameter = parameter;
				this.equivalence = equivalence;
			}

			@Override
			public int hashCode() {
				return equivalence.doHash(parameter);
			}

			@Override
			public boolean equals(Object o) {
				if (this == o) {
					return true;
				}
				if (o == null || getClass() != o.getClass()) {
					return false;
				}
				Wrapper wrapper = (Wrapper) o;
				return Objects.equals(equivalence, wrapper.equivalence) && equivalence.test(parameter, wrapper.parameter);
			}
		}
	}

	private static final PathPatternParser defaultParser = new PathPatternParser();

}
