/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.common.utils.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Charsets;
import com.wl4g.devops.common.utils.lang.StringUtils2;

/**
 * WEB tools
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月30日
 * @since
 */
public abstract class WebUtils2 extends org.springframework.web.util.WebUtils {

	/**
	 * URL separator(/)
	 */
	final public static String URL_SEPAR_SLASH = "%2f";
	/**
	 * URL separator(?)
	 */
	final public static String URL_SEPAR_QUEST = "%3f";
	/**
	 * URL colon separator(:)
	 */
	final public static String URL_SEPAR_COLON = "%3a";
	/**
	 * Protocol separators, such as
	 * https://my.domain.com=>https%3A%2F%2Fmy.domain.com
	 */
	final public static String URL_SEPAR_PROTO = URL_SEPAR_COLON + URL_SEPAR_SLASH + URL_SEPAR_SLASH;

	/**
	 * Request the header key name of real client IP
	 */
	final public static String[] HEADER_REAL_IP = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
			"HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR",
			"HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR" };

	/**
	 * Request the header key name of real protocol
	 */
	final public static String[] HEADER_REAL_PROTOCOL = { "x-forwarded-proto" };

	/**
	 * Request the header key name of real host
	 */
	final public static String[] HEADER_REAL_HOST = { "host" };

	/**
	 * Common media file suffix definitions
	 */
	final public static String[] MEDIA_BASE = new String[] { "ico", "icon", "css", "js", "html", "shtml", "htm", "jsp", "jspx",
			"jsf", "aspx", "asp", "php", "jpeg", "jpg", "png", "bmp", "gif", "tif", "pic", "swf", "svg", "ttf", "eot", "eot@",
			"woff", "woff2", "wd3", "txt", "doc", "docx", "wps", "ppt", "pptx", "pdf", "excel", "xls", "xlsx", "avi", "wav",
			"mp3", "amr", "mp4", "aiff", "rar", "tar.gz", "tar", "zip", "gzip", "ipa", "plist", "apk", "7-zip" };

	/**
	 * Get HTTP remote IP address <br/>
	 * Warning: Be careful if you are implementing security, as all of these
	 * headers are easy to fake.
	 * 
	 * @param request
	 *            HTTP request
	 * @return Real remote client IP
	 */
	public static String getHttpRemoteAddr(HttpServletRequest request) {
		for (String header : HEADER_REAL_IP) {
			String ip = request.getHeader(header);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
				return ip;
			}
		}
		return request.getRemoteAddr();
	}

	/**
	 * Output JSON data with default settings
	 * 
	 * @param response
	 * @param json
	 * @throws IOException
	 */
	public static void writeJson(HttpServletResponse response, String json) throws IOException {
		write(response, HttpServletResponse.SC_OK, MediaType.APPLICATION_JSON_UTF8_VALUE, json.getBytes(Charsets.UTF_8));
	}

	/**
	 * Output message
	 * 
	 * @param response
	 * @param status
	 * @param contentType
	 * @param body
	 * @throws IOException
	 */
	public static void write(HttpServletResponse response, int status, String contentType, byte[] body) throws IOException {
		OutputStream out = null;
		try {
			response.setCharacterEncoding("UTF-8");
			response.setStatus(status);
			response.setContentType(contentType);
			if (body != null) {
				out = response.getOutputStream();
				out.write(body);
				out.flush();
				response.flushBuffer();
			}
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * Check that the requested resource is a base media file?
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isMediaRequest(HttpServletRequest request) {
		String ext = org.springframework.util.StringUtils.getFilenameExtension(request.getRequestURI());
		for (String media : MEDIA_BASE) {
			if (StringUtils.equalsIgnoreCase(ext, media)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Is true <br/>
	 * 
	 * @param value
	 * @return Return TRUE with true/t/y/yes/on/1/enabled
	 */
	public static boolean isTrue(String value) {
		return isTrue(value, false);
	}

	/**
	 * Is true <br/>
	 * 
	 * @param request
	 * @param value
	 * @param defaultValue
	 * @return Return TRUE with true/t/y/yes/on/1/enabled
	 */
	public static boolean isTrue(ServletRequest request, String keyname, boolean defaultValue) {
		return isTrue(request.getParameter(keyname), defaultValue);
	}

	/**
	 * Is true <br/>
	 * 
	 * @param value
	 * @param defaultValue
	 * @return Return TRUE with true/t/y/yes/on/1/enabled
	 */
	public static boolean isTrue(String value, boolean defaultValue) {
		if (StringUtils.isEmpty(value)) {
			return defaultValue;
		}
		return (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("t") || value.equalsIgnoreCase("1")
				|| value.equalsIgnoreCase("enabled") || value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes")
				|| value.equalsIgnoreCase("on"));
	}

	/**
	 * To query URL parameters
	 * 
	 * @param urlQuery
	 * @return
	 */
	public static Map<String, String> toQueryParams(String urlQuery) {
		if (StringUtils.isEmpty(urlQuery)) {
			return Collections.emptyMap();
		}
		try {
			String[] paramPairs = urlQuery.split("&");
			Map<String, String> paramsMap = new LinkedHashMap<>();
			for (int i = 0; i < paramPairs.length; i++) {
				int len = paramPairs[i].indexOf("=");
				if (len > 2) {
					String key = paramPairs[i].substring(0, len);
					String value = paramPairs[i].substring(len + 1);
					paramsMap.put(key, value);
				}
			}
			return paramsMap;
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("Illegal parameter format. '%s'", urlQuery), e);
		}
	}

	/**
	 * Map to query URL
	 * 
	 * @param uri
	 * @param queryParams
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String applyQueryURL(String uri, Map queryParams) {
		if (CollectionUtils.isEmpty(queryParams)) {
			return uri;
		}

		StringBuffer url = new StringBuffer(StringUtils.isEmpty(uri) ? "" : uri);
		if (url.lastIndexOf("?") == -1) {
			url.append("?");
		}
		// To URI parameters string
		for (Iterator<?> it = queryParams.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			url.append(key);
			url.append("=");
			// Prevents any occurrence of a value string null
			Object value = queryParams.get(key);
			if (value != null) {
				url.append(value); // "null"
			}
			if (it.hasNext()) {
				url.append("&");
			}
		}
		return url.toString();
	}

	/**
	 * Get full request query URL
	 * 
	 * @param request
	 * @return e.g:https://portal.mydomain.com/myapp/index?cid=xx&tid=xxx =>
	 *         https://portal.mydomain.com/myapp/index?cid=xx&tid=xxx
	 */
	public static String getFullRequestURL(HttpServletRequest request) {
		return getFullRequestURL(request, true);
	}

	/**
	 * Get full request query URL
	 * 
	 * @param request
	 * @param includeQuery
	 *            Does it contain query parameters?
	 * @return e.g:https://portal.mydomain.com/myapp/index?cid=xx&tid=xxx =>
	 *         https://portal.mydomain.com/myapp/index?cid=xx&tid=xxx
	 */
	public static String getFullRequestURL(HttpServletRequest request, boolean includeQuery) {
		String queryString = includeQuery ? request.getQueryString() : null;
		return request.getRequestURL().toString() + (StringUtils.isEmpty(queryString) ? "" : ("?" + queryString));
	}

	/**
	 * Get full request query URI
	 * 
	 * @param request
	 * @return e.g:https://portal.mydomain.com/myapp/index?cid=xx&tid=xxx =>
	 *         /myapp/index?cid=xx&tid=xxx
	 */
	public static String getFullRequestURI(HttpServletRequest request) {
		String queryString = request.getQueryString();
		return request.getRequestURI() + (StringUtils.isEmpty(queryString) ? "" : ("?" + queryString));
	}

	/**
	 * Has HTTP Request header
	 * 
	 * @param request
	 * @return
	 */
	public static boolean hasHeader(HttpServletRequest request, String name) {
		Enumeration<String> names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			if (StringUtils.equalsIgnoreCase(names.nextElement(), name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Is XHR Request
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isXHRRequest(HttpServletRequest request) {
		return StringUtils.equalsIgnoreCase(request.getHeader("X-Requested-With"), "XMLHttpRequest");
	}

	/**
	 * URL encode by UTF-8
	 * 
	 * @param url
	 *            plain URL
	 * @return
	 */
	public static String safeEncodeURL(String url) {
		try {
			if (!url.toLowerCase().contains(URL_SEPAR_SLASH)) {
				return URLEncoder.encode(url, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return url;
	}

	/**
	 * URL decode by UTF-8
	 * 
	 * @param url
	 *            encode URL
	 * @return
	 */
	public static String safeDecodeURL(String url) {
		try {
			if (url.toLowerCase().contains(URL_SEPAR_SLASH)) {
				return URLDecoder.decode(url, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return url;
	}

	/**
	 * Domain names matching two URIs are equal (including secondary and
	 * tertiary domain names, etc. Exact matching)
	 * 
	 * e.g.<br/>
	 * isEqualWithDomain("http://my.domin.com/myapp1","http://my.domin.com/myapp2")=true
	 * <br/>
	 * isEqualWithDomain("http://my1.domin.com/myapp1","http://my.domin.com/myapp2")=false
	 * <br/>
	 * isEqualWithDomain("http://my.domin.com:80/myapp1","http://my.domin.com:8080/myapp2")=true
	 * <br/>
	 * isEqualWithDomain("https://my.domin.com:80/myapp1","http://my.domin.com:8080/myapp2")=true
	 * <br/>
	 * 
	 * @param uria
	 * @param urib
	 * @return
	 */
	public static boolean isEqualWithDomain(String uria, String urib) {
		if (uria == null || urib == null) {
			return false;
		}

		try {
			return new URI(safeDecodeURL(uria)).getHost().equals(new URI(safeDecodeURL(urib)).getHost());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Determine whether the requested URL belongs to the domain. e.g:<br/>
	 * withInDomain("my.domain.com","http://my.domain.com/myapp") = true <br/>
	 * withInDomain("my.domain.com","https://my.domain.com/myapp") = true <br/>
	 * withInDomain("my.domain.com","https://my1.domain.com/myapp") = false
	 * <br/>
	 * withInDomain("*.domain.com", "https://other1.domain.com/myapp") = true
	 * <br/>
	 * 
	 * @param domain
	 * @param url
	 * @return
	 */
	public static boolean withInDomain(String domain, String url) {
		Assert.notNull(domain, "'domain' must not be null");
		Assert.notNull(url, "'requestUrl' must not be null");

		try {
			String hostname = new URI(safeDecodeURL(cleanURI(url))).getHost();
			if (!domain.contains("*")) {
				Assert.isTrue(StringUtils2.isDomain(domain), String.format("Illegal domain[%s] name format", domain));
				return StringUtils.equalsIgnoreCase(domain, hostname);
			}
			if (domain.startsWith("*")) {
				return StringUtils.equalsIgnoreCase(domain.substring(1), hostname.substring(hostname.indexOf(".")));
			}
			return false;
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Determine whether the requested URL belongs to the base URI. e.g:<br/>
	 * withInURL("https://domain/myapp/login","https://domain/myapp/login?t=123")
	 * == true <br/>
	 * withInURL("https://domain/myapp","https://domain/myapp/login?t=123") ==
	 * true <br/>
	 * withInURL("https://domain/myapp/login?r=abc","https://domain/myapp/login?t=123")
	 * == true <br/>
	 * withInURL("https://domain/myapp/login?r=abc","http://domain/myapp/login?t=123")
	 * == false <br/>
	 * <br/>
	 * 
	 * @param baseUrl
	 * @param url
	 * @return
	 */
	public static boolean withInURL(String baseUrl, String url) {
		if (baseUrl == null || url == null) {
			return false;
		}
		try {
			// If it's a URL in decoding format
			URI baseUrl0 = new URI(safeDecodeURL(cleanURI(baseUrl)));
			URI uri0 = new URI(safeDecodeURL(cleanURI(url)));
			return (StringUtils.startsWithIgnoreCase(uri0.getRawPath(), baseUrl0.getRawPath())
					&& StringUtils.equalsIgnoreCase(uri0.getScheme(), baseUrl0.getScheme())
					&& StringUtils.equalsIgnoreCase(uri0.getHost(), baseUrl0.getHost()) && uri0.getPort() == baseUrl0.getPort());
		} catch (Exception e) {
			// Ignore
		}
		return false;
	}

	/**
	 * Get HTTP request RFC standard based URI
	 * 
	 * @param request
	 * @param hasCtxPath
	 * @return
	 */
	public static String getRFCBaseURI(HttpServletRequest request, boolean hasCtxPath) {
		// Context path
		String ctxPath = request.getContextPath();
		Assert.notNull(ctxPath, "Http request contextPath must not be null");
		ctxPath = !hasCtxPath ? "" : ctxPath;

		// Scheme
		String scheme = request.getScheme();
		for (String schemeKey : HEADER_REAL_PROTOCOL) {
			String scheme0 = request.getHeader(schemeKey);
			if (!StringUtils.isEmpty(scheme0)) {
				scheme = scheme0;
				break;
			}
		}
		Assert.notNull(scheme, "Http request scheme must not be null");

		// Host
		String serverName = request.getServerName();
		for (String hostKey : HEADER_REAL_HOST) {
			String host = request.getHeader(hostKey);
			if (!StringUtils.isEmpty(host)) {
				// my.domain.com:8080
				serverName = (host.contains(":") ? host.split("\\:")[0] : host);
				break;
			}
		}
		Assert.notNull(serverName, "Http request serverName must not be null");

		// Port
		int port = request.getServerPort();
		Assert.isTrue((port > 0 && port < 65536), "Http server port must be greater than 0 and less than 65536");

		StringBuffer baseUri = new StringBuffer(scheme).append("://").append(serverName);
		if (!((scheme.equalsIgnoreCase("HTTP") && port == 80) || (scheme.equalsIgnoreCase("HTTPS") && port == 443))) {
			baseUri.append(":").append(port);
		}
		return baseUri.append(ctxPath).toString();
	}

	/**
	 * Clean request URI. <br/>
	 * cleanURI("https://my.domain.com//myapp///index?t=123")=>"https://my.domain.com/myapp/index?t=123"
	 * 
	 * @param uri
	 * @return
	 */
	public static String cleanURI(String uri) {
		if (StringUtils.isEmpty(uri)) {
			return uri;
		}

		// Checking
		try {
			uri = new URI(uri).toString();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Error syntax uri", e);
		}

		// Cleaning
		try {
			String uri0 = safeEncodeURL(uri).toLowerCase();
			String path = uri0, schema = "";
			if (uri0.contains(URL_SEPAR_PROTO)) {
				// Starting from "://"
				int startIndex = uri0.indexOf(URL_SEPAR_PROTO);
				schema = uri0.substring(0, startIndex) + URL_SEPAR_PROTO;
				path = uri0.substring(startIndex + URL_SEPAR_PROTO.length());
			}
			uri = safeDecodeURL(schema + path.replaceAll(URL_SEPAR_SLASH + URL_SEPAR_SLASH + URL_SEPAR_SLASH, URL_SEPAR_SLASH)
					.replaceAll(URL_SEPAR_SLASH + URL_SEPAR_SLASH, URL_SEPAR_SLASH));
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return uri;
	}

	/**
	 * Generic dynamic web message response type processing enumeration.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2019年1月4日
	 * @since
	 */
	public static enum ResponseType {
		auto, link, json;

		/**
		 * Safe converter string to {@link ResponseType}
		 * 
		 * @param responseType
		 * @return
		 */
		final public static ResponseType safeOf(String responseType) {
			for (ResponseType t : values()) {
				if (String.valueOf(responseType).equalsIgnoreCase(t.name())) {
					return t;
				}
			}
			return null;
		}

		/**
		 * Is response JSON message
		 * 
		 * @param respType
		 * @param request
		 * @return
		 */
		final public static boolean isJSONResponse(String respType, HttpServletRequest request) {
			return isJSONResponse(safeOf(respType), request);
		}

		/**
		 * Is response JSON message
		 * 
		 * @param request
		 * @return
		 */
		final public static boolean isJSONResponse(ResponseType respType, HttpServletRequest request) {
			// Using default strategy
			if (respType == null) {
				respType = ResponseType.auto;
			}

			// Has accept[application/json] ?
			boolean isAccpetJson = false;
			for (String typePart : String.valueOf(request.getHeader("Accept")).split(",")) {
				if (StringUtils.startsWithIgnoreCase(typePart, "application/json")) {
					isAccpetJson = true;
					break;
				}
			}

			// Is header[XHR] ?
			boolean isXhr = WebUtils2.isXHRRequest(request);

			switch (respType) { // Matching
			case json:
				return true;
			case link:
				return false;
			case auto:
				/*
				 * When it's a browser request and not an XHR and token request
				 * (no X-Requested-With: XMLHttpRequest and token at the head of
				 * the line), it responds to the rendering page, otherwise it
				 * responds to JSON.
				 */
				return UserAgentUtils.isBrowser(request) ? (isXhr || isAccpetJson) : true;
			default:
				throw new IllegalStateException(String.format("Illegal response type %s", respType));
			}
		}

	}

}