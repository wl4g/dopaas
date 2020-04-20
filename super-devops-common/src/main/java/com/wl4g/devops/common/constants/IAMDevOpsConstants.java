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
package com.wl4g.devops.common.constants;

/**
 * DevOps SCM Constants.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月13日
 * @since
 */
public abstract class IAMDevOpsConstants extends DevOpsConstants {

	//
	// Common and based.
	//

	/**
	 * Of the fast-CAS attribute for remember me authentication (CAS 3.4.10+)
	 */
	final public static String KEY_REMEMBERME_NAME = "longTermAuthenticationRequestTokenUsed";
	/**
	 * Authentication principal language attribute name.
	 */
	final public static String KEY_LANG_ATTRIBUTE_NAME = "principalLangAttributeName";

	/**
	 * This key is generated when the authentication is successful and can be
	 * used to encrypt and decrypt the transmission data of some sensitive api.
	 */
	final public static String KEY_DATA_CIPHER = "dataCipherKey";
	/**
	 * When authentication is successful, a key for the access token is
	 * generated. It is used to enhance session based validation logic (the
	 * original idea came from JWT). In fact, it is the signature of hmacha1
	 * ("signkey", sessionid + UMID). Validation logic: the signature value
	 * calculated by the server is equal to the signature value submitted by the
	 * client, that is, the validation is passed.
	 * 
	 * @see {@link com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties#accessTokenName}
	 * @see {@link com.wl4g.devops.iam.common.mgt.IamSubjectFactory#assertRequestSignTokenValidity}
	 */
	final public static String KEY_ACCESSTOKEN_SIGN = "accessTokenSignKey";
	/**
	 * iamServer/iamClient the JSON node key that response the session
	 * information.
	 */
	final public static String KEY_SESSIONINFO_NAME = "session";

	/** authentication token save session key-name */
	final public static String KEY_AUTHC_TOKEN = "authcTokenAttributeKey";
	/** authentication accountInfo save session key-name */
	final public static String KEY_AUTHC_ACCOUNT_INFO = "authcAccountInfoAttributeKey";

	/**
	 * IAM system service role parameter name.</br>
	 * Can be used for user-client interception of unregistered state
	 * processing.
	 */
	final public static String KEY_SERVICE_ROLE = "serviceRole";

	/**
	 * IAM Client-server interactive authentication CAS protocol URI
	 */
	final public static String URI_AUTHENTICATOR = "/authenticator";

	/**
	 * {@link IamSession} relation attributes cache name.
	 */
	final public static String CACHE_RELATION_ATTRS = ":iam:session:attrs:";

	//
	// Server configuration.
	//

	/**
	 * URI login submission base path for processing all shiro authentication
	 * filters submitted by login.
	 */
	final public static String URI_AUTH_BASE = "/auth/";
	/**
	 * IAM server base URI. You need to ensure synchronization with the
	 * configuration in bootstrap.yml [spring.cloud.devops.iam.filter-chain]
	 */
	final public static String URI_S_BASE = "/internal";
	/** IAM server validate API URI. */
	final public static String URI_S_VALIDATE = "validate";
	/** IAM server logout API URI. */
	final public static String URI_S_LOGOUT = "logout";
	/** IAM server secondary authentication validate API URI. */
	final public static String URI_S_SECOND_VALIDATE = "secondValidate";
	/** IAM server seesions authentication validate API URI. */
	final public static String URI_S_SESSION_VALIDATE = "sessionValidate";

	/**
	 * Callback Processing and Path in third party social networks services
	 */
	final public static String URI_S_SNS_BASE = "/sns";
	/**
	 * SNS connect URI
	 */
	final public static String URI_S_SNS_CONNECT = "connect";
	/**
	 * SNS connect callback URI
	 */
	final public static String URI_S_SNS_CALLBACK = "callback";
	/**
	 * The callback proxy URI is suitable for the qq and sina authorized login
	 * pages of front-end window.open, and the callback proxy processing pages
	 * (closing the child forms and passing callback information to the parent
	 * forms) when the authorization is successful.
	 */
	final public static String URI_S_AFTER_CALLBACK_AGENT = "after_callback_agent";

	/**
	 * WeChat public platform social services receive message URI.
	 */
	final public static String URI_S_WECHAT_MP_RECEIVE = "receive";

	/** Based URI with login authenticator controller. */
	final public static String URI_S_LOGIN_BASE = "/login";
	/**
	 * Pre-processing handshake, e.g, apply sessionKeyId, All clients are
	 * unified, including PC/WEB/iOS/Andriod/WechatMp/WechatApplet
	 */
	final public static String URI_S_LOGIN_HANDSHAKE = "/handshake";
	/**
	 * Initialization before login checks whether authentication code is
	 * enabled, etc.
	 */
	final public static String URI_S_LOGIN_CHECK = "check";
	/** URI for apply for locale. */
	final public static String URI_S_LOGIN_APPLY_LOCALE = "applylocale";
	/**
	 * Get the error information stored in the current session
	 */
	final public static String URI_S_LOGIN_ERRREAD = "errread";

	/** Based URI with verifier authenticator controller. */
	final public static String URI_S_VERIFY_BASE = "/verify";
	/** URI for apply for CAPTCHA. */
	final public static String URI_S_VERIFY_APPLY_CAPTCHA = "applycaptcha";
	/** URI for verify analyze for CAPTCHA. */
	final public static String URI_S_VERIFY_ANALYSIS_CAPTCHA = "verifyanalysis";
	/** URI for apply for verify-code. */
	final public static String URI_S_VERIFY_SMS_APPLY = "applysmsverify";

	/** Based URI with simple risk control controller. */
	final public static String URI_S_RCM_BASE = "/rcm";
	/**
	 * Before requesting authentication, the client needs to submit the device
	 * fingerprint um, UA and other information to obtain the corresponding
	 * token, so as to solve the risk control detection. Note: it is a simple
	 * version of the implementation of risk control inspection. It is
	 * recommended to use a more professional external RiskControlService in the
	 * production environment.
	 */
	final public static String URI_S_RCM_UMTOKEN_APPLY = "applyumtoken";

	/**
	 * Generic API v1 base URL.
	 */
	final public static String URI_S_API_V1_BASE = "/api/v1";
	/**
	 * Generic API v1 sessions list query.
	 */
	final public static String URI_S_API_V1_SESSION = "/sessions";

	/**
	 * IAM server authentication session stored cache name.
	 */
	final public static String CACHE_SESSION = ":iam:session:";
	/**
	 * IAM server authentication authorization information storage cache name.
	 */
	final public static String CACHE_TICKET_S = ":iam:ticket:s:";
	/**
	 * IAM client authentication authorization information storage cache name.
	 */
	final public static String CACHE_TICKET_C = ":iam:ticket:s:";

	/**
	 * Login authentication related processing cache name.
	 */
	final public static String CACHE_SNSAUTH = ":iam:snsauth:";
	/**
	 * IAM server matching CAPTCHA verification failure counter cache name.
	 */
	final public static String CACHE_FAILFAST_CAPTCHA_COUNTER = ":iam:counter:captcha:";
	/**
	 * IAM server matching SMS verification failure counter cache name.
	 */
	final public static String CACHE_FAILFAST_SMS_COUNTER = ":iam:counter:sms:";
	/**
	 * IAM server matching verification failure counter cache name.
	 */
	final public static String CACHE_FAILFAST_MATCH_COUNTER = ":iam:counter:credentials:";
	/**
	 * Login failure overrun, lock cache name.
	 */
	final public static String CACHE_MATCH_LOCK = ":iam:lock:credentials:";
	/**
	 * Security verifier for jigsaw captcha image cache name.
	 */
	final public static String CACHE_VERIFY_JIGSAW_IMG = ":iam:verify:jigsaw:imgs";
	/**
	 * Cryptographic service cache name.
	 */
	final public static String CACHE_CRYPTO = ":iam:crypto:keypairs";
	/** Simple risk control handler umidToken cache key. */
	final public static String CACHE_SIMPLE_RCM_UMIDTOKEN = ":iam:rcm:simpleumidtoken:";

	/**
	 * The public key index by logged-in users
	 */
	final public static String KEY_SECRET_INFO = "applySecretInfo";

	/**
	 * Limiter login failure prefix based on user-name.
	 */
	final public static String KEY_FAIL_LIMIT_UID_PREFIX = "uid_";
	/**
	 * Limiter login failure prefix based on remote IP.
	 */
	final public static String KEY_FAIL_LIMIT_RIP_PREFIX = "rip_";
	/**
	 * Used for record all accounts that have failed to log in in this session.
	 */
	final public static String KEY_FAIL_PRINCIPAL_FACTORS = "failPrincipalFactors";
	/**
	 * Error information for saving iam-related operations to sessions.
	 */
	final public static String KEY_ERR_SESSION_SAVED = "errorTipsInfo";
	/**
	 * IAM system service role: iam-server.</br>
	 * Can be used for user-client interception of unregistered state
	 * processing.
	 */
	final public static String KEY_SERVICE_ROLE_VALUE_IAMSERVER = "IamWithCasAppServer";

	/**
	 * Delegate message source bean name.
	 */
	final public static String BEAN_DELEGATE_MSG_SOURCE = "iamSessionDelegateMessageBundle";

	//
	// Client configuration.
	//

	/**
	 * IAM system service role: iam-client.</br>
	 * Can be used for user-client interception of unregistered state
	 * processing.
	 */
	final public static String KEY_SERVICE_ROLE_VALUE_IAMCLIENT = "IamWithCasAppClient";

	/** Fast-CAS client base URI. */
	final public static String URI_C_BASE = "/internal";
	/** Fast-CAS client logout URI. */
	final public static String URI_C_LOGOUT = "logout";

}