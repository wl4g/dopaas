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
package com.wl4g.devops.iam.sns.handler;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_AFTER_CALLBACK_AGENT;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SNS_BASE;
import static com.wl4g.devops.common.web.BaseController.REDIRECT_PREFIX;
import static com.wl4g.devops.iam.common.authc.model.SecondAuthcAssertModel.Status.IllegalAuthorizer;
import static com.wl4g.devops.iam.common.authc.model.SecondAuthcAssertModel.Status.InvalidAuthorizer;

import com.google.common.base.Splitter;
import com.wl4g.devops.iam.common.authc.SecondAuthenticationException;
import com.wl4g.devops.iam.common.authc.model.SecondAuthcAssertModel;
import com.wl4g.devops.iam.common.cache.CacheKey;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo.Parameter;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo.SnsAuthorizingParameter;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.config.properties.SnsProperties;
import com.wl4g.devops.iam.configure.ServerSecurityConfigurer;
import com.wl4g.devops.iam.sns.OAuth2ApiBinding;
import com.wl4g.devops.iam.sns.OAuth2ApiBindingFactory;
import com.wl4g.devops.iam.sns.support.Oauth2AccessToken;
import com.wl4g.devops.iam.sns.support.Oauth2OpenId;
import com.wl4g.devops.tool.common.id.IdGenerators;
import com.wl4g.devops.tool.common.web.WebUtils2;

/**
 * Secondary authentication SNS handler
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年2月24日
 * @since
 */
public class SecondAuthcSnsHandler extends AbstractSnsHandler {

	/**
	 * Secondary authentication cache name
	 */
	final public static String SECOND_AUTHC_CACHE = "second_auth_";

	public SecondAuthcSnsHandler(IamProperties config, SnsProperties snsConfig, OAuth2ApiBindingFactory connectFactory,
			ServerSecurityConfigurer context) {
		super(config, snsConfig, connectFactory, context);
	}

	@Override
	public String doOAuth2GetAuthorizingUrl(Which which, String provider, String state, Map<String, String> connectParams) {
		// Connecting
		String authorizingUrl = super.doOAuth2GetAuthorizingUrl(which, provider, state, connectParams);

		// Save connect parameters
		saveOauth2ConnectParameters(provider, state, connectParams);

		return REDIRECT_PREFIX + authorizingUrl;
	}

	@Override
	protected void checkConnectParameters(String provider, String state, Map<String, String> connectParams) {
		super.checkConnectParameters(provider, state, connectParams);

		// Check connect parameters
		Assert.notEmpty(connectParams, "Connect parameters must not be empty");

		// Check 'application'
		Assert.hasText(connectParams.get(config.getParam().getApplication()),
				String.format("'%s' must not be empty", config.getParam().getApplication()));

		// Check 'authorizers'
		Assert.hasText(connectParams.get(config.getParam().getAuthorizers()),
				String.format("'%s' must not be empty", config.getParam().getAuthorizers()));

		// Check 'agent'
		String agentKey = config.getParam().getAgent();
		String agentValue = connectParams.get(agentKey);
		Assert.hasText(agentValue, String.format("'%s' must not be empty", agentKey));
		Assert.state(WebUtils2.isTrue(agentValue), String.format("Parameter %s current supports only enabled mode", agentKey));

		// Check 'funcId'
		Assert.hasText(connectParams.get(config.getParam().getFuncId()),
				String.format("'%s' must not be empty", config.getParam().getFuncId()));
	}

	@Override
	protected void checkCallbackParameters(String provider, String state, String code, Map<String, String> connectParams) {
		// Check 'state'
		Assert.notNull(connectParams, String.format("State '%s' is invalid or expired", state));

		super.checkCallbackParameters(provider, state, code, connectParams);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected String doHandleOAuth2Callback(String provider, String code, OAuth2ApiBinding connect,
			Map<String, String> connectParams, HttpServletRequest request) {
		// Got required parameters
		String sourceApp = connectParams.get(config.getParam().getApplication());
		// Authorizers
		String authorizers = connectParams.get(config.getParam().getAuthorizers());
		// Access token
		Oauth2AccessToken ast = connect.getAccessToken(code);
		// User openId
		Oauth2OpenId openId = connect.getUserOpenId(ast);

		// Account by openId
		Parameter parameter = new SnsAuthorizingParameter(provider, openId.openId(), openId.unionId());
		IamPrincipalInfo account = configurer.getIamAccount(parameter);

		// Second authentication assertion
		SecondAuthcAssertModel model = new SecondAuthcAssertModel(sourceApp, provider,
				connectParams.get(config.getParam().getFuncId()));
		try {
			// Assertion
			assertionSecondAuthentication(provider, openId, account, authorizers, connectParams);
			model.setPrincipal(account.getPrincipal());
			model.setValidFromDate(new Date());
		} catch (SecondAuthenticationException e) {
			log.error("Secondary authentication fail", e);
			model.setStatus(e.getStatus());
			model.setErrdesc(e.getMessage());
		}

		/*
		 * Save authenticated to cache.
		 * See:xx.iam.handler.DefaultAuthenticationHandler#secondValidate()
		 */
		String secondAuthCode = generateSecondAuthcCode(sourceApp);
		CacheKey ekey = new CacheKey(secondAuthCode, snsConfig.getOauth2ConnectExpireMs());
		cacheManager.getIamCache(SECOND_AUTHC_CACHE).put(ekey, model);
		log.info("Saved secondary authentication. {}[{}], result[{}]", config.getParam().getSecondAuthCode(), secondAuthCode,
				model);

		return secondAuthCode;
	}

	@Override
	protected String postCallbackResponse(String provider, String secondAuthCode, Map<String, String> connectParams,
			HttpServletRequest request) {
		return secondAuthCode;
	}

	@Override
	protected String decorateCallbackRefreshUrl(String secondAuthCode, Map<String, String> connectParams,
			HttpServletRequest request) {
		StringBuffer url = new StringBuffer(WebUtils2.getRFCBaseURI(request, true));
		url.append(URI_S_SNS_BASE).append("/");
		url.append(URI_S_AFTER_CALLBACK_AGENT).append("?");
		url.append(config.getParam().getSecondAuthCode()).append("=");
		url.append(secondAuthCode);
		return url.toString();
	}

	@Override
	public Which which() {
		return Which.SECOND_AUTH;
	}

	private void assertionSecondAuthentication(String provider, Oauth2OpenId openId, IamPrincipalInfo account, String authorizers,
			Map<String, String> connectParams) {
		// Check authorizer effectiveness
		if (isNull(account) || isBlank(account.getPrincipal())) {
			throw new SecondAuthenticationException(InvalidAuthorizer, format("Invalid authorizer, openId info[%s]", openId));
		}
		// Check authorizer matches
		else {
			List<String> authorizerList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(authorizers);
			if (!authorizerList.contains(account.getPrincipal())) {
				throw new SecondAuthenticationException(IllegalAuthorizer, String.format(
						"Illegal authorizer, Please use [%s] account authorization bound by user [%s]", provider, authorizers));
			}
		}
	}

	/**
	 * Generate second authentication code
	 *
	 * @param application
	 * @return
	 */
	private String generateSecondAuthcCode(String application) {
		return IdGenerators.genVariableMeaningUUID("sac_", 32);
	}

}