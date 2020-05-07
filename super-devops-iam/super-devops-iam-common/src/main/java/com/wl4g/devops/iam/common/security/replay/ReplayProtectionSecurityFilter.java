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
package com.wl4g.devops.iam.common.security.replay;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.lang.Math.abs;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.apache.shiro.web.util.WebUtils.getPathWithinApplication;
import static org.apache.shiro.web.util.WebUtils.toHttp;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.util.AntPathMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wl4g.devops.iam.common.config.ReplayProperties;
import com.wl4g.devops.tool.common.log.SmartLogger;
import static org.apache.commons.codec.binary.Hex.*;
import static com.wl4g.devops.tool.common.crypto.digest.DigestUtils2.*;
import static com.wl4g.devops.tool.common.web.CookieUtils.*;

/**
 * Replay attacks request protection security filter.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月7日
 * @since
 */
public final class ReplayProtectionSecurityFilter extends OncePerRequestFilter {

	protected SmartLogger log = getLogger(getClass());

	@Autowired
	private ReplayProperties rconfig;

	/**
	 * Specifies a {@link ReplayMatcher} that is used to determine if XSRF
	 * protection should be applied. If the {@link ReplayMatcher} returns true
	 * for a given request, then XSRF protection is applied.
	 *
	 * <p>
	 * The default is to apply XSRF protection for any HTTP method other than
	 * GET, HEAD, TRACE, OPTIONS.
	 * </p>
	 *
	 * @param xsrfMatcher
	 *            the {@link ReplayMatcher} used to determine if XSRF protection
	 *            should be applied.
	 */
	@Autowired
	private ReplayMatcher replayProtectMatcher;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String requestPath = getPathWithinApplication(toHttp(request));

		// Ignore non replay request methods.
		if (!replayProtectMatcher.matches(request)) {
			log.debug("Skip replay protection of: {}", requestPath);
			filterChain.doFilter(request, response);
			return;
		}
		// Ignore exclude URLs XSRF validation.
		for (String pattern : rconfig.getExcludeValidReplayMapping()) {
			if (defaultExcludeReplayMatcher.matchStart(pattern, requestPath)) {
				log.debug("Skip exclude url replay valid '{}'", requestPath);
				filterChain.doFilter(request, response);
				return;
			}
		}

		// Replay token validation
		ReplayToken replayToken = getRequestReplayToken(request, response);

		// Assertion replay token.
		try {
			assertReplayTokenValidity(replayToken, request, response);
		} catch (InvalidReplayTokenException e) {
			throw new InvalidReplayTokenException(format("Invalid replay token found for: {}", requestPath), e);
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * Assertion request replay token validity.
	 * 
	 * @param replayToken
	 * @param request
	 * @param response
	 * @return
	 */
	protected void assertReplayTokenValidity(ReplayToken replayToken, HttpServletRequest request, HttpServletResponse response) {
		// Check replay timestamp
		long now = currentTimeMillis();
		if (abs(now - replayToken.getTimestamp()) >= rconfig.getTermTime()) {
			throw new InvalidReplayTokenException(""); // TODO
		}

		// Validation token signature.
		String plainSign = replayToken.getNonce() + replayToken.getTimestamp();
		String cipherSign = encodeHexString(getDigest(rconfig.getSignatureAlg()).digest(plainSign.getBytes(UTF_8)));
		if (!equalsIgnoreCase(cipherSign, replayToken.getSignature())) {
			throw new InvalidReplayTokenException(""); // TODO
		}

	}

	/**
	 * Gets request replay token.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	protected ReplayToken getRequestReplayToken(HttpServletRequest request, HttpServletResponse response) {
		String tokenSignature = request.getHeader(rconfig.getReplayTokenHeaderName());
		tokenSignature = isBlank(tokenSignature) ? getCleanParam(request, rconfig.getReplayTokenParamName()) : tokenSignature;
		tokenSignature = isBlank(tokenSignature) ? getCookie(request, rconfig.getReplayTokenCookieName()) : tokenSignature;
		return DefaultReplayToken.build(tokenSignature);
	}

	/**
	 * Exclude replay attacks validation URLs mapping matcher.
	 */
	final private static AntPathMatcher defaultExcludeReplayMatcher = new AntPathMatcher();

}
