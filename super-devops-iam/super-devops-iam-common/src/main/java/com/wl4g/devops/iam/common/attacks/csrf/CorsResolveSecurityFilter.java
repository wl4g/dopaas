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
package com.wl4g.devops.iam.common.attacks.csrf;

import static com.wl4g.devops.tool.common.utils.web.WebUtils2.getFullRequestURL;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS(CSRF attack) resolve filter
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月25日
 * @since
 */
public class CorsResolveSecurityFilter extends CorsFilter {

	public CorsResolveSecurityFilter(CorsConfigurationSource configSource) {
		super(configSource);
	}

	/**
	 * Advanced matches CORS processor.
	 *
	 * @author Wangl.sir
	 * @version v1.0 2019年8月21日
	 * @since
	 */
	public static class AdvancedCorsProcessor extends DefaultCorsProcessor {

		final protected Logger log = LoggerFactory.getLogger(getClass());

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