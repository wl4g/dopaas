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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import static java.lang.System.*;
import static java.util.Collections.singletonMap;
import static com.wl4g.devops.tool.common.web.WebUtils2.*;

public class WebUtils2Tests {

	public static void main(String[] args) {
		out.println("-------------0000-------------------");

		// URI s =
		// URI.create("http://portal.wl4g.com/portal/authenticator?fragment=eleIndex/elecReport#/eleIndex/index");
		URI s = URI.create(
				"http://portal.wl4g.com/portal/authenticator?redirect_url=http://portal.wl4g.com/?fragment=eleIndex/elecReport#/authLogin");
		out.println(s.getScheme());
		out.println(s.getHost());
		out.println(s.getPort());
		out.println(s.getPath());
		out.println(s.getQuery());
		out.println(s.getFragment());
		out.println("-------------1111-------------------");

		out.println(getBaseURIForDefault("http", "my.com", 8080));
		out.println(getBaseURIForDefault("http", "my.com", 80));
		out.println(getBaseURIForDefault("https", "my.com", 443));
		out.println(getBaseURIForDefault("http", "my.com", -1));
		out.println(getBaseURIForDefault("https", "my.com", -1));

		out.println("-------------2222-------------------");
		out.println(URI.create("http://my.com/index/#/me").getQuery());
		out.println(toQueryParams("application=iam-example&gt=aaa&redirect_url=http://my.com/index"));
		out.println(toQueryParams("application=iam-example&gt=aaa&redirect_url=http://my.com/index/#/me"));

		out.println("-------------3333-------------------");
		out.println(extractWildcardEndpoint("http://*.aaa.anjiancloud.test/API/v2"));

		out.println("-------------1111-------------------");
		out.println(isSameWildcardOrigin("http://*.aa.domain.com/api/v2", "http://bb.aa.domain.com/api/v2", true));
		out.println(isSameWildcardOrigin("http://*.aa.domain.com/api/v2", "https://bb.aa.domain.com/api/v2", true));
		out.println(isSameWildcardOrigin("http://*.aa.domain.com/api/v2/", "http://bb.aa.domain.com/API/v2", true));
		out.println(isSameWildcardOrigin("http://bb.*.domain.com", "https://bb.aa.domain.com", false));
		out.println(isSameWildcardOrigin("http://*.aa.domain.com", "https://bb.aa.domain.com", true));
		out.println(isSameWildcardOrigin("http://*.aa.domain.com:8080", "http://bb.aa.domain.com:8080/", true));
		out.println(isSameWildcardOrigin("http://*.aa.domain.com:*", "http://bb.aa.domain.com:8080/api/v2/xx", true));
		out.println(isSameWildcardOrigin("http://*.aa.domain.com:8080", "http://bb.aa.domain.com:8443/v2/xx", true));
		out.println(isSameWildcardOrigin("http://*.aa.domain.com:*", "http://bb.aa.domain.com:8080/v2/xx", true));

		out.println("-------------4444-------------------");
		out.println(cleanURI("https://my.domain.com//myapp///index?t=123"));
		out.println(cleanURI("https://my.domain.com///myapp/////index?t=123"));

		out.println("-------------5555-------------------");
		try {
			rejectRequestMethod(true, new TestHttpServletRequest("POST"), testResponse, "POST", "GET");
			out.println("reject1 -- OK");
		} catch (Exception e) {
			out.println("reject1 -- ERROR");
		}
		try {
			rejectRequestMethod(false, new TestHttpServletRequest("POST"), testResponse, "POST", "GET");
			out.println("reject2 -- OK");
		} catch (Exception e) {
			out.println("reject2 -- ERROR");
		}

		out.println("-------------6666-------------------");
		out.println(applyQueryURL("http://iam.wl4g.com/api/v2?response_type=json", singletonMap("aa", 11)));
		out.println(applyQueryURL("http://iam.wl4g.com/api/v2?response_type=json&", singletonMap("aa", 11)));
		out.println(applyQueryURL("http://iam.wl4g.com/api/v2", singletonMap("aa", 11)));
		out.println(applyQueryURL("/api/v2?response_type=json", singletonMap("bb", 22)));
		out.println(applyQueryURL("api/v2?response_type=json&", singletonMap("bb", 22)));
		out.println(applyQueryURL("/api/v2", singletonMap("bb", 22)));

	}

	public static class TestHttpServletRequest implements HttpServletRequest {

		private String method;

		public TestHttpServletRequest(String method) {
			super();
			this.method = method;
		}

		@Override
		public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public AsyncContext startAsync() throws IllegalStateException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
			// TODO Auto-generated method stub

		}

		@Override
		public void setAttribute(String arg0, Object arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void removeAttribute(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isSecure() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isAsyncSupported() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isAsyncStarted() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public ServletContext getServletContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getServerPort() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getServerName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getScheme() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public RequestDispatcher getRequestDispatcher(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getRemotePort() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getRemoteHost() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getRemoteAddr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getRealPath(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public BufferedReader getReader() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getProtocol() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getParameterValues(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Enumeration<String> getParameterNames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getParameter(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Enumeration<Locale> getLocales() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Locale getLocale() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getLocalPort() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getLocalName() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getLocalAddr() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public DispatcherType getDispatcherType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getContentType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getContentLengthLong() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getContentLength() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getCharacterEncoding() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Enumeration<String> getAttributeNames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getAttribute(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public AsyncContext getAsyncContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0) throws IOException, ServletException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void logout() throws ServletException {
			// TODO Auto-generated method stub

		}

		@Override
		public void login(String arg0, String arg1) throws ServletException {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isUserInRole(String arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isRequestedSessionIdValid() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isRequestedSessionIdFromUrl() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isRequestedSessionIdFromURL() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isRequestedSessionIdFromCookie() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Principal getUserPrincipal() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public HttpSession getSession(boolean arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public HttpSession getSession() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getServletPath() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getRequestedSessionId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public StringBuffer getRequestURL() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getRequestURI() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getRemoteUser() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getQueryString() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getPathTranslated() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getPathInfo() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<Part> getParts() throws IOException, ServletException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Part getPart(String arg0) throws IOException, ServletException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getMethod() {
			return method;
		}

		@Override
		public int getIntHeader(String arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Enumeration<String> getHeaders(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Enumeration<String> getHeaderNames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getHeader(String arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getDateHeader(String arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Cookie[] getCookies() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getContextPath() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getAuthType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String changeSessionId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException {
			// TODO Auto-generated method stub
			return false;
		}
	};

	public static HttpServletResponse testResponse = new HttpServletResponse() {

		@Override
		public void setLocale(Locale loc) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setContentType(String type) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setContentLengthLong(long len) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setContentLength(int len) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setCharacterEncoding(String charset) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setBufferSize(int size) {
			// TODO Auto-generated method stub

		}

		@Override
		public void resetBuffer() {
			// TODO Auto-generated method stub

		}

		@Override
		public void reset() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isCommitted() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Locale getLocale() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getContentType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getCharacterEncoding() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getBufferSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void flushBuffer() throws IOException {
			// TODO Auto-generated method stub

		}

		@Override
		public void setStatus(int sc, String sm) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setStatus(int sc) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setIntHeader(String name, int value) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setHeader(String name, String value) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setDateHeader(String name, long date) {
			// TODO Auto-generated method stub

		}

		@Override
		public void sendRedirect(String location) throws IOException {
			// TODO Auto-generated method stub

		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			// TODO Auto-generated method stub

		}

		@Override
		public void sendError(int sc) throws IOException {
			// TODO Auto-generated method stub

		}

		@Override
		public int getStatus() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Collection<String> getHeaders(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<String> getHeaderNames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getHeader(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String encodeUrl(String url) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String encodeURL(String url) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String encodeRedirectUrl(String url) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String encodeRedirectURL(String url) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean containsHeader(String name) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void addIntHeader(String name, int value) {
			// TODO Auto-generated method stub

		}

		@Override
		public void addHeader(String name, String value) {
			// TODO Auto-generated method stub

		}

		@Override
		public void addDateHeader(String name, long date) {
			// TODO Auto-generated method stub

		}

		@Override
		public void addCookie(Cookie cookie) {
			// TODO Auto-generated method stub

		}
	};

}