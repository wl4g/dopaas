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
package com.wl4g.devops.components.tools.common.remoting.uri;

import java.net.URI;
import java.util.Map;

/**
 * Defines methods for expanding a URI template with variables.
 */
public interface UriTemplateHandler {

	/**
	 * Expand the given URI template with a map of URI variables.
	 * 
	 * @param uriTemplate
	 *            the URI template
	 * @param uriVariables
	 *            variable values
	 * @return the created URI instance
	 */
	URI expand(String uriTemplate, Map<String, ?> uriVariables);

	/**
	 * Expand the given URI template with an array of URI variables.
	 * 
	 * @param uriTemplate
	 *            the URI template
	 * @param uriVariables
	 *            variable values
	 * @return the created URI instance
	 */
	URI expand(String uriTemplate, Object... uriVariables);

}