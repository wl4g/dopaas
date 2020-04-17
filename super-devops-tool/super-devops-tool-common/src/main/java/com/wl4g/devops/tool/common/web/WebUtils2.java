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
package com.wl4g.devops.tool.common.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static java.util.Locale.*;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.common.annotations.Beta;
import com.google.common.base.Charsets;
import com.google.common.net.MediaType;
import com.wl4g.devops.tool.common.collection.CollectionUtils2;
import com.wl4g.devops.tool.common.lang.Assert2;
import com.wl4g.devops.tool.common.lang.StringUtils2;

import static com.wl4g.devops.tool.common.collection.Collections2.isEmptyArray;
import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static com.wl4g.devops.tool.common.lang.StringUtils2.isDomain;
import static com.wl4g.devops.tool.common.web.UserAgentUtils.*;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.containsAny;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.replaceIgnoreCase;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * WEB generic tools .
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月30日
 * @since
 */
@Beta
public abstract class WebUtils2 {

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
			if (isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
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
		write(response, HttpServletResponse.SC_OK, MediaType.JSON_UTF_8.toString(), json.getBytes(Charsets.UTF_8));
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
		return isMediaRequest(request.getRequestURI());
	}

	/**
	 * Check that the requested resource is a base media file?
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isMediaRequest(String path) {
		String ext = StringUtils2.getFilenameExtension(path);
		for (String media : MEDIA_BASE) {
			if (equalsIgnoreCase(ext, media)) {
				return true;
			}
		}
		return false;
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
	 * @return Return TRUE with true/t/y/yes/on/1/enabled
	 */
	public static boolean isTrue(String value) {
		return isTrue(value, false);
	}

	/**
	 * Is true <br/>
	 * 
	 * @param value
	 * @param defaultValue
	 * @return Return TRUE with true/t/y/yes/on/1/enabled
	 */
	public static boolean isTrue(String value, boolean defaultValue) {
		return StringUtils2.isTrue(value, defaultValue);
	}

	/**
	 * To query URL parameters.
	 * 
	 * <pre>
	 * toQueryParams("application=iam-example&redirect_url=http://my.com/index") == {application->iam-example, redirect_url=>http://my.com/index}
	 * toQueryParams("application=iam-example&redirect_url=http://my.com/index/#/me") == {application->iam-example, redirect_url=>http://my.com/index/#/me}
	 * </pre>
	 * 
	 * @param urlQuery
	 * @return
	 */
	public static Map<String, String> toQueryParams(String urlQuery) {
		if (isBlank(urlQuery)) {
			return emptyMap();
		}
		try {
			String[] paramPairs = urlQuery.split("&");
			Map<String, String> paramsMap = new LinkedHashMap<>();
			for (int i = 0; i < paramPairs.length; i++) {
				String[] parts = trimToEmpty(paramPairs[i]).split("=");
				if (parts.length >= 2) {
					paramsMap.put(parts[0], parts[1]);
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String applyQueryURL(String uri, Map queryParams) {
		if (CollectionUtils2.isEmpty(queryParams) || isBlank(uri)) {
			return uri;
		}

		URI _uri = URI.create(uri);
		// Merge origin-older uri query parameters.
		Map<String, String> mergeParams = new HashMap<>(toQueryParams(_uri.getQuery()));
		mergeParams.putAll(queryParams);
		// Gets base URI.
		StringBuffer url = new StringBuffer(uri); // Relative path?
		if (!isAnyBlank(_uri.getScheme(), _uri.getHost())) {
			url.setLength(0); // Reset
			url.append(getBaseURIForDefault(_uri.getScheme(), _uri.getHost(), _uri.getPort()));
			url.append(_uri.getPath());
		}
		if (url.lastIndexOf("?") == -1) {
			url.append("?");
		}

		// To URI parameters string
		for (Iterator<?> it = mergeParams.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			url.append(key);
			url.append("=");
			// Prevents any occurrence of a value string null
			Object value = mergeParams.get(key);
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
	 * Reject http request methods.
	 * 
	 * @param allowMode
	 * @param request
	 * @param response
	 * @param methods
	 * @throws UnsupportedOperationException
	 */
	public static void rejectRequestMethod(boolean allowMode, ServletRequest request, ServletResponse response, String... methods)
			throws UnsupportedOperationException {
		notNullOf(request, "request");
		notNullOf(response, "response");
		if (!isEmptyArray(methods)) {
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse resp = (HttpServletResponse) response;
			boolean rejected1 = true, rejected2 = false;
			for (String method : methods) {
				if (method.equalsIgnoreCase(req.getMethod())) {
					if (allowMode) {
						rejected1 = false;
					} else {
						rejected2 = true;
					}
					break;
				}
			}
			if ((allowMode && rejected1) || (!allowMode && rejected2)) {
				resp.setStatus(405);
				throw new UnsupportedOperationException(format("No support '%s' request method", req.getMethod()));
			}
		}
	}

	/**
	 * Convenience method that returns a request parameter value, first running
	 * it through {@link StringUtils#clean(String)}.
	 *
	 * @param request
	 *            the servlet request.
	 * @param paramName
	 *            the parameter name.
	 * @return the clean param value, or null if the param does not exist or is
	 *         empty.
	 */
	public static String getRequestParam(ServletRequest request, String paramName, boolean required) {
		String paramValue = request.getParameter(paramName);
		String cleanedValue = paramValue;
		if (paramValue != null) {
			cleanedValue = paramValue.trim();
			if (cleanedValue.equals(EMPTY)) {
				cleanedValue = null;
			}
		}
		if (required) {
			hasTextOf(cleanedValue, paramName);
		}
		return cleanedValue;
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
			if (!contains(trimToEmpty(url).toLowerCase(US), URL_SEPAR_SLASH)) {
				return URLEncoder.encode(url, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
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
			if (containsAny(trimToEmpty(url).toLowerCase(US), URL_SEPAR_SLASH, URL_SEPAR_QUEST, URL_SEPAR_COLON)) {
				return URLDecoder.decode(url, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
		return url;
	}

	/**
	 * Domain names equals two URIs are equal (including secondary and tertiary
	 * domain names, etc. Exact matching)
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
		return URI.create(safeDecodeURL(uria)).getHost().equals(URI.create(safeDecodeURL(urib)).getHost());
	}

	/**
	 * Check whether the wildcard domain uri belongs to the same origin. </br>
	 * 
	 * e.g:
	 * 
	 * <pre>
	 * {@link #isSameWildcardOrigin}("http://*.aa.domain.com/API/v2", "http://bb.aa.domain.com/API/v2", true) == true
	 * {@link #isSameWildcardOrigin}("http://*.aa.domain.com/API/v2", "https://bb.aa.domain.com/API/v2", true) == false
	 * {@link #isSameWildcardOrigin}("http://*.aa.domain.com/api/v2/", "http://bb.aa.domain.com/API/v2", true) == true
	 * {@link #isSameWildcardOrigin}("http://bb.*.domain.com", "https://bb.aa.domain.com", false) == true
	 * {@link #isSameWildcardOrigin}("http://*.aa.domain.com", "https://bb.aa.domain.com", true) == false
	 * {@link #isSameWildcardOrigin}("http://*.aa.domain.com:8080", "http://bb.aa.domain.com:8080/", true) == true
	 * {@link #isSameWildcardOrigin}("http://*.aa.domain.com:8080", "http://bb.aa.domain.com:8443/v2/xx", true) == true
	 * {@link #isSameWildcardOrigin}("http://*.aa.domain.com:*", "http://bb.aa.domain.com:8443/v2/xx", true) == true
	 * </pre>
	 * 
	 * @param defWildcardUri
	 *            Definition wildcard URI
	 * @param requestUri
	 * @param checkScheme
	 * @return
	 */
	public static boolean isSameWildcardOrigin(String defWildcardUri, String requestUri, boolean checkScheme) {
		if (isBlank(defWildcardUri) || isBlank(requestUri))
			return false;
		if (defWildcardUri.equals(requestUri)) // URL equaled?
			return true;

		// Scheme matched?
		URI uri1 = URI.create(defWildcardUri);
		URI uri2 = URI.create(requestUri);
		final boolean schemeMatched = uri1.getScheme().equalsIgnoreCase(uri2.getScheme());
		if (checkScheme && !schemeMatched)
			return false;

		// Hostname equaled?
		String hostname1 = extractWildcardEndpoint(defWildcardUri);
		String hostname2 = extractWildcardEndpoint(requestUri);
		if (equalsIgnoreCase(hostname1, hostname2))
			return true;

		// Hostname wildcard matched?
		boolean wildcardHostnameMatched = false;
		String[] parts1 = split(hostname1, ".");
		String[] parts2 = split(hostname2, ".");
		for (int i = 0; i < parts1.length; i++) {
			if (equalsIgnoreCase(parts1[i], "*")) {
				if (i < (hostname1.length() - 1) && i < (hostname2.length() - 1)) {
					String compare1 = join(parts1, ".", i + 1, parts1.length);
					String compare2 = join(parts2, ".", i + 1, parts2.length);
					if (equalsIgnoreCase(compare1, compare2)) {
						wildcardHostnameMatched = true;
						break;
					}
				}
			}
		}
		// Check scheme matched.
		if (checkScheme && wildcardHostnameMatched) {
			return schemeMatched;
		}

		return wildcardHostnameMatched;
	}

	/**
	 * Extract domain text from {@link URI}. </br>
	 * Uri resolution cannot be used here because it may fail when there are
	 * wildcards, e.g,
	 * {@link URI#create}("http://*.aa.domain.com/api/v2/).gethost() is
	 * null.</br>
	 * 
	 * <pre>
	 * {@link #extractWildcardHostName}("http://*.domain.com/v2/xx") == *.domain.com
	 * {@link #extractWildcardHostName}("http://*.aa.domain.com:*") == *.aa.domain.com
	 * {@link #extractWildcardHostName}("http://*.bb.domain.com:8080/v2/xx") == *.bb.domain.com
	 * </pre>
	 * 
	 * @param wildcardUri
	 * @return
	 */
	public static String extractWildcardEndpoint(String wildcardUri) {
		if (isEmpty(wildcardUri))
			return EMPTY;

		wildcardUri = trimToEmpty(safeEncodeURL(wildcardUri)).toLowerCase(US);
		String noPrefix = wildcardUri.substring(wildcardUri.indexOf(URL_SEPAR_PROTO) + URL_SEPAR_PROTO.length());
		int slashIndex = noPrefix.indexOf(URL_SEPAR_SLASH);
		String serverName = noPrefix;
		if (slashIndex > 0) {
			serverName = noPrefix.substring(0, slashIndex);
		}

		// Check domain illegal?
		// e.g, http://*.domain.com:8080[allow]
		// http://*.domain.com:*[allow]
		// http://*.aa.*.domain.com[noallow]
		String hostname = serverName;
		if (serverName.contains(URL_SEPAR_COLON)) {
			hostname = serverName.substring(0, serverName.indexOf(URL_SEPAR_COLON));
		}
		Assert2.isTrue(hostname.indexOf("*") == hostname.lastIndexOf("*"), "Illegal serverName: %s, contains multiple wildcards!",
				serverName);
		return safeDecodeURL(hostname);
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
		notNull(domain, "'domain' must not be null");
		notNull(url, "'requestUrl' must not be null");
		try {
			String hostname = new URI(safeDecodeURL(cleanURI(url))).getHost();
			if (!domain.contains("*")) {
				Assert2.isTrue(isDomain(domain), String.format("Illegal domain[%s] name format", domain));
				return equalsIgnoreCase(domain, hostname);
			}
			if (domain.startsWith("*")) {
				return equalsIgnoreCase(domain.substring(1), hostname.substring(hostname.indexOf(".")));
			}
			return false;
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
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
		notNull(ctxPath, "Http request contextPath must not be null");
		ctxPath = !hasCtxPath ? "" : ctxPath;
		// Scheme
		String scheme = request.getScheme();
		for (String schemeKey : HEADER_REAL_PROTOCOL) {
			String scheme0 = request.getHeader(schemeKey);
			if (!isBlank(scheme0)) {
				scheme = scheme0;
				break;
			}
		}
		// Host & Port
		String serverName = request.getServerName();
		int port = request.getServerPort();
		for (String hostKey : HEADER_REAL_HOST) {
			String host = request.getHeader(hostKey);
			if (!isBlank(host)) {
				// me.domain.com:8080
				serverName = host;
				if (host.contains(":")) {
					String[] part = split(host, ":");
					serverName = part[0];
					if (!isBlank(part[1])) {
						port = Integer.parseInt(part[1]);
					}
				} else if (equalsIgnoreCase(scheme, "HTTP")) {
					port = 80;
				} else if (equalsIgnoreCase(scheme, "HTTPS")) {
					port = 443;
				}
				break;
			}
		}
		return getBaseURIForDefault(scheme, serverName, port) + ctxPath;
	}

	/**
	 * Obtain base URI for default. </br>
	 * 
	 * <pre>
	 * getBaseURIForDefault("http", "my.com", 8080) == "http://my.com:8080"
	 * getBaseURIForDefault("http", "my.com", 80) == "http://my.com"
	 * getBaseURIForDefault("https", "my.com", 443) == "https://my.com"
	 * getBaseURIForDefault("https", "my.com", -1) == "https://my.com"
	 * </pre>
	 * 
	 * @param scheme
	 * @param serverName
	 * @param port
	 * @return
	 */
	public static String getBaseURIForDefault(String scheme, String serverName, int port) {
		notNull(scheme, "Http request scheme must not be empty");
		notNull(serverName, "Http request serverName must not be empty");
		StringBuffer baseUri = new StringBuffer(scheme).append("://").append(serverName);
		if (port > 0) {
			Assert2.isTrue((port > 0 && port < 65536), "Http server port must be greater than 0 and less than 65536");
			if (!((equalsIgnoreCase(scheme, "HTTP") && port == 80) || (equalsIgnoreCase(scheme, "HTTPS") && port == 443))) {
				baseUri.append(":").append(port);
			}
		}
		return baseUri.toString();
	}

	/**
	 * Clean request URI. </br>
	 * 
	 * <pre>
	 * cleanURI("https://my.domain.com//myapp///index?t=123") => "https://my.domain.com/myapp/index?t=123"
	 * </pre>
	 * 
	 * @param uri
	 * @return
	 */
	@Beta
	public static String cleanURI(String uri) {
		if (isBlank(uri)) {
			return uri;
		}

		// Check syntax
		uri = URI.create(uri).toString();

		/**
		 * Cleaning.</br>
		 * Note: that you cannot change the original URI case.
		 */
		try {
			String encodeUrl = safeEncodeURL(uri);
			String pathUrl = encodeUrl, schema = EMPTY;
			if (encodeUrl.toLowerCase(US).contains(URL_SEPAR_PROTO)) {
				// Start from "://"
				int startIndex = encodeUrl.toLowerCase(US).indexOf(URL_SEPAR_PROTO);
				schema = encodeUrl.substring(0, startIndex) + URL_SEPAR_PROTO;
				pathUrl = encodeUrl.substring(startIndex + URL_SEPAR_PROTO.length());
			}

			// Cleanup for: '/shopping/order//list' => '/shopping/order/list'
			String lastCleanUrl = pathUrl;
			for (int i = 0; i < 256; i++) { // https://www.ietf.org/rfc/rfc2616.txt#3.2.1
				String cleanUrl = replaceIgnoreCase(lastCleanUrl, (URL_SEPAR_SLASH2).toUpperCase(), URL_SEPAR_SLASH);
				if (StringUtils.equals(cleanUrl, lastCleanUrl)) {
					break;
				} else {
					lastCleanUrl = cleanUrl;
				}
			}
			return safeDecodeURL(schema + lastCleanUrl);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Obtain the available request remember URL, for example: used to log in
	 * successfully and redirect to the last remembered URL
	 * 
	 * @param request
	 * @return
	 */
	public static String getAvaliableRequestRememberUrl(HttpServletRequest request) {
		String rememberUrl = request.getHeader("Referer");
		// #[RFC7231], https://tools.ietf.org/html/rfc7231#section-5.5.2
		rememberUrl = isNotBlank(rememberUrl) ? rememberUrl : request.getHeader("Referrer");
		// Fallback
		if (isBlank(rememberUrl) && request.getMethod().equalsIgnoreCase("GET")) {
			rememberUrl = getFullRequestURL(request, true);
		}
		return rememberUrl;
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
		 * Default get response type parameter name.
		 */
		final public static String DEFAULT_RESPTYPE_NAME = "response_type";

		/**
		 * Get the name of the corresponding data type parameter. Note that
		 * NGINX defaults to replace the underlined header, such as:
		 * 
		 * <pre>
		 * header(response_type: json) => header(responsetype: json)
		 * </pre>
		 * 
		 * and how to disable this feature of NGINX:
		 * 
		 * <pre>
		 * http {
		 * 	underscores_in_headers on;
		 * }
		 * </pre>
		 */
		final public static String[] RESPTYPE_NAMES = { DEFAULT_RESPTYPE_NAME, "responsetype", "Response-Type" };

		/**
		 * Safe converter string to {@link ResponseType}
		 * 
		 * @param respType
		 * @return
		 */
		final public static ResponseType safeOf(String respType) {
			for (ResponseType t : values()) {
				if (String.valueOf(respType).equalsIgnoreCase(t.name())) {
					return t;
				}
			}
			return null;
		}

		/**
		 * Is response JSON message
		 * 
		 * @param respTypeValue
		 * @param request
		 * @return
		 */
		public static boolean isJSONResp(String respTypeValue, HttpServletRequest request) {
			return determineJSONResponse(safeOf(respTypeValue), request);
		}

		/**
		 * Is response JSON message
		 * 
		 * @param request
		 * @return
		 */
		public static boolean isJSONResp(HttpServletRequest request) {
			return isJSONResp(request, null);
		}

		/**
		 * Is response JSON message
		 * 
		 * @param request
		 * @param respTypeName
		 * @return
		 */
		public static boolean isJSONResp(HttpServletRequest request, String respTypeName) {
			notNull(request, "Request must not be null");

			List<String> paramNames = Arrays.asList(RESPTYPE_NAMES);
			if (!isBlank(respTypeName)) {
				paramNames.add(respTypeName);
			}
			for (String name : paramNames) {
				String respTypeValue = request.getParameter(name);
				respTypeValue = isBlank(respTypeValue) ? request.getHeader(name) : respTypeValue;
				if (!isBlank(respTypeValue)) {
					return determineJSONResponse(safeOf(respTypeValue), request);
				}
			}

			// Using auto mode.
			return determineJSONResponse(ResponseType.auto, request);
		}

		/**
		 * Determine response JSON message
		 * 
		 * @param request
		 * @return
		 */
		private static boolean determineJSONResponse(ResponseType respType, HttpServletRequest request) {
			notNull(request, "Request must not be null");
			// Using default strategy
			if (Objects.isNull(respType)) {
				respType = ResponseType.auto;
			}

			// Has header(accept:application/json)
			boolean hasAccpetJson = false;
			for (String typePart : String.valueOf(request.getHeader("Accept")).split(",")) {
				if (startsWithIgnoreCase(typePart, "application/json")) {
					hasAccpetJson = true;
					break;
				}
			}

			// Has header(origin:xx.domain.com)
			boolean hasOrigin = isNotBlank(request.getHeader("Origin"));

			// Is header[XHR] ?
			boolean isXhr = isXHRRequest(request);

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
				return isBrowser(request) ? (isXhr || hasAccpetJson || hasOrigin) : true;
			default:
				throw new IllegalStateException(String.format("Illegal response type %s", respType));
			}
		}

	}

	/**
	 * URL scheme(HTTPS)
	 */
	final public static String URL_SCHEME_HTTPS = "https";

	/**
	 * URL scheme(HTTP)
	 */
	final public static String URL_SCHEME_HTTP = "http";

	/**
	 * URL separator(/)
	 */
	final public static String URL_SEPAR_SLASH = "%2f";

	/**
	 * URL double separator(//)
	 */
	final public static String URL_SEPAR_SLASH2 = URL_SEPAR_SLASH + URL_SEPAR_SLASH;

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
	 * Request the header key name of real client IP. </br>
	 * 
	 * <pre>
	 *	一、没有使用代理服务器的情况：
	 *	      REMOTE_ADDR = 您的 IP
	 *	      HTTP_VIA = 没数值或不显示
	 *	      HTTP_X_FORWARDED_FOR = 没数值或不显示
	 *	二、使用透明代理服务器的情况：Transparent Proxies
	 *	      REMOTE_ADDR = 最后一个代理服务器 IP 
	 *	      HTTP_VIA = 代理服务器 IP
	 *	      HTTP_X_FORWARDED_FOR = 您的真实 IP ，经过多个代理服务器时，这个值类似如下：203.98.182.163, 203.98.182.163, 203.129.72.215。
	 *	   这类代理服务器还是将您的信息转发给您的访问对象，无法达到隐藏真实身份的目的。
	 *	三、使用普通匿名代理服务器的情况：Anonymous Proxies
	 *	      REMOTE_ADDR = 最后一个代理服务器 IP 
	 *	      HTTP_VIA = 代理服务器 IP
	 *	      HTTP_X_FORWARDED_FOR = 代理服务器 IP ，经过多个代理服务器时，这个值类似如下：203.98.182.163, 203.98.182.163, 203.129.72.215。
	 *	   隐藏了您的真实IP，但是向访问对象透露了您是使用代理服务器访问他们的。
	 *	四、使用欺骗性代理服务器的情况：Distorting Proxies
	 *	      REMOTE_ADDR = 代理服务器 IP 
	 *	      HTTP_VIA = 代理服务器 IP 
	 *	      HTTP_X_FORWARDED_FOR = 随机的 IP ，经过多个代理服务器时，这个值类似如下：203.98.182.163, 203.98.182.163, 203.129.72.215。
	 *	   告诉了访问对象您使用了代理服务器，但编造了一个虚假的随机IP代替您的真实IP欺骗它。
	 *	五、使用高匿名代理服务器的情况：High Anonymity Proxies (Elite proxies)
	 *	      REMOTE_ADDR = 代理服务器 IP
	 *	      HTTP_VIA = 没数值或不显示
	 *	      HTTP_X_FORWARDED_FOR = 没数值或不显示 ，经过多个代理服务器时，这个值类似如下：203.98.182.163, 203.98.182.163, 203.129.72.215。
	 * </pre>
	 */
	final public static String[] HEADER_REAL_IP = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "X-Real-IP",
			"REMOTE_ADDR", "Remote-Addr", "RemoteAddr", // RemoteAddr
			"REMOTE_IP", "Remote-Ip", "RemoteIp", // RemoteIp: Aliyun-SLB
			"HTTP_X_FORWARDED_FOR", "Http-X-Forwarded-For", "HttpXForwardedFor", // HttpXForwardedFor
			"HTTP_X_FORWARDED", "Http-X-Forwarded", "HttpXForwarded", // HttpXForwarded
			"HTTP_Client_IP", "Http-Client-Ip", "HttpClientIp", // HttpClientIp
			"HTTP_X_CLUSTER_CLIENT_IP", "Http-X-Cluster-Client-Ip", "HttpXClusterClientIp", // HttpXClusterClientIp
			"HTTP_FORWARDED_FOR", "Http-Forwarded-For", "HttpForwardedFor", // HttpForwardedFor
			"HTTP_VIA ", "Http-Via", "HttpVia" }; // HttpVia

	/**
	 * Request the header key name of real protocol scheme.
	 */
	final public static String[] HEADER_REAL_PROTOCOL = { "X-Forwarded-Proto" };

	/**
	 * Request the header key name of real host
	 */
	final public static String[] HEADER_REAL_HOST = { "Host" };

	/**
	 * Common media file suffix definitions
	 */
	final public static String[] MEDIA_BASE = new String[] { "ico", "icon", "css", "js", "html", "shtml", "htm", "jsp", "jspx",
			"jsf", "aspx", "asp", "php", "jpeg", "jpg", "png", "bmp", "gif", "tif", "pic", "swf", "svg", "ttf", "eot", "eot@",
			"woff", "woff2", "wd3", "txt", "doc", "docx", "wps", "ppt", "pptx", "pdf", "excel", "xls", "xlsx", "avi", "wav",
			"mp3", "amr", "mp4", "aiff", "rar", "tar.gz", "tar", "zip", "gzip", "ipa", "plist", "apk", "7-zip" };

}