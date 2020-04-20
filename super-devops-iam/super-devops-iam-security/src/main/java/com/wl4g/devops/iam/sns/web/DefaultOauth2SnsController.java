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
package com.wl4g.devops.iam.sns.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.annotation.SnsController;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.config.properties.SnsProperties;
import com.wl4g.devops.iam.sns.CallbackResult;
import com.wl4g.devops.iam.sns.handler.DelegateSnsHandler;

import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.apache.shiro.web.util.WebUtils.issueRedirect;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SNS_CONNECT;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.devops.tool.common.web.WebUtils2.getFullRequestURI;
import static com.wl4g.devops.tool.common.web.WebUtils2.safeDecodeURL;
import static com.wl4g.devops.tool.common.web.WebUtils2.toQueryParams;
import static com.wl4g.devops.tool.common.web.WebUtils2.ResponseType.isJSONResp;
import static java.lang.String.format;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SNS_CALLBACK;
import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_AFTER_CALLBACK_AGENT;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

/**
 * Default oauth2 social networking services controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月2日
 * @since
 */
@SnsController
public class DefaultOauth2SnsController extends AbstractSnsController {

	final public static String DEFAULT_AUTHC_READY_STATUS = "certificateReady";
	final public static String DEFAULT_SECOND_AUTHC_STATUS = "SecondCertifies";

	public DefaultOauth2SnsController(IamProperties config, SnsProperties snsConfig, DelegateSnsHandler delegate) {
		super(config, snsConfig, delegate);
	}

	/**
	 * Request to connect to social service provider preprocessing.</br>
	 * 
	 * <pre>
	 * 示例1：(获取配置微信公众号view类型菜单的URL)
	 * 请求此接口(工具接口)：http://sso.wl4g.com/sso/sns/connect/wechatmp?which=client_auth&service=wechatMp
	 * 
	 * 返回：
	 * https://open.weixin.qq.com/connect/oauth2/authorize?state=a7d2e06d3c05483a8feeaa0bdf37455a&scope=snsapi_userinfo&redirect_uri=http%3A%2F%2Fsso.wl4g.com%2Fsso%2Fsns%2Fwechatmp%2Fcallback%3Fwhich%3Dclient_auth%26service%3DwechatMp&response_type=code&appid=wxec3f74a4062d650f#wechat_redirect
	 * </pre>
	 * 
	 * @param provider
	 *            social networking provider id
	 * @param which
	 *            action
	 * @param state
	 *            Oauth2 protocol state (which can be imported when the WeChat
	 *            public platform is used for manual configuration)
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/" + URI_S_SNS_CONNECT + "/{" + PARAM_SNS_PRIVIDER + "}")
	public void preOAuth2Connect(@PathVariable(PARAM_SNS_PRIVIDER) String provider, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		log.info("Connecting SNS url[{}]", getFullRequestURI(request));

		// Basic parameters
		String which = getCleanParam(request, config.getParam().getWhich());
		String state = getCleanParam(request, config.getParam().getState());

		// Extra parameters all.(Note: Form submission parameters will be
		// ignored)
		Map<String, String> connectParams = toQueryParams(request.getQueryString());

		// Getting SNS authorizingUrl
		String authorizingUrl = delegate.doOAuth2GetAuthorizingUrl(Which.of(which), provider, state, connectParams);

		// Response type
		if (isJSONResp(request)) {
			RespBase<String> resp = RespBase.create();
			resp.setCode(RetCode.OK).setStatus(DEFAULT_AUTHC_READY_STATUS)
					.setMessage("Obtain the SNS authorization code is ready.");
			writeJson(response, toJSONString(resp));
		} else {
			// Some handler have carried the 'redirect:' prefix
			if (startsWithIgnoreCase(authorizingUrl, REDIRECT_PREFIX)) {
				issueRedirect(request, response, authorizingUrl.substring(REDIRECT_PREFIX.length()), null, false);
			} else {
				// Return the URL string directly without redirect
				String msg = format(
						"<p style='text-align:center'>The following is the OAuth2 configuration address, Please configure this URL to the platform.</p><hr/>"
								+ "<p><b>Social Service Provider:</b>&nbsp;%s</p>"
								+ "<p><b>OAuth2 URL:</b>&nbsp;<a style='word-break:break-all;' href='%s' target='_blank'>%s</a></p>",
						provider, authorizingUrl, authorizingUrl);
				write(response, HttpServletResponse.SC_OK, MediaType.TEXT_HTML_VALUE, msg.getBytes(UTF_8));
			}
		}

	}

	/**
	 * Used to process callback request after social service provider completes
	 * oauth2 authorization.</br>
	 * 
	 * <pre>
	 * 示例1：
	 * 
	 * step1: 设置微信公众号view类型菜单的URL，如：
	 * APPID=yours appid
	 * REDIRECT_URL=https://sso.wl4g.com/sso/sns/wechatmp/callback?which=client_auth&state=1
	 * https://open.weixin.qq.com/connect/oauth2/authorize?appid={APPID}&redirect_uri={REDIRECT_URL}&response_type=code&scope=snsapi_base#wechat_redirect
	 * 
	 * step2: 点击它，此时微信将发起回调请求，如：
	 * https://sso.wl4g.com/sso/sns/wechatmp/callback?which=client_auth&state=1&code=011Z9B7G1SkEh60IE38G1jpG7G1Z9B71
	 * 
	 * </pre>
	 * 
	 * @param provider
	 *            social networking provider id
	 * @param code
	 *            oauth2 callback authorization code
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@GetMapping("/{" + PARAM_SNS_PRIVIDER + "}/" + URI_S_SNS_CALLBACK)
	public void postOAuth2Callback(@PathVariable(PARAM_SNS_PRIVIDER) String provider,
			@NotBlank @RequestParam(PARAM_SNS_CODE) String code, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		log.info("Sns callback url[{}]", getFullRequestURI(request));

		// Get required parameters
		Which which = Which.of(getCleanParam(request, config.getParam().getWhich()));
		String state = getCleanParam(request, config.getParam().getState());

		// Do oauth2 callback
		CallbackResult ret = delegate.doOAuth2Callback(which, provider, state, code, request);
		log.info("Callback provider[{}], state[{}], refreshUrl[{}]", provider, state, ret);

		/*
		 * When refresh redirectUrl is blank, indicating that no redirect is
		 * required for this operation.
		 */
		if (ret.hasRefreshUrl()) {
			issueRedirect(request, response, safeDecodeURL(ret.getRefreshUrl()), null, false);
		} else {
			RespBase<String> resp = RespBase.create(DEFAULT_SECOND_AUTHC_STATUS);
			resp.setCode(RetCode.OK).setMessage("Second authenticate successfully.");
			// resp.setData(singletonMap(config.getParam().getRefreshUrl(),
			// redirectRefreshUrl));
			writeJson(response, toJSONString(resp));
		}

	}

	/**
	 * After the SNS callback, it is used to jump to the middle page of the home
	 * page.
	 * <p>
	 * {@link com.wl4g.devops.iam.sns.web.DefaultOauth2SnsController#callback()}
	 *
	 * @param response
	 * @param refreshUrl
	 *            Actual after callback refresh URL
	 * @throws IOException
	 */
	@GetMapping(URI_S_AFTER_CALLBACK_AGENT)
	public void afterOAuth2CallbackAgent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> callbackParams = toQueryParams(request.getQueryString());
		String attributeJsonstr = toJSONString(callbackParams).replaceAll("\\\"", "\\\\\"");

		// Readering agent page
		byte[] agentPageHtml = format(TEMPLATE_CALLBACK_AGENT, attributeJsonstr).getBytes(UTF_8);
		write(response, HttpStatus.OK.value(), MediaType.TEXT_HTML_VALUE, agentPageHtml);
	}

}