/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.iam.example.android;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * IAM client android SDK multiple background system authenticated routing
 * controller.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月23日
 * @since
 */
public abstract class AndroidIamUserCoordinator {

	final public static String PARAM_CODE = "code";
	final public static String PARAM_DATA = "data";
	final public static String CODE_UNAUTH = "401";
	final public static String URI_AUTHENTICATOR = "authenticator";

	/**
	 * Table for storing authentication information.
	 */
	final protected ConcurrentMap<ServiceType, String> authTable = new ConcurrentHashMap<>(16);

	@SuppressWarnings("rawtypes")
	protected abstract ResponseEntity doRequest(ServiceType serviceType, String requestUri, Map parameter);

	@SuppressWarnings("rawtypes")
	protected void preRequest(ServiceType serviceType, String requestUri, Map parameter) {
		if (serviceType == null) {
			throw new IllegalStateException("Service type must not be null");
		}
	}

	@SuppressWarnings("rawtypes")
	protected boolean checkUnauthWithPostResponse(ServiceType serviceType, Map header, String body) {
		if (serviceType == null) {
			throw new IllegalStateException("Service type must not be null");
		}
		Map respBody = toJsonMap(body);
		if (respBody != null && CODE_UNAUTH.equals(respBody.get(PARAM_CODE))) { // unauth?
			if (ServiceType.IAM_SERV != serviceType) { // e.g. portal unauth?
				String redirectUrl = (String) respBody.get(PARAM_DATA);
				if (redirectUrl != null) {
					// Check symbol for redirectUrl.
					checkUrl(redirectUrl);
					// Login IAM
					ResponseEntity resp = doRequest(ServiceType.IAM_SERV, URI_AUTHENTICATOR, Collections.emptyMap());
					respBody = toJsonMap(resp.getBody());
					// IAM unauth?
					if (respBody != null && CODE_UNAUTH.equals(respBody.get(PARAM_CODE))) {
						return true; // Jump login view.
					}
				}
			} else { // iam unauth?
				return true; // Direct jump login view.
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	protected abstract Map toJsonMap(String json);

	private void checkUrl(String url) {
		// Check symbol for redirectUrl.
		try {
			new URI(url);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Service type definition.
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年7月23日
	 * @since
	 */
	public static enum ServiceType {

		/**
		 * IAM certification background service.
		 */
		IAM_SERV("http://passport.anjiancloud.test/sso/"),

		/**
		 * Management console back-end service.
		 */
		MP_SERV("http://mp.anjiancloud.test/mp/"),

		/**
		 * Portal backstage service.
		 */
		PORTAL_SERV("http://portal.anjiancloud.test/portal/"),

		/**
		 * Energy consumption management background service
		 */
		EMS_SERV("http://ems.anjiancloud.test/ems/"),

		/**
		 * Trend forecasting background service.
		 */
		TRENDS_SERV("http://trends.anjiancloud.test/trends/"),

		/**
		 * Family cloud backstage service
		 */
		HIOT_SERV("http://hiot.anjiancloud.test/hiot/"),

		/**
		 * Industrial internet of Things cloud background service
		 */
		IIOT_SERV("http://iiot.anjiancloud.test/iiot/");

		// /**
		// * IAM certification background service.
		// */
		// IAM_SERV("https://passport.anjiancloud.com/sso/"),
		//
		// /**
		// * Management console back-end service.
		// */
		// MP_SERV("https://mp.anjiancloud.com/mp/"),
		//
		// /**
		// * Portal backstage service.
		// */
		// PORTAL_SERV("https://portal.anjiancloud.com/portal/"),
		//
		// /**
		// * Energy consumption management background service
		// */
		// EMS_SERV("https://ems.anjiancloud.com/ems/"),
		//
		// /**
		// * Trend forecasting background service.
		// */
		// TRENDS_SERV("https://trends.anjiancloud.com/trends/"),
		//
		// /**
		// * Family cloud backstage service
		// */
		// HIOT_SERV("https://hiot.anjiancloud.com/hiot/"),
		//
		// /**
		// * Industrial internet of Things cloud background service
		// */
		// IIOT_SERV("https://iiot.anjiancloud.com/iiot/");

		final private String baseUri;

		private ServiceType(String baseUri) {
			this.baseUri = baseUri;
		}

		public String getBaseUri() {
			return baseUri;
		}

	}

	@SuppressWarnings("rawtypes")
	public static class ResponseEntity {

		private Map header;

		private String body;

		public ResponseEntity(Map header) {
			super();
			this.header = header;
		}

		public ResponseEntity(String body) {
			super();
			this.body = body;
		}

		public ResponseEntity(Map header, String body) {
			super();
			this.header = header;
			this.body = body;
		}

		public Map getHeader() {
			return header;
		}

		public void setHeader(Map header) {
			this.header = header;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

	}

	public static void main(String[] args) {
		System.out.println("".equals(null));
	}

}
