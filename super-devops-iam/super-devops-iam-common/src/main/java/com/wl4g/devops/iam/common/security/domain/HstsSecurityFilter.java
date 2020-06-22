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
package com.wl4g.devops.iam.common.security.domain;

import static com.wl4g.devops.components.tools.common.collection.Collections2.safeList;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;

/**
 * HTTP strict transport security filter
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年6月20日 v1.0.0
 * @see
 */
public class HstsSecurityFilter extends OncePerRequestFilter {

	@Autowired
	protected AbstractIamProperties<ParamProperties> config;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// Http strict transport security:
		Optional<String> hstsOpt = safeList(config.getDomain().getHstsProfilesActive()).stream()
				.filter(a -> equalsIgnoreCase(a, config.getSpringApplicationName())).findAny();
		if (hstsOpt.isPresent()) {
			if (!response.containsHeader("Strict-Transport-Security")) {
				response.addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
				response.addHeader("Strict-Transport-Security", "max-age=0");
			}
		}

		// X-Download-Options:
		// https://stackoverflow.com/questions/15299325/x-download-options-noopen-equivalent
		response.setHeader("X-Download-Options", "noopen");

	}

}
