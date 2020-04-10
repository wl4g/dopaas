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
package com.wl4g.devops.iam.common.security.cipher;

import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * Perform automatic decryption decoding filtering for encrypted parameters.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月28日 v1.0.0
 * @see
 */
public final class CipherRequestSecurityFilter extends OncePerRequestFilter {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * {@link AbstractIamProperties}
	 */
	final protected AbstractIamProperties<? extends ParamProperties> config;

	/**
	 * {@link CipherRequestWrapperFactory}
	 */
	final protected CipherRequestWrapperFactory factory;

	public CipherRequestSecurityFilter(AbstractIamProperties<? extends ParamProperties> config,
			CipherRequestWrapperFactory factory) {
		notNullOf(config, "config");
		notNullOf(factory, "factory");
		this.config = config;
		this.factory = factory;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (config.getCipher().isEnableDataCipher()) { // Enabled dataCipher?
			// If it is an encrypted parameter request, it is wrapped as a
			// decryptable request.
			filterChain.doFilter(factory.newRequestWrapper(config, request), response);
		} else {
			filterChain.doFilter(request, response);
		}

	}

}