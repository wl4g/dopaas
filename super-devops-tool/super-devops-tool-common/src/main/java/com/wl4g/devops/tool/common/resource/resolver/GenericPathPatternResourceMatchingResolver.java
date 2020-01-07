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
package com.wl4g.devops.tool.common.resource.resolver;

import static java.util.stream.Collectors.toCollection;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import com.wl4g.devops.tool.common.lang.Assert2;
import com.wl4g.devops.tool.common.resource.StreamResource;

/**
 * Enhanced path pattern resource matching resolver, support multiple location
 * matching patterns.
 * 
 * @author wanglsir@gmail.com, 983708408@qq.com
 * @version 2019年12月26日 v1.0.0
 * @see
 */
public class GenericPathPatternResourceMatchingResolver extends PathPatternResourceMatchingResolver {

	/**
	 * Create a new PathMatchingResourcePatternResolver with a
	 * DefaultResourceLoader.
	 * <p>
	 * ClassLoader access will happen via the thread context class loader.
	 * 
	 * @see com.wl4g.devops.tool.common.resources.resolver.springframework.core.io.DefaultResourceLoader
	 */
	public GenericPathPatternResourceMatchingResolver() {
		super();
	}

	/**
	 * Create a new PathMatchingResourcePatternResolver.
	 * <p>
	 * ClassLoader access will happen via the thread context class loader.
	 * 
	 * @param resourceLoader
	 *            the ResourceLoader to load root directories and actual
	 *            resources with
	 */
	public GenericPathPatternResourceMatchingResolver(ResourceLoader resourceLoader) {
		super(resourceLoader);
	}

	/**
	 * Create a new PathMatchingResourcePatternResolver with a
	 * DefaultResourceLoader.
	 * 
	 * @param classLoader
	 *            the ClassLoader to load classpath resources with, or
	 *            {@code null} for using the thread context class loader at the
	 *            time of actual resource access
	 * @see com.wl4g.devops.tool.common.resources.resolver.springframework.core.io.DefaultResourceLoader
	 */
	public GenericPathPatternResourceMatchingResolver(ClassLoader classLoader) {
		super(classLoader);
	}

	@Override
	public StreamResource getResource(String location) {
		return super.getResource(location);
	}

	public Set<StreamResource> getResources(String... locationPatterns) throws IOException {
		Assert2.notNull(locationPatterns, "Path locationPatterns can't null");
		return Arrays.asList(locationPatterns).stream().map(pattern -> {
			try {
				return super.getResources(pattern);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}).flatMap(rss -> rss.stream()).collect(toCollection(LinkedHashSet::new));
	}

}