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
package com.wl4g.devops.iam.example.android;

import static java.util.Collections.emptyMap;

import java.net.URI;
import java.net.URISyntaxException;
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

	/**
	 * Table for storing authentication information.
	 */
	final protected ConcurrentMap<ServiceType, String> grantTicketCache = new ConcurrentHashMap<>(16);

	/**
	 * To JSON map.
	 * 
	 * @param json
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected abstract Map toJsonMap(String json);

	/**
	 * Do execution HTTP request.
	 * 
	 * @param serviceType
	 * @param requestUri
	 * @param parameter
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected abstract ResponseEntity doRequest(ServiceType serviceType, String requestUri, Map parameter);

	/**
	 * Check for unauthenticated from the current response
	 * 
	 * @param serviceType
	 * @param header
	 * @param body
	 * @return Returning TRUE indicates that the current state is not
	 *         authenticated, otherwise FALSE
	 */
	@SuppressWarnings("rawtypes")
	protected boolean checkUnauthWithResponse(ServiceType serviceType, Map header, String body) {
		if (serviceType == null) {
			throw new IllegalStateException("Service type must not be null");
		}
		Map respBody = toJsonMap(body);
		if (respBody != null && "401".equals(respBody.get("code"))) { // unauth?
			// e.g. EMS no-authentication?
			if (ServiceType.IAM != serviceType) {
				// Login IAM
				String authIamUri = String.format("authenticator?response_type=json&service=%s&redirect_url=%s",
						serviceType.getService(), serviceType.getBaseUri());
				ResponseEntity resp = doRequest(ServiceType.IAM, authIamUri, emptyMap());
				respBody = toJsonMap(resp.getBody());
				// IAM no authentication?
				if (respBody != null && "401".equals(respBody.get("code"))) {
					return true; // Necessary jump login view.
				} else {
					// get redirectUrl and check.
					String redirectUrl = checkUrl((String) respBody.get("data"));
					resp = doRequest(serviceType, redirectUrl, emptyMap());
					respBody = toJsonMap(resp.getBody());
					if (respBody != null && "401".equals(respBody.get("code"))) {
						// e.g. save EMS token.
						String grantTicket = (String) resp.getHeader().get(serviceType.getGrantTicketName());
						if (grantTicket == null) {
							throw new IllegalStateException(String
									.format("Error to iam client grantTicket null, for response header: ", resp.getHeader()));
						}
						grantTicketCache.put(serviceType, grantTicket);
					}
				}
			} else { // IAM no authentication?
				return true; // Direct jump login view.
			}
		}
		return false;
	}

	private String checkUrl(String url) {
		// Check symbol for redirectUrl.
		if (url != null) {
			try {
				new URI(url);
			} catch (URISyntaxException e) {
				throw new IllegalStateException(e);
			}
		}
		return url;
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
		IAM("sso", "__SSO", "http://passport.anjiancloud.test/sso/"),

		/**
		 * Management console back-end service.
		 */
		MP("mp", "__MP", "http://mp.anjiancloud.test/mp/"),

		/**
		 * Portal backstage service.
		 */
		PORTAL("portal", "__PORTAL", "http://portal.anjiancloud.test/portal/"),

		/**
		 * Energy consumption management background service
		 */
		EMS("ems", "__EMS", "http://ems.anjiancloud.test/ems/"),

		/**
		 * Trend forecasting background service.
		 */
		TRENDS("trends", "__TRENDS", "http://trends.anjiancloud.test/trends/"),

		/**
		 * Family cloud backstage service
		 */
		HIOT("hiot", "__HIOT", "http://hiot.anjiancloud.test/hiot/"),

		/**
		 * Industrial internet of Things cloud background service
		 */
		IIOT("iiot", "__IIOT", "http://iiot.anjiancloud.test/iiot/");

		// /**
		// * IAM certification background service.
		// */
		// IAM("sso", "__SSO","https://passport.anjiancloud.com/sso/"),
		//
		// /**
		// * Management console back-end service.
		// */
		// MP("mp", "__MP","https://mp.anjiancloud.com/mp/"),
		//
		// /**
		// * Portal backstage service.
		// */
		// PORTAL("portal",
		// "__PORTAL","https://portal.anjiancloud.com/portal/"),
		//
		// /**
		// * Energy consumption management background service
		// */
		// EMS("ems", "__EMS","https://ems.anjiancloud.com/ems/"),
		//
		// /**
		// * Trend forecasting background service.
		// */
		// TRENDS("trends",
		// "__TRENDS","https://trends.anjiancloud.com/trends/"),
		//
		// /**
		// * Family cloud backstage service
		// */
		// HIOT("hiot", "__HIOT","https://hiot.anjiancloud.com/hiot/"),
		//
		// /**
		// * Industrial internet of Things cloud background service
		// */
		// IIOT("iiot", "__IIOT","https://iiot.anjiancloud.com/iiot/");

		final private String service;

		final private String grantTicketName;

		final private String baseUri;

		private ServiceType(String service, String grantTicketName, String baseUri) {
			this.service = service;
			this.grantTicketName = grantTicketName;
			this.baseUri = baseUri;
		}

		public String getService() {
			return service;
		}

		public String getBaseUri() {
			return baseUri;
		}

		public String getGrantTicketName() {
			return grantTicketName;
		}

	}

	/**
	 * Response wrap entity.
	 * 
	 * @author Wangl.sir
	 * @version v1.0 2019年7月25日
	 * @since
	 */
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

}