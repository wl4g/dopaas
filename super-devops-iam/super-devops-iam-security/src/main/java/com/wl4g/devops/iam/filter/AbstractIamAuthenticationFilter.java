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
package com.wl4g.devops.iam.filter;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.iam.common.utils.AuthenticatingUtils.*;
import static com.wl4g.devops.common.web.RespBase.RetCode.*;
import static com.wl4g.devops.iam.common.utils.AuthenticatingUtils.correctAuthenticaitorURI;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.bind;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.bindKVParameters;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.extParameterValue;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.unbind;
import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static com.wl4g.devops.tool.common.lang.Exceptions.getRootCausesString;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.devops.tool.common.web.WebUtils2.*;
import static com.wl4g.devops.tool.common.web.WebUtils2.cleanURI;
import static com.wl4g.devops.tool.common.web.WebUtils2.getBaseURIForDefault;
import static com.wl4g.devops.tool.common.web.WebUtils2.getHttpRemoteAddr;
import static com.wl4g.devops.tool.common.web.WebUtils2.getRFCBaseURI;
import static com.wl4g.devops.tool.common.web.WebUtils2.safeEncodeURL;
import static com.wl4g.devops.tool.common.web.WebUtils2.toQueryParams;
import static com.wl4g.devops.tool.common.web.WebUtils2.writeJson;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;
import static org.apache.shiro.web.util.WebUtils.issueRedirect;
import static org.apache.shiro.web.util.WebUtils.toHttp;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;

import com.wl4g.devops.common.bean.iam.ApplicationInfo;
import com.wl4g.devops.common.exception.iam.AccessRejectedException;
import com.wl4g.devops.common.exception.iam.IamException;
import com.wl4g.devops.common.exception.iam.IllegalRequestException;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken;
import com.wl4g.devops.iam.authc.ClientSecretIamAuthenticationToken;
import com.wl4g.devops.iam.common.authc.AbstractIamAuthenticationToken.RedirectInfo;
import com.wl4g.devops.iam.common.cache.IamCacheManager;
import com.wl4g.devops.iam.common.filter.IamAuthenticationFilter;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.configure.ServerSecurityConfigurer;
import com.wl4g.devops.iam.configure.ServerSecurityCoprocessor;
import com.wl4g.devops.iam.crypto.SecureCryptService;
import com.wl4g.devops.iam.crypto.SecureCryptService.SecureAlgKind;
import com.wl4g.devops.iam.handler.AuthenticationHandler;
import com.wl4g.devops.tool.common.log.SmartLogger;

import static com.wl4g.devops.tool.common.web.WebUtils2.ResponseType.*;

import java.io.IOException;
import java.net.URI;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.apache.commons.codec.binary.Hex.*;

import org.apache.commons.codec.DecoderException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Multiple channel login authentication submitted processing based filter
 * <p>
 * {@link org.apache.shiro.web.filter.mgt.DefaultFilterChainManager#proxy()}
 * {@link org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver#pathMatches()}
 * {@link org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver#getChain()}
 * {@link org.apache.shiro.web.servlet.AbstractShiroFilter#getExecutionChain()}
 * {@link org.apache.shiro.web.servlet.AbstractShiroFilter#executeChain()}
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月27日
 * @since
 */
public abstract class AbstractIamAuthenticationFilter<T extends IamAuthenticationToken> extends AuthenticatingFilter
		implements IamAuthenticationFilter {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * IAM server configuration properties
	 */
	@Autowired
	protected IamProperties config;

	/**
	 * IAM authentication handler
	 */
	@Autowired
	protected AuthenticationHandler authHandler;

	/**
	 * IAM security configure handler
	 */
	@Autowired
	protected ServerSecurityConfigurer configurer;

	/**
	 * IAM server security processor
	 */
	@Autowired
	protected ServerSecurityCoprocessor coprocessor;

	/**
	 * Secure asymmetric cryptic service.
	 */
	@Autowired
	protected GenericOperatorAdapter<SecureAlgKind, SecureCryptService> cryptAdapter;

	/**
	 * Enhanced cache manager.
	 */
	@Autowired
	protected IamCacheManager cacheManager;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MSG_SOURCE)
	protected SessionDelegateMessageBundle bundle;

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		/*
		 * The request path must be /login/general or /login/sms, etc. Just
		 * return to false directly, that is, let it execute
		 * this#onAccessDenied()
		 */
		return false;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		log.debug("Access denied url: {}", toHttp(request).getRequestURI());
		return executeLogin(request, response);
	}

	@Override
	protected IamAuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest req = toHttp(request);
		HttpServletResponse resp = toHttp(response);
		/*
		 * Pre-handler before authentication, For example, the implementation of
		 * restricting client IP white-list to prevent violent cracking of large
		 * number of submission login requests.
		 */
		if (!coprocessor.preCreateToken(this, req, resp)) {
			throw new AccessRejectedException(format("Access rejected of remoteIp: %s", getHttpRemoteAddr(req)));
		}

		// Success redirection.
		RedirectInfo redirect = getRedirectInfo(request);
		/**
		 * Remember redirect parameters.</br>
		 * e.g. Android submit login will bring redirectUrl and application,
		 * which will be used for successful login redirection.
		 */
		rememberRedirectInfo(redirect, request, response);

		// Remote client address.
		String clientRemoteAddr = getHttpRemoteAddr(req);

		// Create authentication token
		return doCreateToken(clientRemoteAddr, redirect, req, resp);
	}

	protected abstract T doCreateToken(String remoteHost, RedirectInfo redirectInfo, HttpServletRequest request,
			HttpServletResponse response) throws Exception;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response)
			throws Exception {
		try {
			IamAuthenticationToken tk = (IamAuthenticationToken) token;

			/*
			 * Save the token at the time of authentication, which can then be
			 * used for extended logic usage.
			 */
			// bind(KEY_AUTHC_TOKEN, tk);

			// Determine(decorate) redirectUrl(authenticator URL).
			RedirectInfo redirect = determineSuccessRedirect(getRedirectInfo(request), tk, subject, request, response);

			// Granting ticket.
			String grantTicket = authHandler.loggedin(redirect.getFromAppName(), subject).getGrantTicket();

			// Build response parameter.
			Map params = new HashMap();
			params.put(config.getParam().getApplication(), redirect.getFromAppName());

			// Response with JSON?
			if (isJSONResponse(request)) {
				try {
					// Make logged JSON.
					Map jsonParams = new HashMap(params);
					RespBase<String> resp = makeLoggedResponse(token, subject, request, response, grantTicket,
							redirect.getRedirectUrl(), jsonParams);

					// Call authenticated success.
					coprocessor.postAuthenticatingSuccess(tk, subject, toHttp(request), toHttp(response), resp.asMap());

					String logged = toJSONString(resp);
					log.info("Response to success - {}", logged);

					writeJson(toHttp(response), logged);
				} catch (IOException e) {
					log.error("Login success response json error", e);
				}
			} else { // Redirection page?
				/*
				 * When the source application exists, the indication is that it
				 * needs to be redirected to the CAS client application, then
				 * grantTicket is required.
				 */
				if (!isBlank(grantTicket)) {
					params.put(config.getParam().getGrantTicket(), grantTicket);
				}

				// Call success handle.
				coprocessor.postAuthenticatingSuccess(tk, subject, toHttp(request), toHttp(response), params);

				// Sets secret tokens to cookies.
				setSuccessSecretTokens2Cookie(token, request, response);

				log.info("Redirect to successUrl '{}', param:{}", redirect.getRedirectUrl(), params);
				issueRedirect(request, response, redirect.getRedirectUrl(), params, true);
			}

		} catch (IamException e) {
			// Convert to authentication exception before it can be intercepted.
			// See:org.apache.shiro.web.filter.authc.AuthenticatingFilter#executeLogin
			throw new AuthenticationException(e);
		} finally {
			cleanup(token, subject, request, response); // Cleanup
		}

		// Redirection has been responded and no further execution is required.
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request,
			ServletResponse response) {
		IamAuthenticationToken tk = (IamAuthenticationToken) token;

		String errmsg = getRootCausesString(ae);
		if (isNotBlank(errmsg)) {
			String tip = String.format("Failed to authentication of token: %s", token);
			if (log.isDebugEnabled()) {
				log.debug(tip, ae);
			} else {
				log.warn(tip + ", caused by: {}", errmsg);
			}
			/**
			 * {@link LoginAuthenticatorController#errReads()}
			 */
			bind(KEY_ERR_SESSION_SAVED, errmsg);
		}
		// Failure redirect
		RedirectInfo redirect = determineFailureRedirect(getRedirectInfo(request), tk, ae, request, response);

		// Post handling of authentication failure.
		coprocessor.postAuthenticatingFailure(tk, ae, request, response);

		// Obtain bound parameters.
		Map params = new HashMap();
		params.put(config.getParam().getApplication(), redirect.getFromAppName());
		params.put(config.getParam().getRedirectUrl(), redirect.getRedirectUrl());

		// Response JSON message.
		if (isJSONResponse(request)) {
			try {
				RespBase<String> resp = makeFailedResponse(redirect.getRedirectUrl(), request, params, errmsg);
				String failed = toJSONString(resp);
				log.info("Resp unauth: {}", failed);
				writeJson(toHttp(response), failed);
			} catch (IOException e) {
				log.error("Error resp unauth", e);
			}
		}
		// Redirect the login page directly.
		else {
			try {
				log.info("Redirect to login: {}", redirect);
				issueRedirect(request, response, redirect.getRedirectUrl(), params, true);
			} catch (IOException e1) {
				log.error("Redirect to login failed.", e1);
			}
		}

		// Redirection has been responded and no further execution is required.
		return false;
	}

	/**
	 * Determine is the JSON interactive strategy, or get it from
	 * session(flexible API).
	 *
	 * @param request
	 * @return
	 */
	protected boolean isJSONResponse(ServletRequest request) {
		// Using last saved parameters.
		String respTypeValue = extParameterValue(KEY_REQ_AUTH_PARAMS, DEFAULT_RESPTYPE_NAME);
		log.debug("Using last response type:{}", respTypeValue);
		return isJSONResp(respTypeValue, toHttp(request)) || isJSONResp(toHttp(request));
	}

	/**
	 * Get redirect information bound from authentication request
	 * {@link AuthenticatorAuthenticationFilter#rememberRedirectInfo(ServletRequest, ServletResponse)}
	 * </br>
	 * {@link AbstractIamAuthenticationFilter#createToken(ServletRequest, ServletResponse)}
	 *
	 * @param request
	 *            Whether to get only the redirection information of the binding
	 * @return
	 */
	protected RedirectInfo getRedirectInfo(ServletRequest request) {
		// Gets request redirect
		String fromAppName = getCleanParam(request, config.getParam().getApplication());
		String redirectUrl = getCleanParam(request, config.getParam().getRedirectUrl());
		String fallbackRedirect = getCleanParam(request, config.getParam().getFallbackRedirect());
		RedirectInfo redirect = RedirectInfo.build(fromAppName, redirectUrl, fallbackRedirect);

		// Fallback redirect
		if (isBlank(redirect.getFromAppName())) {
			RedirectInfo bind = extParameterValue(KEY_REQ_AUTH_PARAMS, KEY_REQ_AUTH_REDIRECT);
			if (nonNull(bind)) {
				redirect = bind;
			}
		}

		// Check and use default redirectUrl
		if (!isBlank(redirect.getFromAppName())) {
			ApplicationInfo appInfo = configurer.getApplicationInfo(redirect.getFromAppName());
			if (isNull(appInfo)) {
				throw new IllegalRequestException(format("Invalid redirected application '%s'", redirect.getFromAppName()));
			}
			if (isBlank(redirect.getRedirectUrl())) {
				// Use default redirectUrl
				redirect.setRedirectUrl(appInfo.getViewExtranetBaseUri());
			}
			return redirect;
		}
		return new RedirectInfo();
	}

	/**
	 * Saved the latest redirect info, such as response_type, source
	 * application, etc.</br>
	 * E.G.:</br>
	 * </br>
	 *
	 * <b>Req1：</b>http://localhost:14040/iam-server/view/login.html?service=iam-example&redirect_url=http://localhost:14041/iam-example/index.html</br>
	 * <b>Resp1：</b>login.html</br>
	 * </br>
	 * <b>Req2：(Intercepted by
	 * rootFilter)</b>http://localhost:14040/iam-server/favicon.ico</br>
	 * <b>Resp2：</b>
	 * 302->http://localhost:14040/iam-server/view/login.html?service=iam-example&redirect_url=http://localhost:14041/iam-example/index.html</br>
	 * </br>
	 * <b>Req3：</b>http://localhost:14040/iam-server/view/login.html</br>
	 * </br>
	 * <p>
	 * No parameters for the second request for login.html ??? This is the
	 * problem to be solved by this method.
	 *
	 * @param redirect
	 * @param request
	 * @param response
	 */
	private void rememberRedirectInfo(RedirectInfo redirect, ServletRequest request, ServletResponse response) {
		notNull(redirect, "Redirect info must not be null.");
		// Safety encoding for URL fragment.
		redirect.setRedirectUrl(safeEncodeParameterRedirectUrl(redirect.getRedirectUrl()));

		// Response type.
		String respTypeKey = DEFAULT_RESPTYPE_NAME;
		String respType = getCleanParam(request, respTypeKey);

		// Overlay to save the latest parameters.
		bindKVParameters(KEY_REQ_AUTH_PARAMS, respTypeKey, respType, KEY_REQ_AUTH_REDIRECT, redirect);

		log.debug("Binding for respType[{}], redirect[{}]", respType, redirect);
	}

	/**
	 * The redirection URI of the secure encoding loop is mainly used to prevent
	 * the loss of fragments such as for example "/#/index". </br>
	 * e.g.
	 *
	 * <pre>
	 * http://mydomain.com/iam-example/authenticator?redirect_url=http://mydomain.com/#/index
	 * => http://mydomain.com/iam-example/authenticator?redirect_url=http%3A%2F%2Fmydomain.com%2F%23%2Findex
	 * </pre>
	 *
	 * @param fullRedirectUrl
	 *            Full redirect URL
	 * @return
	 */
	private String safeEncodeParameterRedirectUrl(String fullRedirectUrl) {
		if (!isBlank(fullRedirectUrl)) {
			// To prevent automatic loss, such as the anchor part of "#".
			URI uri = URI.create(fullRedirectUrl);
			Map<String, String> params = toQueryParams(uri.getQuery() + "#" + uri.getFragment());
			if (!isEmpty(params)) {
				String clientRedirectUrl = params.get(config.getParam().getRedirectUrl());
				if (!isBlank(clientRedirectUrl)) {
					params.remove(config.getParam().getRedirectUrl());
					params.put(config.getParam().getRedirectUrl(), safeEncodeURL(clientRedirectUrl));
					String newRedirectUrl = getBaseURIForDefault(uri.getScheme(), uri.getHost(), uri.getPort()) + uri.getPath();
					fullRedirectUrl = applyQueryURL(newRedirectUrl, params);
				}
			}
		}
		return fullRedirectUrl;
	}

	/**
	 * Cleaning-up.(e.g.Save request parameters)
	 *
	 * @param token
	 * @param subject
	 * @param request
	 * @param response
	 */
	private void cleanup(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) {
		/*
		 * Clean See:AuthenticatorAuthenticationFilter#bindRequestParameters()
		 */
		unbind(KEY_REQ_AUTH_PARAMS);

		/*
		 * Clean error messages. See:i.w.DiabloExtraController#errReads()
		 */
		unbind(KEY_ERR_SESSION_SAVED);
	}

	/**
	 * Make logged-in response message.
	 *
	 * @param token
	 * @param subject
	 * @param request
	 *            Servlet request
	 * @param fromAppName
	 *            from application name
	 * @param grantTicket
	 *            logged information model
	 * @param callbackUrl
	 *            login success redirect URL
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected RespBase<String> makeLoggedResponse(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response, String grantTicket, String callbackUrl, Map params) throws Exception {
		hasTextOf(callbackUrl, "successCallbackUrl");

		// Redirection URL
		String successRedirectUrl = callbackUrl;
		if (isNotBlank(grantTicket)) {
			successRedirectUrl = applyQueryURL(callbackUrl, singletonMap(config.getParam().getGrantTicket(), grantTicket));
		}

		// Generate absoulte full redirectUrl.
		String fullRedirectUrl = successRedirectUrl.toString();
		if (startsWith(fullRedirectUrl, "/")) { // Relative path?
			fullRedirectUrl = getRFCBaseURI(toHttp(request), true) + successRedirectUrl;
		}

		// Placing it in http.body makes it easier for Android/iOS
		// to get token.
		params.put(config.getParam().getRedirectUrl(), fullRedirectUrl);
		params.put(KEY_SERVICE_ROLE, KEY_SERVICE_ROLE_VALUE_IAMSERVER);

		// Handling secret tokens
		postHandleSuccessSecretTokens(token, subject, params, request, response);

		// Make message
		RespBase<String> resp = RespBase.create(SESSION_STATUS_AUTHC);
		resp.setCode(OK).setMessage("Authentication successful");
		resp.forMap().putAll(params);
		return resp;
	}

	/**
	 * Make login failed response message.
	 *
	 * @param failRedirectUrl
	 *            failure redirect URL
	 * @param request
	 *            Servlet request
	 * @param params
	 *            Redirected query configuration parameters
	 * @param thw
	 *            Exception object
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected RespBase<String> makeFailedResponse(String failRedirectUrl, ServletRequest request, Map params, String errmsg) {
		errmsg = (isNotBlank(errmsg)) ? errmsg : "Authentication failure";

		// Make message
		RespBase<String> resp = RespBase.create(sessionStatus());
		resp.setCode(UNAUTHC).setMessage(errmsg);
		resp.forMap().putAll(params);
		resp.forMap().put(config.getParam().getRedirectUrl(), failRedirectUrl);
		resp.forMap().put(KEY_SERVICE_ROLE, KEY_SERVICE_ROLE_VALUE_IAMSERVER);
		return resp;
	}

	/**
	 * Determine the URL of the login success redirection, default: successURL,
	 * can support customization.
	 *
	 * @param redirect
	 *            First get from the binding {@link RedirectInfo}
	 * @param token
	 * @param subject
	 * @param request
	 * @param response
	 * @return
	 */
	protected RedirectInfo determineSuccessRedirect(RedirectInfo redirect, IamAuthenticationToken token, Subject subject,
			ServletRequest request, ServletResponse response) {

		// Unvalidity using to default
		if (isBlank(redirect.getFromAppName())) {
			redirect.setFromAppName(config.getSuccessService());
			redirect.setRedirectUrl(getSuccessUrl());
		}

		// after expand determine successUrl.
		String determinedRedirectUrl = configurer.decorateAuthenticateSuccessUrl(redirect.getRedirectUrl(), token, subject,
				request, response);
		redirect.setRedirectUrl(correctAuthenticaitorURI(URI.create(determinedRedirectUrl).toString())); // Check-symbol
		hasText(redirect.getRedirectUrl(), "Success redirectUrl empty, please check the configure");
		hasText(redirect.getFromAppName(), "Success application empty, please check the configure");
		return redirect;
	}

	/**
	 * Determine the URL of the login failure redirection, default: loginURL,
	 * can support customization.
	 *
	 * @param redirect
	 *            First get from the binding {@link RedirectInfo}
	 * @param token
	 * @param ae
	 * @param request
	 * @param response
	 * @return
	 */
	protected RedirectInfo determineFailureRedirect(RedirectInfo redirect, IamAuthenticationToken token,
			AuthenticationException ae, ServletRequest request, ServletResponse response) {
		if (isBlank(redirect.getRedirectUrl())) {
			redirect.setRedirectUrl(getLoginUrl());
		}

		// after expand determine failure loginUrl.
		String determinedLoginUrl = configurer.decorateAuthenticateFailureUrl(redirect.getRedirectUrl(), token, ae, request,
				response);
		redirect.setRedirectUrl(cleanURI(determinedLoginUrl)); // Check-symbol
		// hasTextOf(redirect.getFromAppName(), "application"); //Probably-empty
		hasText(redirect.getRedirectUrl(), "Failure redirectUrl empty, please check the configure");
		return redirect;
	}

	/**
	 * Post secret and tokens/signature handling.
	 * 
	 * @param token
	 * @param subject
	 * @param params
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void postHandleSuccessSecretTokens(AuthenticationToken token, Subject subject, Map params, ServletRequest request,
			ServletResponse response) throws Exception {

		// Sets secret tokens to cookies.
		String[] tokens = setSuccessSecretTokens2Cookie(token, request, response);

		// Sets secret tokens to ressponse.
		params.put(config.getParam().getDataCipherKeyName(), tokens[0]);
		params.put(config.getParam().getAccessTokenName(), tokens[1]);
	}

	/**
	 * Sets secret and tokens/signature to cookies handling.
	 * 
	 * @param token
	 * @param request
	 * @param response
	 * @return
	 * @throws DecoderException
	 */
	protected String[] setSuccessSecretTokens2Cookie(AuthenticationToken token, ServletRequest request, ServletResponse response)
			throws DecoderException {

		String dataCipherKeyHex = null, accessToken = null;
		if (token instanceof ClientSecretIamAuthenticationToken) {
			// Sets dataCipherKey
			if (config.getCipher().isEnableDataCipher()) {
				// Gets SecureCryptService.
				SecureAlgKind kind = ((ClientSecretIamAuthenticationToken) token).getSecureAlgKind();
				SecureCryptService cryptService = cryptAdapter.forOperator(kind);

				// Use clientSecretKey(hexPublicKey) to encrypt the newly
				// generate symmetric dataCipherKey
				String clientSecretKey = ((ClientSecretIamAuthenticationToken) token).getClientSecretKey();
				// Encryption dataCipherKey by clientSecretKey.
				KeySpec pubKeySpec = cryptService.generatePubKeySpec(decodeHex(clientSecretKey.toCharArray()));
				// New generate dataCipherKey.
				String hexDataCipherKey = bind(KEY_DATA_CIPHER, generateDataCipherKey());
				dataCipherKeyHex = cryptService.encrypt(pubKeySpec, hexDataCipherKey);

				// Set to cookies
				Cookie c = new SimpleCookie(config.getCookie());
				c.setName(config.getParam().getDataCipherKeyName());
				c.setValue(dataCipherKeyHex);
				c.saveTo(toHttp(request), toHttp(response));
			}

			// Sets accessToken
			if (config.getSession().isEnableAccessTokenValidity()) {
				// Create accessTokenSignKey.
				String accessTokenSignKey = bind(KEY_ACCESSTOKEN_SIGN, generateAccessTokenSignKey(getSessionId()));
				accessToken = generateAccessToken(getSessionId(), accessTokenSignKey);
				Cookie c = new SimpleCookie(config.getCookie());
				c.setName(config.getParam().getAccessTokenName());
				c.setValue(accessToken);
				c.saveTo(toHttp(request), toHttp(response));
			}

		}

		return new String[] { dataCipherKeyHex, accessToken };
	}

	/**
	 * Get filter name
	 */
	public abstract String getName();

	/**
	 * Login/Authentication request parameters binding session key
	 */
	final public static String KEY_REQ_AUTH_PARAMS = "REQ_AUTH_PARAMS";

	/**
	 * Authentication request redirect information key.
	 */
	final public static String KEY_REQ_AUTH_REDIRECT = "REQ_AUTH_REDIRECT";

	/**
	 * URI login submission base path for processing all SHIRO authentication
	 * filters submitted by login
	 */
	final public static String URI_BASE_MAPPING = URI_AUTH_BASE;

}