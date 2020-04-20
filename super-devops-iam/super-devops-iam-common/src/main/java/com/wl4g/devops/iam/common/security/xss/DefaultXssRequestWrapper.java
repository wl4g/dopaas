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
package com.wl4g.devops.iam.common.security.xss;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import javax.servlet.http.HttpServletRequest;

import com.wl4g.devops.iam.common.security.xss.html.HTMLParser;
import com.wl4g.devops.iam.common.security.xss.html.XSSFilter;

/**
 * Default XSS HttpServlet request wrapper
 *
 * @author wangl.sir
 * @version v1.0 2019年4月26日
 * @since
 */
public class DefaultXssRequestWrapper extends XssRequestWrapper {

	public DefaultXssRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getParameter(String name) {
		return _xssEncode(super.getParameter(_xssEncode(name)));
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] paramValues = super.getParameterValues(_xssEncode(name));
		if (paramValues == null || paramValues.length == 0) {
			return null;
		}

		for (int i = 0; i < paramValues.length; i++) {
			paramValues[i] = _xssEncode(paramValues[i]);
		}

		return paramValues;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> _paramMap = new LinkedHashMap<>();

		Map<String, String[]> paramMap = super.getParameterMap();
		for (String key : paramMap.keySet()) {
			String[] values = paramMap.get(_xssEncode(key));

			for (int i = 0; i < values.length; i++) {
				values[i] = _xssEncode(values[i]);
			}

			_paramMap.put(key, values);
		}

		return _paramMap;
	}

	@Override
	public String getHeader(String name) {
		return _xssEncode(super.getHeader(_xssEncode(name)));
	}

	@Override
	public String getAuthType() {
		return _xssEncode(super.getAuthType());
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		Enumeration<String> headers = super.getHeaders(name);

		List<String> _headers = new ArrayList<>(4);
		while (headers.hasMoreElements()) {
			_headers.add(_xssEncode(headers.nextElement()));
		}

		return Collections.enumeration(_headers);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		Enumeration<String> names = super.getHeaderNames();

		List<String> _names = new ArrayList<>(4);
		while (names.hasMoreElements()) {
			_names.add(_xssEncode(names.nextElement()));
		}

		return Collections.enumeration(_names);
	}

	@Override
	public int getIntHeader(String name) {
		return super.getIntHeader(_xssEncode(name));
	}

	@Override
	public String getMethod() {
		return _xssEncode(super.getMethod());
	}

	@Override
	public String getRemoteUser() {
		return _xssEncode(super.getRemoteUser());
	}

	@Override
	public String getRequestedSessionId() {
		return _xssEncode(super.getRequestedSessionId());
	}

	@Override
	public String changeSessionId() {
		return _xssEncode(super.changeSessionId());
	}

	@Override
	public String getQueryString() {
		return _xssEncode(super.getQueryString());
	}

	@Override
	public Object getAttribute(String name) {
		return _xssEncode(super.getAttribute(_xssEncode(name)));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <O, I> O _xssEncode(I value) {
		if (value != null && value instanceof CharSequence && isNotBlank((String) value)) {
			// value = StringEscapeUtils.unescapeJava((String) value);
			// value = StringEscapeUtils.unescapeEcmaScript((String) value);
			// value = StringEscapeUtils.unescapeHtml4((String) value);
			// value = StringEscapeUtils.unescapeHtml3((String) value);
			// value = StringEscapeUtils.unescapeJson((String) value);
			// value = StringEscapeUtils.unescapeXml((String) value);
			// value = StringEscapeUtils.unescapeXml((String) value);

			try {
				StringReader reader = new StringReader((String) value);
				StringWriter writer = new StringWriter();
				HTMLParser.process(reader, writer, new XSSFilter(), true);
				return (O) writer.toString();
			} catch (NullPointerException ex) {
				return (O) value;
			} catch (Exception ex) {
				throw new IllegalArgumentException(format("Decrypting xss request failure of parameter: %s ", value), ex);
			}
		}

		return (O) value;
	}

}