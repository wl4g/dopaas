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
package com.wl4g.devops.iam.sns;

import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.common.base.Charsets;
import com.wl4g.devops.common.exception.iam.SnsApiBindingException;
import com.wl4g.devops.iam.common.cache.IamCache;
import com.wl4g.devops.iam.config.properties.SnsProperties.AbstractSocialProperties;
import com.wl4g.devops.iam.sns.support.OAuth2GrantType;
import com.wl4g.devops.iam.sns.support.OAuth2ResponseType;
import com.wl4g.devops.iam.sns.support.Oauth2AccessToken;
import com.wl4g.devops.iam.sns.support.Oauth2OpenId;
import com.wl4g.devops.iam.sns.support.Oauth2UserProfile;
import com.wl4g.devops.tool.common.web.WebUtils2;

/**
 * Abstract generic based social networking connection binding implement
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月7日
 * @since
 */
public abstract class GenericOAuth2ApiBinding<C extends AbstractSocialProperties, T extends Oauth2AccessToken, O extends Oauth2OpenId, U extends Oauth2UserProfile>
		implements OAuth2ApiBinding<T, O, U> {
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

	final protected Logger log = getLogger(getClass());

	final protected C config;
	final protected RestTemplate restTemplate;
	final protected IamCache cache;

	public GenericOAuth2ApiBinding(C config, RestTemplate restTemplate, CacheManager cacheManager) {
		notNull(config, "'config' is null, please check the configure");
		notNull(restTemplate, "'restTemplate' is null, please check the configure");
		notNull(cacheManager, "'cacheManager' is null, please check the configure");
		this.config = config;
		this.restTemplate = restTemplate;
		Object cacheObject = cacheManager.getCache(DEFAULT_CACHE_NAME);
		notNull(cacheObject, "'cacheObject' is null, please check the configure");
		isInstanceOf(IamCache.class, cacheObject);
		this.cache = (IamCache) cacheObject;
	}

	//
	// P R E _ O A U T H 2 _ M E T H O D
	//

	@Override
	public String getAuthorizeCodeUrl(String state, Map<String, String> queryParams) {
		Map<String, String> parameters = createParameters();

		// Client ID
		hasText(config.getAppId(), "'appId' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_CLIENT_ID, config.getAppId());

		// State
		String stateVal = StringUtils.hasText(state) ? state : state();
		parameters.put(DEFAULT_PARAM_STATE, stateVal);

		// Scope
		if (StringUtils.hasText(scope())) {
			parameters.put(DEFAULT_PARAM_SCOPE, scope());
		}

		// Redirect URL
		hasText(config.getRedirectUrl(), "'redirect_url' is empty, please check the configure");
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
		postGetAuthorizationCodeUrl(parameters);

		String url = parametersToUrl(getAuthorizationCodeUriEndpoint(), parameters);
		log.info("Get authorization code url: '{}'", url);

		return url.toString();
	}

	//
	// A P I _ M E T H O D
	//

	@Override
	public T getAccessToken(String code) {
		Map<String, String> parameters = createParameters();

		hasText(config.getAppId(), "'appId' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_CLIENT_ID, config.getAppId());

		hasText(config.getAppSecret(), "'appSecret' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_CLIENT_SECRET, config.getAppSecret());

		// Consistent with the previous getAuthorizeCodeUrl step
		hasText(config.getRedirectUrl(), "'redirect_url' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_REDIRECT_URI, config.getRedirectUrl());

		// grant_type
		OAuth2GrantType gt = (grantType() != null) ? grantType() : OAuth2GrantType.getDefault();
		parameters.put(DEFAULT_PARAM_GRANT_TYPE, gt.name().toLowerCase());

		if (gt == OAuth2GrantType.AUTHORIZATION_CODE) {
			isTrue(StringUtils.hasText(code), "'code' is empty, please check the configure");
			parameters.put(DEFAULT_PARAM_AUTH_CODE, code);
		}

		// Post process
		postGetAccessTokenUrl(parameters);

		String url = parametersToUrl(getAccessTokenUriEndpoint(), parameters);
		log.info("Get accessToken url: '{}'", url);

		// Send request
		String accessTokenJson = restTemplate.getForObject(url.toString(), String.class);
		if (isBlank(accessTokenJson)) {
			throw new SnsApiBindingException("OAuth2 response accessToken empty");
		}

		log.info("Response accessToken: {}", accessTokenJson);
		return ((Oauth2AccessToken) newResponseMessage(1)).build(accessTokenJson).validate();
	}

	@Override
	public O getUserOpenId(T accessToken) {
		Map<String, String> parameters = createParameters();

		notNull(accessToken, "'accessToken' is empty, please check the configure");
		hasText(accessToken.accessToken(), "'accessToken' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_ACCESS_TOKEN, accessToken.accessToken());

		// Post process
		postGetOpenIdUrl(parameters);

		String url = parametersToUrl(getOpenIdUriEndpoint(), parameters);
		log.info("Get openId url: '{}'", url);

		// Send request
		String openIdJson = restTemplate.getForObject(url.toString(), String.class);
		if (isBlank(openIdJson)) {
			throw new SnsApiBindingException("OAuth2 response openId empty");
		}

		log.info("Response openId: {}", openIdJson);
		return ((Oauth2OpenId) newResponseMessage(2)).build(openIdJson).validate();
	}

	@Override
	public U getUserInfo(String accessToken, String openId) {
		Map<String, String> parameters = createParameters();

		hasText(config.getAppId(), "'appId' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_CLIENT_ID, config.getAppId());

		hasText(accessToken, "'accessToken' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_ACCESS_TOKEN, accessToken);

		hasText(openId, "'openId' is empty, please check the configure");
		parameters.put(DEFAULT_PARAM_OPEN_ID, openId);

		// Post process
		postGetUserInfoUrl(parameters);

		String url = parametersToUrl(getUserInfoUriEndpoint(), parameters);
		log.info("Get userInfo url: '{}'", url);

		// Send request
		ResponseEntity<String> resp = restTemplate.getForEntity(url.toString(), String.class);
		if (nonNull(resp) && resp.getStatusCode() == HttpStatus.OK) {
			String body = resp.getBody();
			hasText(body, "OAuth2 response userinfo empty");
			body = new String(body.getBytes(Charsets.ISO_8859_1), Charsets.UTF_8);
			log.info("OAuth2 response userInfo: {}", body);

			return ((Oauth2UserProfile) newResponseMessage(3)).build(body).validate();
		}

		throw new SnsApiBindingException(format("Failed to receiving OAuth2 userinfo of - %s", resp));
	}

	//
	// U R I _ E N D P O I N T _ M E T H O D
	//

	protected abstract String getAuthorizationCodeUriEndpoint();

	protected abstract String getAccessTokenUriEndpoint();

	protected abstract String getOpenIdUriEndpoint();

	protected abstract String getUserInfoUriEndpoint();

	//
	// C O N F I G U E _ M E T H O D
	//

	protected String state() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	protected abstract String scope();

	protected OAuth2GrantType grantType() {
		return OAuth2GrantType.getDefault();
	}

	protected OAuth2ResponseType responseType() {
		return OAuth2ResponseType.getDefault();
	}

	//
	// P O S T _ P R O C E S S _ M E T H O D
	//

	protected abstract void postGetAuthorizationCodeUrl(Map<String, String> parameters);

	protected abstract void postGetAccessTokenUrl(Map<String, String> parameters);

	protected abstract void postGetOpenIdUrl(Map<String, String> parameters);

	protected abstract void postGetUserInfoUrl(Map<String, String> parameters);

	//
	// O T H T E R _ M E T H O D
	//

	private String parametersToUrl(String baseUri, Map<String, String> parameters) {
		notNull(baseUri, "'baseUri' is empty, please check the configure");
		notEmpty(parameters, "'parameters' is empty, please check the configure");

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
	private <E> E newResponseMessage(int index) {
		try {
			ResolvableType resolveType = ResolvableType.forClass(getClass());
			return (E) resolveType.getSuperType().getGeneric(index).resolve().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, String> createParameters() {
		return new LinkedHashMap<>();
	}

}