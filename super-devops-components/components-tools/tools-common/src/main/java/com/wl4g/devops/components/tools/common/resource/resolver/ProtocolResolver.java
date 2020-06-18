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
 * A resolution strategy for protocol-specific resource handles.
 *
 * <p>
 * Used as an SPI for {@link DefaultResourceLoader}, allowing for custom
 * protocols to be handled without subclassing the loader implementation (or
 * application context implementation).
 *
 * @author Juergen Hoeller
 * @since 4.3
 * @see DefaultResourceLoader#addProtocolResolver
 */
public interface ProtocolResolver {

	/**
	 * Resolve the given location against the given resource loader if this
	 * implementation's protocol matches.
	 * 
	 * @param location
	 *            the user-specified resource location
	 * @param resourceLoader
	 *            the associated resource loader
	 * @return a corresponding {@code Resource} handle if the given location
	 *         matches this resolver's protocol, or {@code null} otherwise
	 */
	StreamResource resolve(String location, ResourceLoader resourceLoader);

}