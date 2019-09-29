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
package com.wl4g.devops.iam.sns.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Charsets;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.annotation.SnsController;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.config.properties.SnsProperties;
import com.wl4g.devops.iam.sns.handler.DelegateSnsHandler;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.apache.shiro.web.util.WebUtils.issueRedirect;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SNS_CONNECT;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;
import static com.wl4g.devops.common.utils.web.WebUtils2.getFullRequestURI;
import static com.wl4g.devops.common.utils.web.WebUtils2.safeDecodeURL;
import static com.wl4g.devops.common.utils.web.WebUtils2.toQueryParams;
import static com.wl4g.devops.common.utils.web.WebUtils2.ResponseType.isJSONResponse;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SNS_CALLBACK;
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
	 * Connection social networking
	 * 
	 * @param provider
	 *            social platform name
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
	public void connect(@PathVariable(PARAM_SNS_PRIVIDER) String provider, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (log.isInfoEnabled()) {
			log.info("Connecting SNS url[{}]", WebUtils2.getFullRequestURI(request));
		}

		// Basic parameters
		String which = getCleanParam(request, config.getParam().getWhich());
		String state = getCleanParam(request, config.getParam().getState());

		// Extra parameters all.(Note: Form submission parameters will be
		// ignored)
		Map<String, String> connectParams = toQueryParams(request.getQueryString());

		// Getting SNS authorizingUrl
		String authorizingUrl = this.delegate.connect(Which.of(which), provider, state, connectParams);

		// Response type
		if (isJSONResponse(request)) {
			RespBase<String> resp = RespBase.create();
			resp.setCode(RetCode.OK).setStatus(DEFAULT_AUTHC_READY_STATUS)
					.setMessage("Obtain the SNS authorization code is ready.");
			writeJson(response, toJSONString(resp));
		} else {
			/**
			 * Some handler have carried the 'redirect:' prefix
			 */
			if (startsWithIgnoreCase(authorizingUrl, REDIRECT_PREFIX)) {
				issueRedirect(request, response, authorizingUrl.substring(REDIRECT_PREFIX.length()), null, false);
			} else {
				// Return the URL string directly without redirection
				String msg = String.format(
						"<div>Please configure the callback URL on the social network platform <b>%s</b> as follows (note: it's the Wechat official public platform, not an open platform):</div><br/><a style=\"word-break:break-all;\" href=\"%s\" target=\"_blank\">%s</a>",
						provider, authorizingUrl, authorizingUrl);
				write(response, HttpServletResponse.SC_OK, MediaType.TEXT_HTML_VALUE, msg.getBytes(Charsets.UTF_8));
			}
		}
	}

	/**
	 * Unified callback address for social service providers
	 * 
	 * @param provider
	 * @param state
	 */
	@GetMapping("/{" + PARAM_SNS_PRIVIDER + "}/" + URI_S_SNS_CALLBACK)
	public void callback(@PathVariable(PARAM_SNS_PRIVIDER) String provider, @NotBlank @RequestParam(PARAM_SNS_CODE) String code,
			HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (log.isInfoEnabled()) {
			log.info("Sns callback url[{}]", getFullRequestURI(request));
		}

		// Basic parameters
		String which = getCleanParam(request, config.getParam().getWhich());
		String state = getCleanParam(request, config.getParam().getState());

		// Which
		Which wh = Which.safeOf(which);
		Assert.notNull(wh, String.format("'%s' must not be null", config.getParam().getWhich()));

		// Delegate getting redirect refreshUrl
		String redirectRefreshUrl = delegate.callback(wh, provider, state, code, request);
		if (log.isInfoEnabled()) {
			log.info("Callback provider[{}], state[{}], url[{}]", provider, state, redirectRefreshUrl);
		}

		/*
		 * Refresh redirection URL is empty, indicating that no redirection is
		 * required for this operation.
		 */
		if (isBlank(redirectRefreshUrl)) {
			// Response JSON of redirection.
			RespBase<String> resp = RespBase.create(DEFAULT_SECOND_AUTHC_STATUS);
			resp.setCode(RetCode.OK).setMessage("Second authenticate successfully.");
			// resp.setData(singletonMap(config.getParam().getRefreshUrl(),
			// redirectRefreshUrl));
			writeJson(response, toJSONString(resp));
		}
		// Redirection to refresh URL
		else {
			issueRedirect(request, response, safeDecodeURL(redirectRefreshUrl), null, false);
		}

	}

	/**
	 * Intermediate pages handled by agents after SNS callback
	 * 
	 * {@link com.wl4g.devops.iam.sns.web.DefaultOauth2SnsController#callback()}
	 * 
	 * @param response
	 * @param refreshUrl
	 *            Actual after callback refresh URL
	 * @throws IOException
	 */
	@GetMapping(URI_S_AFTER_CALLBACK_AGENT)
	public void afterCallbackAgent(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// Get callback parameters
		Map<String, String> callbackParams = WebUtils2.toQueryParams(request.getQueryString());
		// To JSON text plain
		String attributeJSONString = JacksonUtils.toJSONString(callbackParams).replaceAll("\\\"", "\\\\\"");

		// Build agent HTML
		byte[] agentHtml = String.format(TEMPLATE_CALLBACK_AGENT, attributeJSONString).getBytes(Charsets.UTF_8);
		this.write(response, HttpStatus.OK.value(), MediaType.TEXT_HTML_VALUE, agentHtml);
	}

}