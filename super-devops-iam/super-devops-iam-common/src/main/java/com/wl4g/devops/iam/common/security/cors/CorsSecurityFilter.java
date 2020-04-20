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
package com.wl4g.devops.iam.common.security.cors;

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.tool.common.web.WebUtils2.getFullRequestURL;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.filter.CorsFilter;

import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * CORS(CSRF attack) resolve filter
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月25日
 * @since
 */
public final class CorsSecurityFilter extends CorsFilter {

	public CorsSecurityFilter(CorsConfigurationSource configSource) {
		super(configSource);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// Only Internet Explorer supports(P3P)? The authentication mechanism
		// has been upgraded to non cookie form, reference:
		// /default-webapps/sdk/{version}/iam-jssdk-form.html

		// response.addHeader("P3P", "CP='CURa ADMa DEVa PSAo PSDo OUR BUS UNI
		// PUR INT DEM STA PRE COM NAV OTC NOI DSP COR'");
		// response.addHeader("Set-Cookie", "HttpOnly;Secure;SameSite=None");
		super.doFilterInternal(request, response, filterChain);
	}

	/**
	 * Advanced matches CORS processor.
	 *
	 * @author Wangl.sir
	 * @version v1.0 2019年8月21日
	 * @since
	 */
	public static class AdvancedCorsProcessor extends DefaultCorsProcessor {

		final protected SmartLogger log = getLogger(getClass());

		@Override
		public boolean processRequest(CorsConfiguration config, HttpServletRequest request, HttpServletResponse response)
				throws IOException {
			// CORS check processing.
			final boolean corsAllowed = super.processRequest(config, request, response);
			if (!corsAllowed && log.isWarnEnabled()) {
				log.warn("Rejected cors request of URL: '{}'", (request.getMethod() + " " + getFullRequestURL(request)));
			}
			return corsAllowed;
		}

	}

}