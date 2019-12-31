package com.wl4g.devops.tool.common.resource;

import com.wl4g.devops.tool.common.resource.resolver.ResourceLoader;

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
 * {@link org.ContextStreamResource.io.ContextResource}
 * 
 * Extended interface for a resource that is loaded from an enclosing 'context',
 * e.g. from a {@link javax.servlet.ServletContext} or a
 * {@link javax.portlet.PortletContext} but also from plain classpath paths or
 * relative file system paths (specified without an explicit prefix, hence
 * applying relative to the local {@link ResourceLoader}'s context).
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.web.context.support.ServletContextResource
 * @see org.springframework.web.portlet.context.PortletContextResource
 */
public interface ContextStreamResource extends StreamResource {

	/**
	 * Return the path within the enclosing 'context'.
	 * <p>
	 * This is typically path relative to a context-specific root directory,
	 * e.g. a ServletContext root or a PortletContext root.
	 */
	String getPathWithinContext();

}
