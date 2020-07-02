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
import java.io.IOException;
import java.util.Set;

import com.wl4g.devops.components.tools.common.resource.StreamResource;

/**
 * Strategy interface for resolving a location pattern (for example, an
 * Ant-style path pattern) into Resource objects.
 *
 * <p>
 * This is an extension to the
 * {@link com.wl4g.devops.tool.common.resources.resolver.springframework.core.io.ResourceLoader}
 * interface. A passed-in ResourceLoader (for example, an
 * {@link org.springframework.context.ApplicationContext} passed in via
 * {@link org.springframework.context.ResourceLoaderAware} when running in a
 * context) can be checked whether it implements this extended interface too.
 *
 * <p>
 * {@link ClassPathResourcePatternResolver} is a standalone implementation that
 * is usable outside an ApplicationContext, also used by
 * {@link ResourceArrayPropertyEditor} for populating Resource array bean
 * properties.
 *
 * <p>
 * Can be used with any sort of location pattern (e.g.
 * "/WEB-INF/*-context.xml"): Input patterns have to match the strategy
 * implementation. This interface just specifies the conversion method rather
 * than a specific pattern format.
 *
 * <p>
 * This interface also suggests a new resource prefix "classpath*:" for all
 * matching resources from the class path. Note that the resource location is
 * expected to be a path without placeholders in this case (e.g. "/beans.xml");
 * JAR files or classes directories can contain multiple files of the same name.
 *
 * @author Juergen Hoeller
 * @since 1.0.2
 * @see org.StreamResource.core.io.Resource
 * @see com.wl4g.devops.tool.common.resources.resolver.springframework.core.io.ResourceLoader
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 */
public interface ResourcePatternResolver extends ResourceLoader {

	/**
	 * Pseudo URL prefix for all matching resources from the class path:
	 * "classpath*:" This differs from ResourceLoader's classpath URL prefix in
	 * that it retrieves all matching resources for a given name (e.g.
	 * "/beans.xml"), for example in the root of all deployed JAR files.
	 * 
	 * @see com.wl4g.devops.tool.common.resources.resolver.springframework.core.io.ResourceLoader#CLASSPATH_URL_PREFIX
	 */
	String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

	/**
	 * Resolve the given location pattern into Resource objects.
	 * <p>
	 * Overlapping resource entries that point to the same physical resource
	 * should be avoided, as far as possible. The result should have set
	 * semantics.
	 * 
	 * @param locationPatterns
	 *            the location pattern to resolve
	 * @return the corresponding Resource objects
	 * @throws IOException
	 *             in case of I/O errors
	 */
	Set<StreamResource> getResources(String... locationPatterns) throws IOException;

}