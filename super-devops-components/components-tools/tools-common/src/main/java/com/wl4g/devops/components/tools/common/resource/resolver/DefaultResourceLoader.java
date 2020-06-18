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
package com.wl4g.devops.components.tools.common.resource.resolver;

/**
 * Retention of upstream license agreement statement:</br>
 * Thank you very much spring framework, We fully comply with and support the open license
 * agreement of spring. The purpose of migration is to solve the problem
 * that these elegant API programs can still be easily used without running
 * in the spring environment.
 * </br>
 * Copyright 2002-2017 the original author or authors.
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.wl4g.devops.components.tools.common.lang.Assert2;
import com.wl4g.devops.components.tools.common.lang.ClassUtils2;
import com.wl4g.devops.components.tools.common.lang.StringUtils2;
import com.wl4g.devops.components.tools.common.resource.ClassPathStreamResource;
import com.wl4g.devops.components.tools.common.resource.StreamResource;
import com.wl4g.devops.components.tools.common.resource.UrlStreamResource;

/**
 * Default implementation of the {@link ResourceLoader} interface. Used by
 * {@link ResourceEditor}, and serves as base class for
 * {@link org.springframework.context.support.AbstractApplicationContext}. Can
 * also be used standalone.
 *
 * <p>
 * Will return a {@link UrlStreamResource} if the location value is a URL, and a
 * {@link ClassPathStreamResource} if it is a non-URL path or a "classpath:"
 * pseudo-URL.
 *
 * @author Juergen Hoeller
 * @since 10.03.2004
 * @see FileSystemResourceLoader
 * @see org.springframework.context.support.ClassPathXmlApplicationContext
 */
public class DefaultResourceLoader implements ResourceLoader {

	private ClassLoader classLoader;

	private final Set<ProtocolResolver> protocolResolvers = new LinkedHashSet<ProtocolResolver>(4);

	/**
	 * Create a new DefaultResourceLoader.
	 * <p>
	 * ClassLoader access will happen using the thread context class loader at
	 * the time of this ResourceLoader's initialization.
	 * 
	 * @see java.lang.Thread#getContextClassLoader()
	 */
	public DefaultResourceLoader() {
		this.classLoader = ClassUtils2.getDefaultClassLoader();
	}

	/**
	 * Create a new DefaultResourceLoader.
	 * 
	 * @param classLoader
	 *            the ClassLoader to load class path resources with, or
	 *            {@code null} for using the thread context class loader at the
	 *            time of actual resource access
	 */
	public DefaultResourceLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Specify the ClassLoader to load class path resources with, or
	 * {@code null} for using the thread context class loader at the time of
	 * actual resource access.
	 * <p>
	 * The default is that ClassLoader access will happen using the thread
	 * context class loader at the time of this ResourceLoader's initialization.
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Return the ClassLoader to load class path resources with.
	 * <p>
	 * Will get passed to ClassPathResource's constructor for all
	 * ClassPathResource objects created by this resource loader.
	 * 
	 * @see ClassPathStreamResource
	 */
	@Override
	public ClassLoader getClassLoader() {
		return (this.classLoader != null ? this.classLoader : ClassUtils2.getDefaultClassLoader());
	}

	/**
	 * Register the given resolver with this resource loader, allowing for
	 * additional protocols to be handled.
	 * <p>
	 * Any such resolver will be invoked ahead of this loader's standard
	 * resolution rules. It may therefore also override any default rules.
	 * 
	 * @since 4.3
	 * @see #getProtocolResolvers()
	 */
	public void addProtocolResolver(ProtocolResolver resolver) {
		Assert2.notNull(resolver, "ProtocolResolver must not be null");
		this.protocolResolvers.add(resolver);
	}

	/**
	 * Return the collection of currently registered protocol resolvers,
	 * allowing for introspection as well as modification.
	 * 
	 * @since 4.3
	 */
	public Collection<ProtocolResolver> getProtocolResolvers() {
		return this.protocolResolvers;
	}

	@Override
	public StreamResource getResource(String location) {
		Assert2.notNull(location, "Location must not be null");

		for (ProtocolResolver protocolResolver : this.protocolResolvers) {
			StreamResource resource = protocolResolver.resolve(location, this);
			if (resource != null) {
				return resource;
			}
		}

		if (location.startsWith("/")) {
			return getResourceByPath(location);
		} else if (location.startsWith(CLASSPATH_URL_PREFIX)) {
			return new ClassPathStreamResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
		} else {
			try {
				// Try to parse the location as a URL...
				URL url = new URL(location);
				return new UrlStreamResource(url);
			} catch (MalformedURLException ex) {
				// No URL -> resolve as resource path.
				return getResourceByPath(location);
			}
		}
	}

	/**
	 * Return a Resource handle for the resource at the given path.
	 * <p>
	 * The default implementation supports class path locations. This should be
	 * appropriate for standalone implementations but can be overridden, e.g.
	 * for implementations targeted at a Servlet container.
	 * 
	 * @param path
	 *            the path to the resource
	 * @return the corresponding Resource handle
	 * @see ClassPathStreamResource
	 * @see org.springframework.context.support.FileSystemXmlApplicationContext#getResourceByPath
	 * @see org.springframework.web.context.support.XmlWebApplicationContext#getResourceByPath
	 */
	protected StreamResource getResourceByPath(String path) {
		return new ContextClassPathStreamResource(path, getClassLoader());
	}

	/**
	 * ClassPathResource that explicitly expresses a context-relative path
	 * through implementing the ContextResource interface.
	 */
	protected static class ContextClassPathStreamResource extends ClassPathStreamResource {

		public ContextClassPathStreamResource(String path, ClassLoader classLoader) {
			super(path, classLoader);
		}

		public String getPathWithinContext() {
			return getPath();
		}

		@Override
		public StreamResource createRelative(String relativePath) {
			String pathToUse = StringUtils2.applyRelativePath(getPath(), relativePath);
			return new ContextClassPathStreamResource(pathToUse, getClassLoader());
		}

	}

}