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

import com.wl4g.devops.components.tools.common.resource.ResourceUtils2;
import com.wl4g.devops.components.tools.common.resource.StreamResource;

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

/**
 * Strategy interface for loading resources (e.. class path or file system
 * resources). An {@link org.springframework.context.ApplicationContext} is
 * required to provide this functionality, plus extended
 * {@link com.wl4g.devops.tool.common.resources.resovler.springframework.core.io.support.ResourcePatternResolver}
 * support.
 *
 * <p>
 * {@link DefaultResourceLoader} is a standalone implementation that is usable
 * outside an ApplicationContext, also used by {@link ResourceEditor}.
 *
 * <p>
 * Bean properties of type Resource and Resource array can be populated from
 * Strings when running in an ApplicationContext, using the particular context's
 * resource loading strategy.
 *
 * @author Juergen Hoeller
 * @since 10.03.2004
 * @see StreamResource
 * @see com.wl4g.devops.tool.common.resources.resovler.springframework.core.io.support.ResourcePatternResolver
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 */
public interface ResourceLoader {

	/** Pseudo URL prefix for loading from the class path: "classpath:" */
	String CLASSPATH_URL_PREFIX = ResourceUtils2.CLASSPATH_URL_PREFIX;

	/**
	 * Return a Resource handle for the specified resource location.
	 * <p>
	 * The handle should always be a reusable resource descriptor, allowing for
	 * multiple {@link StreamResource#getInputStream()} calls.
	 * <p>
	 * <ul>
	 * <li>Must support fully qualified URLs, e.g. "file:C:/test.dat".
	 * <li>Must support classpath pseudo-URLs, e.g. "classpath:test.dat".
	 * <li>Should support relative file paths, e.g. "WEB-INF/test.dat". (This
	 * will be implementation-specific, typically provided by an
	 * ApplicationContext implementation.)
	 * </ul>
	 * <p>
	 * Note that a Resource handle does not imply an existing resource; you need
	 * to invoke {@link StreamResource#exists} to check for existence.
	 * 
	 * @param location
	 *            the resource location
	 * @return a corresponding Resource handle (never {@code null})
	 * @see #CLASSPATH_URL_PREFIX
	 * @see StreamResource#exists()
	 * @see StreamResource#getInputStream()
	 */
	StreamResource getResource(String location);

	/**
	 * Expose the ClassLoader used by this ResourceLoader.
	 * <p>
	 * Clients which need to access the ClassLoader directly can do so in a
	 * uniform manner with the ResourceLoader, rather than relying on the thread
	 * context ClassLoader.
	 * 
	 * @return the ClassLoader (only {@code null} if even the system ClassLoader
	 *         isn't accessible)
	 * @see com.wl4g.devops.components.tools.common.lang.ClassUtils2.util.ClassUtils#getDefaultClassLoader()
	 */
	ClassLoader getClassLoader();

}