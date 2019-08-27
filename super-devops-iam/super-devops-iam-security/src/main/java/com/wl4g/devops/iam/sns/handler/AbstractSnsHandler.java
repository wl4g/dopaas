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
package com.wl4g.devops.iam.sns.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.wl4g.devops.common.bean.iam.SocialAuthorizeInfo;
import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.iam.common.cache.EnhancedCacheManager;
import com.wl4g.devops.iam.common.cache.EnhancedKey;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.config.IamProperties;
import com.wl4g.devops.iam.config.SnsProperties;
import com.wl4g.devops.iam.configure.ServerSecurityConfigurer;
import com.wl4g.devops.iam.filter.ProviderSupports;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_AFTER_CALLBACK_AGENT;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SNS_BASE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_SNSAUTH;
import static com.wl4g.devops.iam.filter.AbstractIamAuthenticationFilter.URI_BASE_MAPPING;
import static com.wl4g.devops.iam.sns.web.AbstractSnsController.PARAM_SNS_PRIVIDER;
import static com.wl4g.devops.iam.sns.web.AbstractSnsController.PARAM_SNS_CODE;
import static com.wl4g.devops.iam.sns.web.AbstractSnsController.KEY_SNS_CALLBACK_PARAMS;
import static com.wl4g.devops.iam.sns.web.AbstractSnsController.PARAM_SNS_CALLBACK_ID;

import com.wl4g.devops.iam.sns.BindConnection;
import com.wl4g.devops.iam.sns.SocialConnectionFactory;
import com.wl4g.devops.iam.sns.support.Oauth2AccessToken;
import com.wl4g.devops.iam.sns.support.Oauth2OpenId;
import com.wl4g.devops.iam.sns.support.Oauth2UserProfile;

/**
 * Abstract based social networking services handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月7日
 * @since
 */
public abstract class AbstractSnsHandler implements SnsHandler {

	/**
	 * Request connects to a social network (requesting oauth2 authorization)
	 * parameters binding session key name
	 */
	final public static String KEY_SNS_CONNECT_PARAMS = "connect_params_";

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * IAM server properties configuration
	 */
	final protected IamProperties config;

	/**
	 * SNS properties configuration
	 */
	final protected SnsProperties snsConfig;

	/**
	 * IAM Social connection factory
	 */
	final protected SocialConnectionFactory connectFactory;

	/**
	 * IAM security context handler
	 */
	final protected ServerSecurityConfigurer context;

	/**
	 * IAM server security coprocessor
	 */
	@Autowired
	protected ServerSecurityConfigurer coprocessor;

	/**
	 * Enhanced cache manager.
	 */
	@Autowired
	protected EnhancedCacheManager cacheManager;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MSG_SOURCE)
	protected SessionDelegateMessageBundle bundle;

	public AbstractSnsHandler(IamProperties config, SnsProperties snsConfig, SocialConnectionFactory connectFactory,
			ServerSecurityConfigurer context) {
		Assert.notNull(config, "'config' must not be null");
		Assert.notNull(snsConfig, "'snsConfig' must not be null");
		Assert.notNull(connectFactory, "'connectFactory' must not be null");
		Assert.notNull(context, "'context' must not be null");
		this.config = config;
		this.snsConfig = snsConfig;
		this.connectFactory = connectFactory;
		this.context = context;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String connect(Which which, String provider, String state, Map<String, String> connectParams) {
		// Check parameters
		checkConnectRequireds(provider, state, connectParams);

		// Provider connection
		BindConnection connect = connectFactory.getBindConnection(provider);

		// Authorizing code URL query parameters
		Map<String, String> queryParams = getAuthorizeUrlQueryParams(which, provider, state, connectParams);

		// Build URL
		String authorizingUrl = connect.getAuthorizeCodeUrl(state, queryParams);
		if (log.isInfoEnabled()) {
			log.info("SNS connect provider[{}], state[{}], authorizingUrl[{}]", provider, state, authorizingUrl);
		}
		return authorizingUrl;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String callback(Which which, String provider, String state, String code, HttpServletRequest request) {
		try {
			// Connect parameters
			Map<String, String> connectParams = getOauth2ConnectParameters(state, request);

			// Check parameters
			checkConnectCallbacks(provider, state, code, connectParams);

			// Provider connection
			BindConnection connect = connectFactory.getBindConnection(provider);

			// Subsequent processing
			String result = afterCallbackProcess(provider, code, connect, connectParams, request);

			// Build response message
			String msg = buildResponseMessage(provider, result, connectParams, request);

			// Wrap response
			return wrapResponse(msg, connectParams, request);
		} finally {
			releaseConnects(state);
		}
	}

	/**
	 * Check connect required parameters
	 * 
	 * @param provider
	 * @param refreshUrl
	 */
	protected void checkConnectRequireds(String provider, String state, Map<String, String> connectParams) {
		// Check provider supported
		ProviderSupports.checkSupport(provider);
		// Check state
		Assert.hasText(state, String.format("Illegal parameter '%s' is empty", config.getParam().getState()));
	}

	/**
	 * Get authorize URL query parameters
	 * 
	 * @param which
	 * @param provider
	 * @param state
	 * @param connectParams
	 * @return
	 */
	protected Map<String, String> getAuthorizeUrlQueryParams(Which which, String provider, String state,
			Map<String, String> connectParams) {
		Map<String, String> queryParams = new HashMap<>();
		queryParams.put(config.getParam().getWhich(), whichType().name().toLowerCase());
		return queryParams;
	}

	/**
	 * Check connect callback required parameters
	 * 
	 * @param provider
	 * @param state
	 * @param code
	 */
	protected void checkConnectCallbacks(String provider, String state, String code, Map<String, String> connectParams) {
		// Check based parameters
		checkConnectRequireds(provider, state, connectParams);
		// Check 'code'
		Assert.hasText(code, String.format("Illegal parameter '%s' must not be empty", PARAM_SNS_CODE));
	}

	/**
	 * Save oauth2 connect parameters
	 * 
	 * @param which
	 * @param provider
	 * @param state
	 * @param connectParams
	 */
	protected void saveOauth2ConnectParameters(String provider, String state, Map<String, String> connectParams) {
		// Basic parameters
		Map<String, String> connectParamsAll = new HashMap<>();
		connectParamsAll.put(PARAM_SNS_PRIVIDER, provider);
		connectParamsAll.put(config.getParam().getWhich(), connectParams.remove(config.getParam().getWhich()));
		connectParamsAll.put(config.getParam().getState(), connectParams.remove(config.getParam().getState()));

		// Extended parameters
		if (connectParams != null && !connectParams.isEmpty()) {
			connectParams.forEach((key, value) -> {
				Assert.hasText(key, "Empty parameter names are not allowed.");
				Assert.hasText(value, String.format("No empty parameter allowed, name '%s'", key));
				if (null != connectParamsAll.putIfAbsent(key, value)) {
					throw new IllegalStateException(String.format("Extended parameter '%s' is not supported.", key));
				}
			});
		}

		// Save to cache
		cacheManager.getEnhancedCache(CACHE_SNSAUTH)
				.put(new EnhancedKey(KEY_SNS_CONNECT_PARAMS + state, snsConfig.getOauth2ConnectExpireMs()), connectParamsAll);
	}

	/**
	 * Get oauth2 connect parameters
	 * 
	 * @param state
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	protected Map<String, String> getOauth2ConnectParameters(String state, HttpServletRequest request) {
		// Saved parameters
		return (Map<String, String>) cacheManager.getEnhancedCache(CACHE_SNSAUTH)
				.get(new EnhancedKey(KEY_SNS_CONNECT_PARAMS + state, HashMap.class));
	}

	/**
	 * After SNS callback process.<br/>
	 * 
	 * @param provider
	 * @param code
	 * @param connect
	 * @param connectParams
	 * @param request
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected String afterCallbackProcess(String provider, String code, BindConnection connect, Map<String, String> connectParams,
			HttpServletRequest request) {
		// Access token
		Oauth2AccessToken ast = connect.getAccessToken(code);
		// User openId
		Oauth2OpenId openId = connect.getUserOpenId(ast);
		// User info
		Oauth2UserProfile profile = connect.getUserInfo(ast.accessToken(), openId.openId());

		/*
		 * Caching social callback user openId information.(Applicable only to
		 * which=login/client_auth)<br/>
		 * See:xx.realm.Oauth2SnsAuthorizingRealm#doAuthenticationInfo()
		 */
		String callbackId = generateCallbackId();
		cacheManager.getEnhancedCache(CACHE_SNSAUTH).put(new EnhancedKey(KEY_SNS_CALLBACK_PARAMS + callbackId, 30),
				new SocialAuthorizeInfo(provider, openId.openId(), openId.unionId(), BeanMap.create(profile)));

		return callbackId;
	}

	/**
	 * Building response message.(Default:Refresh redirection URL)
	 * 
	 * @param provider
	 * @param result
	 * @param connectParams
	 * @param request
	 * @return
	 */
	protected abstract String buildResponseMessage(String provider, String result, Map<String, String> connectParams,
			HttpServletRequest request);

	/**
	 * Get login submission URL. <br/>
	 * Synchronized at com.wl4g.devops.iam.common.filter.Iam
	 * AuthenticationFilter#getUriMapping <br/>
	 * 
	 * <font color=red>Note: Social network login does not require login
	 * account(principal)</font>
	 * 
	 * @param provider
	 *            SNS connect provider name
	 * @param callbackId
	 *            Oauth2 callback process id
	 * @param request
	 * @return
	 */
	protected String getLoginSubmissionUrl(String provider, String callbackId, HttpServletRequest request) {
		Assert.hasText(callbackId, String.format("'%s' must not be empty", PARAM_SNS_CALLBACK_ID));
		Assert.notNull(request, "'request' must not be null");

		/*
		 * xx.filter.Oauth2SnsAuthenticationFilter#createAuthenticationToken()
		 */
		StringBuffer loginSubmissionUrl = new StringBuffer(WebUtils2.getRFCBaseURI(request, true));
		loginSubmissionUrl.append(URI_BASE_MAPPING).append(provider).append("?");
		loginSubmissionUrl.append(PARAM_SNS_CALLBACK_ID).append("=").append(callbackId);
		return loginSubmissionUrl.toString();
	}

	/**
	 * Release clean oauth2 connect parameters etc.
	 * 
	 * @param state
	 */
	protected void releaseConnects(String state) {
		Assert.state(!StringUtils.isEmpty(state), String.format("'%s' must not be empty", config.getParam().getState()));
		cacheManager.getEnhancedCache(CACHE_SNSAUTH).remove(new EnhancedKey(KEY_SNS_CONNECT_PARAMS + state));
	}

	/**
	 * Wrap response.(Default: Wrap after callback agent refresh redirection
	 * URL)
	 * 
	 * @param refreshUrl
	 *            Default: Refresh redirection URL, or other message
	 * @param connectParams
	 *            SNS connect parameters
	 * @return
	 */
	protected String wrapResponse(String refreshUrl, Map<String, String> connectParams, HttpServletRequest request) {
		Assert.hasText(refreshUrl, "Callback response message must not be empty");

		/*
		 * When using agent jumps, you need to code to pass parameters to the
		 * past. See:this#afterCallbackAgent()
		 */
		if (connectParams != null && WebUtils2.isTrue(connectParams.get(config.getParam().getAgent()))) {
			StringBuffer url = new StringBuffer(WebUtils2.getRFCBaseURI(request, true));
			url.append(URI_S_SNS_BASE).append("/");
			url.append(URI_S_AFTER_CALLBACK_AGENT).append("?");
			url.append(config.getParam().getRefreshUrl()).append("=");
			url.append(WebUtils2.safeEncodeURL(refreshUrl));
			return url.toString();
		}
		return refreshUrl;
	}

	/**
	 * Generate callback-id
	 * 
	 * @return
	 */
	protected String generateCallbackId() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}