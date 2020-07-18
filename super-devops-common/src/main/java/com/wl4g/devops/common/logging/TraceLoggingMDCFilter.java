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
package com.wl4g.devops.common.logging;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static com.wl4g.devops.common.logging.TraceLoggingMDCFilter.TraceMDCDefinition.*;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Add the MDC parameter option to the logback log output. Note that this filter
 * should be placed before other filters as much as possible. By default, for
 * example, "requestid", "requestseq", "timestamp", "uri" will be added to the
 * MDC context.</br>
 * </br>
 * 1) Among them, requestid and requestseq are used for call chain tracking, and
 * developers usually do not need to modify them manually.</br>
 * </br>
 * 2) Timestamp is the time stamp when the request starts to be processed by the
 * servlet. It is designed to be the start time when the filter executes. This
 * value can be used to determine the efficiency of internal program
 * execution.</br>
 * </br>
 * 3) Uri is the URI value of the current request.</br>
 * 
 * Use: We can use the variables in MDC through %X{key} in the layout section of
 * logback.xml, for example: vim application.yml
 * 
 * <pre>
 * logging:
 *   pattern:
 *     console: ${logging.pattern.file}
 *     #file: '%d{yyyy-MM-dd HH:mm:ss.SSS} ${LOG_LEVEL_PATTERN:-%5p} ${PID} --- [%t] %-40.40logger{39} : %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}'
 *     file: '%d{yy-MM-dd HH:mm:ss.SSS} ${LOG_LEVEL_PATTERN:%4p} ${PID} [%t] <font color
=
red>[%X{_H_:X-Request-ID}] [%X{_H_:X-Request-Seq}] [%X{_C_:${spring.cloud.devops.iam.client.cookie.name}}]</font> - %-40.40logger{39} : %m%n${LOG_EXCEPTION_CONVERSION_WORD:%wEx}'
 * </pre>
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年2月26日
 * @since
 */
public abstract class TraceLoggingMDCFilter implements Filter {

	public static final long DEFAULT_CACHE_REFRESH_MS = 500L;
	public static final String HEADER_REQUEST_ID = "X-Request-ID";
	public static final String HEADER_REQUEST_SEQ = "X-Request-Seq";

	final protected Logger log = getLogger(getClass());

	/**
	 * Spring application context.
	 */
	final protected ApplicationContext context;

	/**
	 * Cache refresh timestamp.
	 */
	final private AtomicLong cacheRefreshLastTime = new AtomicLong(0);

	/**
	 * Whether to enable the headers mapping. for example:
	 * <b>%X{_C_:JSESSIONID}</b></br>
	 */
	protected boolean enableMappedCookies;

	/**
	 * Whether to enable the headers mapping. for example:
	 * <b>%X{_H_:X-Forwarded-For}</b></br>
	 */
	protected boolean enableMappedHeaders;

	/**
	 * Whether to enable the headers mapping. for example:
	 * <b>%X{_P_:userId}</b></br>
	 */
	protected boolean enableMappedParameters;

	public TraceLoggingMDCFilter(ApplicationContext context) {
		notNullOf(context, "applicationContext");
		this.context = context;

		// Initializing mapped MDC configuration
		refreshMappedConfigIfNecessary();
	}

	public boolean isEnableMappedCookies() {
		return enableMappedCookies;
	}

	public boolean isEnableMappedHeaders() {
		return enableMappedHeaders;
	}

	public boolean isEnableMappedParameters() {
		return enableMappedParameters;
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;

		try {
			// Refreshing MDC mapped(If necessary).
			refreshMappedConfigIfNecessary();

			// Set logging MDC
			setLoggingMDC(req);
		} catch (Exception e) {
			log.error(format("Could't set logging MDC. uri: %s", req.getRequestURI()), e);
		}

		try {
			chain.doFilter(request, response);
		} finally {
			MDC.clear(); // must
		}

	}

	@Override
	public void destroy() {
	}

	/**
	 * Automatically set trace log MDC
	 * 
	 * @param req
	 */
	protected void setLoggingMDC(HttpServletRequest req) {
		// Set basic MDC
		MDC.put(KEY_REQUEST_ID, req.getHeader(HEADER_REQUEST_ID));
		String requestSeq = req.getHeader(HEADER_REQUEST_SEQ);
		MDC.put(KEY_REQUEST_SEQ, requestSeq);
		if (!isBlank(requestSeq)) {
			// seq will be like:000, real seq is the number of "0"
			String nextSeq = requestSeq + "0";
			MDC.put(KEY_NEXT_REQUEST_SEQ, nextSeq);
		} else {
			MDC.put(KEY_NEXT_REQUEST_SEQ, "0");
		}
		MDC.put(KEY_TIMESTAMP, valueOf(currentTimeMillis()));
		MDC.put(KEY_URI, req.getRequestURI());

		// Set headers MDC
		if (isEnableMappedCookies()) {
			Enumeration<String> e = req.getHeaderNames();
			if (e != null) {
				while (e.hasMoreElements()) {
					String header = e.nextElement();
					String value = req.getHeader(header);
					MDC.put(KEY_PREFIX_HEADER + header, value);
				}
			}
		}

		// Set cookies MDC
		if (isEnableMappedCookies()) {
			Cookie[] cookies = req.getCookies();
			if (cookies != null && cookies.length > 0) {
				for (Cookie cookie : cookies) {
					String name = cookie.getName();
					String value = cookie.getValue();
					MDC.put(KEY_PREFIX_COOKIE + name, value);
				}
			}
		}

		// Set parameters MDC
		if (isEnableMappedParameters()) {
			Enumeration<String> e = req.getParameterNames();
			if (e != null) {
				while (e.hasMoreElements()) {
					String key = e.nextElement();
					String value = req.getParameter(key);
					MDC.put(KEY_PREFIX_PARAMETER + key, value);
				}
			}
		}

	}

	/**
	 * Refreshing MDC mapped by patterns, When the logging configuration is
	 * modified, it can be updated in time
	 * 
	 * @return
	 */
	protected boolean refreshMappedConfigIfNecessary() {
		long now = currentTimeMillis();
		if ((now - cacheRefreshLastTime.get()) < DEFAULT_CACHE_REFRESH_MS) {
			return false;
		}

		String consolePattern = context.getEnvironment().getProperty("logging.pattern.console");
		String filePattern = context.getEnvironment().getProperty("logging.pattern.file");
		this.enableMappedCookies = isMappedMDCField(consolePattern, filePattern, KEY_PREFIX_COOKIE);
		this.enableMappedHeaders = isMappedMDCField(consolePattern, filePattern, KEY_PREFIX_HEADER);
		this.enableMappedParameters = isMappedMDCField(consolePattern, filePattern, KEY_PREFIX_PARAMETER);
		cacheRefreshLastTime.set(now);
		return true;
	}

	/**
	 * 
	 * Check if MDC field is enabled.
	 * 
	 * @param consolePattern
	 * @param filePattern
	 * @param mdcField
	 * @return
	 */
	private boolean isMappedMDCField(String consolePattern, String filePattern, String mdcField) {
		// for example: %X{_C_:JSESSIONID} => %X{_C_:
		String mdcFieldFull = "%X{" + mdcField;
		return contains(consolePattern, mdcFieldFull) || contains(filePattern, mdcFieldFull);
	}

	/**
	 * Tracking log dyeing MDC constants.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年2月26日
	 * @since
	 */
	public static class TraceMDCDefinition {

		public static final String KEY_REQUEST_ID = "requestId";

		public static final String KEY_REQUEST_SEQ = "requestSeq";

		/**
		 * When the tracking chain is distributed, the used SEQ is generated by
		 * filter, and usually the developer does not need to modify it.
		 */
		public static final String KEY_NEXT_REQUEST_SEQ = "nextRequestSeq";

		public static final String KEY_URI = "_uri_";

		/**
		 * Timestamp when the request enters the filter
		 */
		public static final String KEY_TIMESTAMP = "_timestamp_";

		public static final String KEY_PREFIX_COOKIE = "_C_:";

		public static final String KEY_PREFIX_HEADER = "_H_:";

		public static final String KEY_PREFIX_PARAMETER = "_P_:";

	}

}