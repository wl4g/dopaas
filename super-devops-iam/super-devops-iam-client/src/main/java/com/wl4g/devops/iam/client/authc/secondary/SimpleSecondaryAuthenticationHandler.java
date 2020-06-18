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
package com.wl4g.devops.iam.client.authc.secondary;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_BASE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SECOND_VALIDATE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SNS_BASE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SNS_CONNECT;
import static com.wl4g.devops.common.web.RespBase.RetCode.PRECONDITITE_LIMITED;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.devops.components.tools.common.web.WebUtils2.writeJson;
import static com.wl4g.devops.components.tools.common.web.WebUtils2.ResponseType.isJSONResp;
import static com.wl4g.devops.iam.client.filter.AbstractClientIamAuthenticationFilter.SAVE_GRANT_TICKET;
import static com.wl4g.devops.iam.common.authc.model.SecondAuthcAssertModel.Status.Authenticated;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.apache.shiro.web.util.WebUtils.issueRedirect;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.common.exception.iam.IamException;
import com.wl4g.devops.common.utils.bean.BeanMapConvert;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.iam.client.annotation.SecondaryAuthenticate;
import com.wl4g.devops.iam.common.authc.SecondaryAuthenticationException;
import com.wl4g.devops.iam.common.authc.model.SecondAuthcAssertModel;
import com.wl4g.devops.iam.client.config.IamClientProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;

/**
 * Secondary authentication processor.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年3月9日
 * @since
 */
public class SimpleSecondaryAuthenticationHandler implements SecondaryAuthenticationHandler<SecondaryAuthenticate> {
	protected SmartLogger log = getLogger(getClass());

	/**
	 * IAM client properties configuration
	 */
	final private IamClientProperties config;

	/**
	 * Rest template
	 */
	final private RestTemplate restTemplate;

	/**
	 * Spring container bean factory
	 */
	final private BeanFactory beanFactory;

	public SimpleSecondaryAuthenticationHandler(IamClientProperties config, RestTemplate restTemplate, BeanFactory beanFactory) {
		Assert.notNull(config, "'config' is null, please check configure");
		Assert.notNull(restTemplate, "'restTemplate' is null, please check configure");
		Assert.notNull(beanFactory, "'beanFactory' is null, please check configure");
		this.config = config;
		this.restTemplate = restTemplate;
		this.beanFactory = beanFactory;
	}

	@Override
	public Object doIntercept(ProceedingJoinPoint jp, SecondaryAuthenticate annotation) throws Throwable {
		// Get required request response
		RequestResponse http = getRequestResponse(jp);

		// Get second authenticate code
		String authCode = http.getRequest().getParameter(config.getParam().getSecondaryAuthCode());

		// Validation
		String errdesc = null;
		try {
			doRemoteValidate(authCode, annotation);
			return jp.proceed(); // Second authenticated, be pass
		} catch (Exception e) {
			errdesc = e.getMessage();
			log.warn("Secondary authentication failed. {}", errdesc);
		}

		// Redirecting secondary authentication URLs
		String redirectUrl = buildConnectAuthenticatingUrl(http, annotation);

		// Response JSON message
		if (isJSONResp(http.getRequest())) {
			RespBase<String> resp = new RespBase<>(PRECONDITITE_LIMITED, STATUS_SECOND_UNAUTHC, MSG_SECOND_UNAUTHC, null);
			resp.forMap().put(config.getParam().getRedirectUrl(), redirectUrl);
			resp.setMessage(errdesc);
			writeJson(http.getResponse(), toJSONString(resp));
		}
		// Redirection
		else {
			issueRedirect(http.getRequest(), http.getResponse(), redirectUrl, null, false);
		}

		return null;
	}

	/**
	 * Get HTTP request and response parameter
	 * 
	 * @param jp
	 * @return
	 */
	private RequestResponse getRequestResponse(ProceedingJoinPoint jp) {
		HttpServletRequest request = null;
		HttpServletResponse response = null;
		for (Object arg : jp.getArgs()) {
			if (arg instanceof HttpServletRequest) {
				request = (HttpServletRequest) arg;
			}
			if (arg instanceof HttpServletResponse) {
				response = (HttpServletResponse) arg;
			}
			if (request != null && response != null) {
				break;
			}
		}
		// Check request and response
		Assert.state((request != null && response != null),
				String.format(
						"The controller method marked @%s must have the HttpServletRequest and HttpServletResponse parameter",
						SecondaryAuthenticate.class.getSimpleName()));
		return new RequestResponse(request, response);
	}

	/**
	 * Building connect authenticating redirect URL
	 * 
	 * @param http
	 * @param annotation
	 * @return
	 */
	private String buildConnectAuthenticatingUrl(RequestResponse http, SecondaryAuthenticate annotation) {
		StringBuffer url = new StringBuffer(config.getServerUri()); // ???
		url.append(URI_S_SNS_BASE).append("/");
		url.append(URI_S_SNS_CONNECT).append("/");
		url.append(config.getSecondaryAuthenticatorProvider()).append("?");

		// Parameter 'which'
		Map<String, Object> param = new HashMap<>();
		param.put(config.getParam().getWhich(), Which.SECOND_AUTH.name().toLowerCase());
		// Parameter 'authorizers'
		param.put(config.getParam().getAuthorizers(), getAuthorizersString(annotation));
		// Parameter 'application'
		param.put(config.getParam().getApplication(), config.getServiceName());
		// Parameter 'agent'
		param.put(config.getParam().getAgent(), "yes");
		// Parameter 'funcId'
		param.put(config.getParam().getFuncId(), annotation.funcId());
		// Parameter 'state'
		param.put(config.getParam().getState(), "");

		// To URLs
		return url.append(BeanMapConvert.toUriParmaters(param)).toString();
	}

	/**
	 * Building validation internal URL
	 * 
	 * @param authCode
	 * @return
	 */
	private String buildValidateUrl(String authCode) {
		StringBuffer url = new StringBuffer(config.getServerUri());
		url.append(URI_S_BASE).append("/");
		url.append(URI_S_SECOND_VALIDATE).append("?");

		// Parameter 'secondAuthCode'
		Map<String, Object> param = new HashMap<>();
		param.put(config.getParam().getSecondaryAuthCode(), authCode);

		// Parameter 'grantTicket', (Prevent login failure)
		String grantTicket = (String) SecurityUtils.getSubject().getSession().getAttribute(SAVE_GRANT_TICKET);
		Assert.state(StringUtils.hasText(grantTicket),
				String.format("'%s' is empty, please check configure", config.getParam().getGrantTicket()));
		param.put(config.getParam().getGrantTicket(), grantTicket);

		// Parameter 'application'
		param.put(config.getParam().getApplication(), config.getServiceName());

		// To URLs
		return url.append(BeanMapConvert.toUriParmaters(param)).toString();
	}

	/**
	 * Perform remote secondary authentication
	 * 
	 * @param authCode
	 */
	private void doRemoteValidate(String authCode, SecondaryAuthenticate annotation) {
		if (StringUtils.isEmpty(authCode)) {
			throw new SecondaryAuthenticationException("Empty second authentication code");
		}

		// Validation URL
		String validateUrl = buildValidateUrl(authCode);
		// Request remote
		RespBase<SecondAuthcAssertModel> resp = restTemplate.exchange(validateUrl.toString(), HttpMethod.GET, null,
				new ParameterizedTypeReference<RespBase<SecondAuthcAssertModel>>() {
				}).getBody();

		// Check successful
		if (RespBase.isSuccess(resp)) {
			SecondAuthcAssertModel assertion = resp.getData();
			if (!(assertion != null && assertion.getStatus() != null && assertion.getStatus() == Authenticated
					&& valueOf(assertion.getFunctionId()).equals(annotation.funcId()))) {
				throw new SecondaryAuthenticationException(assertion.getErrdesc());
			}
		} else {
			throw new IamException(format("Secondary authc error. %s", toJSONString(resp)));
		}
	}

	/**
	 * Getting security authorizers(principals) string
	 * 
	 * @param annotation
	 * @return
	 */
	private String getAuthorizersString(SecondaryAuthenticate annotation) {
		try {
			SecondaryAuthenticator handler = beanFactory.getBean(annotation.handleClass());
			return StringUtils.arrayToDelimitedString(handler.doGetAuthorizers(annotation.funcId()), ",");
		} catch (NoSuchBeanDefinitionException e) {
			throw new NoSuchBeanDefinitionException(SecondaryAuthenticator.class,
					String.format("No such beans instance, check that the configuration has implemented the %s interface",
							annotation.handleClass().getSimpleName()));
		}
	}

	/**
	 * HTTP request and response pair bean
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年2月28日
	 * @since
	 */
	public static class RequestResponse {

		final private HttpServletRequest request;
		final private HttpServletResponse response;

		public RequestResponse(HttpServletRequest request, HttpServletResponse response) {
			super();
			this.request = request;
			this.response = response;
		}

		public HttpServletRequest getRequest() {
			return request;
		}

		public HttpServletResponse getResponse() {
			return response;
		}

	}

	/**
	 * Error message without secondary authentication.
	 */
	final public static String MSG_SECOND_UNAUTHC = "Without secondary authentication, redirect the specified external URL for authentication";

	/**
	 * Error status without secondary Unauthenticated.
	 */
	final public static String STATUS_SECOND_UNAUTHC = "SecondaryUnauthenticated";

}