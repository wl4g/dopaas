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

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.components.tools.common.web.WebUtils2.getFullRequestURL;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpHeaders.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.filter.CorsFilter;

import com.wl4g.devops.components.tools.common.log.SmartLogger;

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

		// Timing-Allow-Origin:
		response.setHeader("Timing-Allow-Origin", "*");

		// response.addHeader("P3P", "CP='CURa ADMa DEVa PSAo PSDo OUR BUS UNI
		// PUR INT DEM STA PRE COM NAV OTC NOI DSP COR'");
		// response.addHeader("Set-Cookie", "HttpOnly;Secure;SameSite=None");
		super.doFilterInternal(request, new CorsProtectHttpServletResponse(response), filterChain);
	}

	/**
	 * Iam matches CORS processor.
	 *
	 * @author Wangl.sir
	 * @version v1.0 2019年8月21日
	 * @since
	 */
	public static class IamCorsProcessor extends DefaultCorsProcessor {

		final protected SmartLogger log = getLogger(getClass());

		@Override
		public boolean processRequest(CorsConfiguration config, HttpServletRequest request, HttpServletResponse response)
				throws IOException {
			// Core processing.
			final boolean corsAllowed = super.processRequest(config, request, response);
			if (!corsAllowed && log.isWarnEnabled()) {
				log.warn("Rejected cors request of URL: '{}'", (request.getMethod() + " " + getFullRequestURL(request)));
			}
			return corsAllowed;
		}

	}

	/**
	 * HTTP servlet response wrapper for CORS protection.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年6月11日
	 * @since
	 * @see {@link CorsProtectHttpServletResponse#reset()}
	 */
	public static class CorsProtectHttpServletResponse implements HttpServletResponse {

		/** Actual {@link HttpServletResponse} */
		final private HttpServletResponse response;

		public CorsProtectHttpServletResponse(HttpServletResponse response) {
			notNullOf(response, "response");
			this.response = response;
		}

		@Override
		public String getCharacterEncoding() {
			return response.getCharacterEncoding();
		}

		@Override
		public String getContentType() {
			return response.getContentType();
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return response.getOutputStream();
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			return response.getWriter();
		}

		@Override
		public void setCharacterEncoding(String charset) {
			response.setCharacterEncoding(charset);
		}

		@Override
		public void setContentLength(int len) {
			response.setContentLength(len);
		}

		@Override
		public void setContentLengthLong(long length) {
			response.setContentLengthLong(length);
		}

		@Override
		public void setContentType(String type) {
			response.setContentType(type);
		}

		@Override
		public void setBufferSize(int size) {
			response.setBufferSize(size);
		}

		@Override
		public int getBufferSize() {
			return response.getBufferSize();
		}

		@Override
		public void flushBuffer() throws IOException {
			response.flushBuffer();
		}

		@Override
		public void resetBuffer() {
			response.resetBuffer();
		}

		@Override
		public boolean isCommitted() {
			return response.isCommitted();
		}

		@Override
		public void reset() {
			reset(false);
		}

		/**
		 * In order to solve the problem of being executed by external error
		 * {@link HttpServletResponse#reset()} deleted the CORS security header
		 * by mistake, resulting in cross domain failure.
		 * 
		 * @param force
		 */
		public void reset(boolean force) {
			if (force) {
				response.reset();
			} else {
				String allowOrigin = response.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN);
				String allowHeaders = response.getHeader(ACCESS_CONTROL_ALLOW_HEADERS);
				String allowMethods = response.getHeader(ACCESS_CONTROL_ALLOW_METHODS);
				String allowCredentials = response.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS);
				response.reset();
				// Restore reserved CORS security headers.
				response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, allowOrigin);
				response.addHeader(ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders);
				response.addHeader(ACCESS_CONTROL_ALLOW_METHODS, allowMethods);
				response.addHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, allowCredentials);
			}
		}

		@Override
		public void setLocale(Locale loc) {
			response.setLocale(loc);
		}

		@Override
		public Locale getLocale() {
			return response.getLocale();
		}

		@Override
		public void addCookie(Cookie cookie) {
			response.addCookie(cookie);
		}

		@Override
		public boolean containsHeader(String name) {
			return response.containsHeader(name);
		}

		@Override
		public String encodeURL(String url) {
			return response.encodeURL(url);
		}

		@Override
		public String encodeRedirectURL(String url) {
			return response.encodeRedirectURL(url);
		}

		@SuppressWarnings("deprecation")
		@Override
		public String encodeUrl(String url) {
			return response.encodeUrl(url);
		}

		@SuppressWarnings("deprecation")
		@Override
		public String encodeRedirectUrl(String url) {
			return response.encodeRedirectUrl(url);
		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			response.sendError(sc, msg);
		}

		@Override
		public void sendError(int sc) throws IOException {
			response.sendError(sc);
		}

		@Override
		public void sendRedirect(String location) throws IOException {
			response.sendRedirect(location);
		}

		@Override
		public void setDateHeader(String name, long date) {
			response.setDateHeader(name, date);
		}

		@Override
		public void addDateHeader(String name, long date) {
			response.addDateHeader(name, date);
		}

		@Override
		public void setHeader(String name, String value) {
			response.setHeader(name, value);
		}

		@Override
		public void addHeader(String name, String value) {
			response.addHeader(name, value);
		}

		@Override
		public void setIntHeader(String name, int value) {
			response.setIntHeader(name, value);
		}

		@Override
		public void addIntHeader(String name, int value) {
			response.addIntHeader(name, value);
		}

		@Override
		public void setStatus(int sc) {
			response.setStatus(sc);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void setStatus(int sc, String sm) {
			response.setStatus(sc, sm);
		}

		@Override
		public int getStatus() {
			return response.getStatus();
		}

		@Override
		public String getHeader(String name) {
			return response.getHeader(name);
		}

		@Override
		public Collection<String> getHeaders(String name) {
			return response.getHeaders(name);
		}

		@Override
		public Collection<String> getHeaderNames() {
			return response.getHeaderNames();
		}

	}

}