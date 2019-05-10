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
package com.wl4g.devops.iam.sns;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Charsets;
import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.iam.common.cache.EnhancedCache;
import com.wl4g.devops.iam.config.SnsProperties.AbstractSocialProperties;
import com.wl4g.devops.iam.sns.support.OAuth2GrantType;
import com.wl4g.devops.iam.sns.support.OAuth2ResponseType;
import com.wl4g.devops.iam.sns.support.Oauth2AccessToken;
import com.wl4g.devops.iam.sns.support.Oauth2OpenId;
import com.wl4g.devops.iam.sns.support.Oauth2UserProfile;

/**
 * Abstract based social networking connection binding implement
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月7日
 * @since
 */
public abstract class AbstractBindConnection<C extends AbstractSocialProperties, T extends Oauth2AccessToken, O extends Oauth2OpenId, U extends Oauth2UserProfile>
		implements BindConnection<T, O, U> {
	final public static String DEFAULT_CACHE_NAME = "social_";

	final public static String DEFAULT_PARAM_CLIENT_ID = "client_id";
	final public static String DEFAULT_PARAM_CLIENT_SECRET = "client_secret";
	final public static String DEFAULT_PARAM_AUTH_CODE = "code";
	final public static String DEFAULT_PARAM_REDIRECT_URI = "redirect_uri";
	final public static String DEFAULT_PARAM_STATE = "state";
	final public static String DEFAULT_PARAM_SCOPE = "scope";
	final public static String DEFAULT_PARAM_ACCESS_TOKEN = "access_token";
	final public static String DEFAULT_PARAM_OPEN_ID = "openid";
	final public static String DEFAULT_PARAM_GRANT_TYPE = "grant_type";
	final public static String DEFAULT_PARAM_RESPONSE_TYPE = "response_type";

	final protected Logger log = LoggerFactory.getLogger(getClass());

	final protected C config;
	final protected RestTemplate restTemplate;
	final protected EnhancedCache cache;

	public AbstractBindConnection(C config, RestTemplate restTemplate, CacheManager cacheManager) {
		Assert.notNull(config, "'config' is null, please check the configure");
		Assert.notNull(restTemplate, "'restTemplate' is null, please check the configure");
		Assert.notNull(cacheManager, "'cacheManager' is null, please check the configure");
		this.config = config;
		this.restTemplate = restTemplate;
		Object cacheObject = cacheManager.getCache(DEFAULT_CACHE_NAME);
		Assert.notNull(cacheObject, "'cacheObject' is null, please check the configure");
		Assert.isInstanceOf(EnhancedCache.class, cacheObject);
		this.cache = (EnhancedCache) cacheObject;
	}

	@Override
	public String getAuthorizeCodeUrl(String state, Map<String, String> queryParams) {
		Map<String, String> parameters = this.createParameters();

		// Client ID
		Assert.hasText(config.getAppId(), "'appId' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_CLIENT_ID, config.getAppId());

		// State
		String stateVal = StringUtils.hasText(state) ? state : state();
		parameters.put(DEFAULT_PARAM_STATE, stateVal);

		// Scope
		if (StringUtils.hasText(scope())) {
			parameters.put(DEFAULT_PARAM_SCOPE, scope());
		}

		// Redirect URL
		Assert.hasText(config.getRedirectUrl(), "'redirect_url' is empty, please check the configure");
		// Extra query parameters
		String redirectUrl = config.getRedirectUrl();
		if (queryParams != null && !queryParams.isEmpty()) {
			redirectUrl = WebUtils2.applyQueryURL(redirectUrl, queryParams);
		}
		parameters.put(DEFAULT_PARAM_REDIRECT_URI, WebUtils2.safeEncodeURL(redirectUrl));

		// Response type
		OAuth2ResponseType rt = (responseType() != null) ? responseType() : OAuth2ResponseType.getDefault();
		parameters.put(DEFAULT_PARAM_RESPONSE_TYPE, rt.name().toLowerCase());

		// Post process
		this.postGetAuthorizationCodeUrl(parameters);

		String url = this.parametersToUrl(getAuthorizationCodeUriEndpoint(), parameters);
		if (log.isInfoEnabled()) {
			log.info("Get authorization code url: '{}'", url);
		}

		return url.toString();
	}

	//
	// A P I _ M E T H O D
	//

	@Override
	public T getAccessToken(String code) {
		Map<String, String> parameters = this.createParameters();

		Assert.hasText(config.getAppId(), "'appId' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_CLIENT_ID, config.getAppId());

		Assert.hasText(config.getAppSecret(), "'appSecret' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_CLIENT_SECRET, config.getAppSecret());

		// Consistent with the previous getAuthorizeCodeUrl step
		Assert.hasText(config.getRedirectUrl(), "'redirect_url' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_REDIRECT_URI, config.getRedirectUrl());

		// grant_type
		OAuth2GrantType gt = (grantType() != null) ? grantType() : OAuth2GrantType.getDefault();
		parameters.put(DEFAULT_PARAM_GRANT_TYPE, gt.name().toLowerCase());

		if (gt == OAuth2GrantType.AUTHORIZATION_CODE) {
			Assert.isTrue(StringUtils.hasText(code), "'code' is empty, please check the configure");
			parameters.put(DEFAULT_PARAM_AUTH_CODE, code);
		}

		// Post process
		this.postGetAccessTokenUrl(parameters);

		String url = this.parametersToUrl(getAccessTokenUriEndpoint(), parameters);
		if (log.isInfoEnabled()) {
			log.info("Get accessToken url: '{}'", url);
		}

		// Send request
		String accessTokenText = this.restTemplate.getForObject(url.toString(), String.class);
		Assert.hasText(accessTokenText, "Response accessToken info is empty");
		if (log.isInfoEnabled()) {
			log.info("Response accessToken: {}", accessTokenText);
		}
		return ((Oauth2AccessToken) this.newResponseInstance(1)).build(accessTokenText);
	}

	@Override
	public O getUserOpenId(T accessToken) {
		Map<String, String> parameters = this.createParameters();

		Assert.notNull(accessToken, "'accessToken' is empty, please check the configure");
		Assert.hasText(accessToken.accessToken(), "'accessToken' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_ACCESS_TOKEN, accessToken.accessToken());

		// Post process
		this.postGetOpenIdUrl(parameters);

		String url = this.parametersToUrl(getOpenIdUriEndpoint(), parameters);
		if (log.isInfoEnabled()) {
			log.info("Get openId url: '{}'", url);
		}

		// Send request
		String openIdText = this.restTemplate.getForObject(url.toString(), String.class);
		Assert.hasText(openIdText, "Response openId info is empty");
		if (log.isInfoEnabled()) {
			log.info("Response openId: {}", openIdText);
		}
		return ((Oauth2OpenId) this.newResponseInstance(2)).build(openIdText);
	}

	@Override
	public U getUserInfo(String accessToken, String openId) {
		Map<String, String> parameters = this.createParameters();

		Assert.hasText(config.getAppId(), "'appId' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_CLIENT_ID, config.getAppId());

		Assert.hasText(accessToken, "'accessToken' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_ACCESS_TOKEN, accessToken);

		Assert.hasText(openId, "'openId' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_OPEN_ID, openId);

		// Post process
		this.postGetUserInfoUrl(parameters);

		String url = this.parametersToUrl(getUserInfoUriEndpoint(), parameters);
		if (log.isInfoEnabled()) {
			log.info("Get userInfo url: '{}'", url);
		}

		// Send request
		ResponseEntity<String> resp = this.restTemplate.getForEntity(url.toString(), String.class);
		if (resp != null && resp.getStatusCode() == HttpStatus.OK) {
			String body = resp.getBody();
			Assert.hasText(body, "Response user info is empty");
			body = new String(body.getBytes(Charsets.ISO_8859_1), Charsets.UTF_8);
			if (log.isInfoEnabled()) {
				log.info("Response userInfo: {}", body);
			}
			return ((Oauth2UserProfile) this.newResponseInstance(3)).build(body);
		}
		return null;
	}

	//
	// U R I _ E N D P O I N T _ M E T H O D
	//

	public abstract String getAuthorizationCodeUriEndpoint();

	public abstract String getAccessTokenUriEndpoint();

	public abstract String getOpenIdUriEndpoint();

	public abstract String getUserInfoUriEndpoint();

	//
	// C O N F I G U E _ M E T H O D
	//

	public String state() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public abstract String scope();

	public OAuth2GrantType grantType() {
		return OAuth2GrantType.getDefault();
	}

	public OAuth2ResponseType responseType() {
		return OAuth2ResponseType.getDefault();
	}

	//
	// P O S T _ P R O C E S S _ M E T H O D
	//

	public abstract void postGetAuthorizationCodeUrl(Map<String, String> parameters);

	public abstract void postGetAccessTokenUrl(Map<String, String> parameters);

	public abstract void postGetOpenIdUrl(Map<String, String> parameters);

	public abstract void postGetUserInfoUrl(Map<String, String> parameters);

	//
	// O T H T E R _ M E T H O D
	//

	private String parametersToUrl(String baseUri, Map<String, String> parameters) {
		Assert.notNull(baseUri, "'baseUri' is empty, please check the configure");
		Assert.notEmpty(parameters, "'parameters' is empty, please check the configure");

		/*
		 * https://open.weixin.qq.com/connect/qrconnect?appid=APPID&redirect_uri
		 * =REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#
		 * wechat_redirect
		 */
		String anchor = ""; // Location anchors (tracing points) in HTML pages
		if (baseUri.contains("#")) {
			anchor = baseUri.substring(baseUri.lastIndexOf("#"), baseUri.length());
			baseUri = baseUri.substring(0, baseUri.lastIndexOf("#"));
		}
		StringBuffer url = new StringBuffer(baseUri).append("?");
		parameters.forEach((k, v) -> url.append(k).append("=").append(v).append("&"));
		return url.substring(0, url.length() - 1).toString() + anchor;
	}

	@SuppressWarnings("unchecked")
	private <E> E newResponseInstance(int index) {
		try {
			ResolvableType resolveType = ResolvableType.forClass(this.getClass());
			return (E) resolveType.getSuperType().getGeneric(index).resolve().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, String> createParameters() {
		return new LinkedHashMap<>();
	}

}