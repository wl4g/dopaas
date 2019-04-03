package com.wl4g.devops.iam.sns.web;

import org.apache.shiro.web.util.WebUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.base.Charsets;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.common.utils.web.WebUtils2.ResponseType;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.annotation.SnsController;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
import com.wl4g.devops.iam.config.IamProperties;
import com.wl4g.devops.iam.config.SnsProperties;
import com.wl4g.devops.iam.sns.handler.DelegateSnsHandler;

import static com.wl4g.devops.iam.common.config.AbstractIamProperties.StrategyProperties.DEFAULT_AUTHC_READY_STATUS;
import static com.wl4g.devops.iam.common.config.AbstractIamProperties.StrategyProperties.DEFAULT_SECOND_AUTHC_STATUS;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SNS_CONNECT;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SNS_CALLBACK;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_AFTER_CALLBACK_AGENT;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		String which = WebUtils.getCleanParam(request, config.getParam().getWhich());
		String state = WebUtils.getCleanParam(request, config.getParam().getState());

		// Extra parameters all.(Note: Form submission parameters will be
		// ignored)
		Map<String, String> connectParams = WebUtils2.toQueryParams(request.getQueryString());

		// Getting SNS authorizingUrl
		String authorizingUrl = this.delegate.connect(Which.of(which), provider, state, connectParams);

		// Response type
		String respType = WebUtils.getCleanParam(request, config.getParam().getResponseType());
		if (ResponseType.isJSONResponse(respType, request)) {
			String authorizingMsg = config.getStrategy().makeResponse(RetCode.OK.getCode(), DEFAULT_AUTHC_READY_STATUS,
					"Getting the SNS authorization code is ready.", null);
			this.writeJson(response, authorizingMsg);
		} else {
			/**
			 * Some handler have carried the 'redirect:' prefix
			 */
			if (StringUtils.startsWithIgnoreCase(authorizingUrl, REDIRECT_PREFIX)) {
				WebUtils.issueRedirect(request, response, authorizingUrl.substring(REDIRECT_PREFIX.length()), null, false);
			} else {
				// Return the URL string directly without redirection
				String msg = String.format(
						"<div>Please configure the callback URL on the social network platform <b>%s</b> as follows (note: it's the Wechat official public platform, not an open platform):</div><br/><a style=\"word-break:break-all;\" href=\"%s\" target=\"_blank\">%s</a>",
						provider, authorizingUrl, authorizingUrl);
				this.write(response, HttpServletResponse.SC_OK, MediaType.TEXT_HTML_VALUE, msg.getBytes(Charsets.UTF_8));
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
			log.info("Sns callback url[{}]", WebUtils2.getFullRequestURI(request));
		}

		// Basic parameters
		String which = WebUtils.getCleanParam(request, config.getParam().getWhich());
		String state = WebUtils.getCleanParam(request, config.getParam().getState());

		// Which
		Which wh = Which.safeOf(which);
		Assert.notNull(wh, String.format("'%s' must not be null", config.getParam().getWhich()));

		// Delegate getting redirect refreshUrl
		String redirectRefreshUrl = this.delegate.callback(wh, provider, state, code, request);
		if (log.isInfoEnabled()) {
			log.info("Callback provider[{}], state[{}], url[{}]", provider, state, redirectRefreshUrl);
		}

		// Refresh redirection URL is empty, indicating that no redirection is
		// required for this operation.
		if (StringUtils.isEmpty(redirectRefreshUrl)) {
			// Response JSON of redirection
			String respMsg = config.getStrategy().makeResponse(RetCode.OK.getCode(), DEFAULT_SECOND_AUTHC_STATUS,
					"The second authentication has been successful.", redirectRefreshUrl);
			WebUtils2.writeJson(response, respMsg);
		}
		// Redirection to refresh URL
		else {
			WebUtils.issueRedirect(request, response, WebUtils2.safeDecodeURL(redirectRefreshUrl), null, false);
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
	@GetMapping(URI_AFTER_CALLBACK_AGENT)
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
