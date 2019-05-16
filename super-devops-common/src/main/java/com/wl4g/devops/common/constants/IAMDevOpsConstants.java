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
package com.wl4g.devops.common.constants;

import com.google.common.base.Charsets;
import org.apache.commons.codec.binary.Hex;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
	 * Get server validation response ticket assert key(Abbreviation:
	 * ticket-assert).
	 */
	final public static String KEY_TICKET_ASSERT = "ticketAssert";
	/**
	 * Get server validation response secondary authentication assert key.
	 */
	final public static String KEY_SECOND_AUTH_ASSERT = "secondAuthAssert";
	/**
	 * Get server validation response sessions expire assert key.
	 */
	final public static String KEY_SESSION_VALID_ASSERT = "sessionValidAssert";
	/**
	 * Fast-Cas remote logout result information.
	 */
	final public static String KEY_LOGOUT_INFO = "logoutInfo";
	/**
	 * Of the fast-CAS attribute for remember me authentication (CAS 3.4.10+)
	 */
	final public static String KEY_REMEMBERME_NAME = "longTermAuthenticationRequestTokenUsed";
	/**
	 * Of the principal corresponding role attributeKey name returned
	 * successfully by fast-CAS server authentication
	 */
	final public static String KEY_ROLE_ATTRIBUTE_NAME = "authzPrincipalRoleAttributeName";
	/**
	 * Of the principal corresponding permission attributeKey name returned
	 * successfully by fast-CAS server authentication
	 */
	final public static String KEY_PERMIT_ATTRIBUTE_NAME = "authzPrincipalPermisstionAttributeName";

	/**
	 * IAM Client-server interactive authentication CAS protocol URI
	 */
	final public static String URI_AUTHENTICATOR = "/authenticator";

	//
	// Server configuration.
	//

	/**
	 * URI login submission base path for processing all shiro authentication
	 * filters submitted by login.
	 */
	final public static String URI_LOGIN_SUBMISSION_BASE = "/login-submission/";
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
	final public static String URI_S_SECOND_VALIDATE = "second-validate";
	/** IAM server seesions authentication validate API URI. */
	final public static String URI_S_SESSION_VALIDATE = "session-validate";

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
	final public static String URI_AFTER_CALLBACK_AGENT = "after_callback_agent";
	/**
	 * WeChat public platform social services receive message URI.
	 */
	final public static String URI_S_WECHAT_MP_RECEIVE = "receive";
	/** Based URI with extra needed verification code etc. */
	final public static String URI_S_EXT_BASE = "/ext";
	/**
	 * Initialization before login checks whether authentication code is
	 * enabled, etc.
	 */
	final public static String URI_S_EXT_CHECK = "check";
	/** URI for apply for CAPTCHA. */
	final public static String URI_S_EXT_CAPTCHA_APPLY = "captcha-apply";
	/** URI for apply for verify-code. */
	final public static String URI_S_EXT_VERIFY_APPLY = "verifycode-apply";
	/** URI for apply for locale. */
	final public static String URI_S_EXT_LOCALE_APPLY = "locale-apply";
	/**
	 * Get the error information stored in the current session
	 */
	final public static String URI_S_EXT_ERRREAD = "errread";

	/**
	 * IAM server authentication session stored cache name.
	 */
	final public static String CACHE_SESSION = "session_";
	/**
	 * IAM server authentication authorization information storage cache name.
	 */
	final public static String CACHE_TICKET_S = "ticket_s_";
	/**
	 * IAM client authentication authorization information storage cache name.
	 */
	final public static String CACHE_TICKET_C = "ticket_c_";
	/**
	 * Login authentication related processing cache name.
	 */
	final public static String CACHE_SNSAUTH = "snsauth_";
	/**
	 * IAM server matching CAPTCHA verification failure counter cache name.
	 */
	final public static String CACHE_FAILFAST_CAPTCHA_COUNTER = "captcha_counter_";
	/**
	 * IAM server matching SMS verification failure counter cache name.
	 */
	final public static String CACHE_FAILFAST_SMS_COUNTER = "sms_counter_";
	/**
	 * IAM server matching verification failure counter cache name.
	 */
	final public static String CACHE_FAILFAST_MATCH_COUNTER = "match_counter_";
	/**
	 * Login failure overrun, lock cache name.
	 */
	final public static String CACHE_MATCH_LOCK = "matcher_lock_";
	/**
	 * Securer based cache name
	 */
	final public static String CACHE_SECURER = "securer_";
	/**
	 * The public key index by logged-in users
	 */
	final public static String CACHE_PUBKEY_IDX = CACHE_SECURER + "pubkey_idx_";

	/**
	 * The key of cache encryption key pairs
	 */
	final public static String KEY_KEYPAIRS = "keypairs";
	/**
	 * Token used to save current session authenticating
	 */
	final public static String KEY_AUTHC_TOKEN = "authcTokenAttributeName";
	/**
	 * The locale currently stored in the session.
	 */
	final public static String KEY_USE_LOCALE = "usageLocale";
	/**
	 * Limiter login failure prefix based on username.
	 */
	final public static String KEY_FAIL_LIMITER_USER_PREFIX = "u_";
	/**
	 * Limiter login failure prefix based on remote IP.
	 */
	final public static String KEY_FAIL_LIMITER_RIP_PREFIX = "rip_";

	/**
	 * Error information for saving iam-related operations to sessions.
	 */
	final public static String KEY_ERR_SESSION_SAVED = "errorTipsInfo";

	/**
	 * Delegate message source bean name.
	 */
	final public static String BEAN_DELEGATE_MSG_SOURCE = "iamSessionDelegateMessageBundle";

	//
	// Client configuration.
	//

	/** Fast-CAS client base URI. */
	final public static String URI_C_BASE = "/internal";
	/** Fast-CAS client logout URI. */
	final public static String URI_C_LOGOUT = "logout";

	//
	// Definitions method's
	//

	/**
	 * Safety limiting factor(e.g. Client remote IP and login user-name)
	 * 
	 * @param remoteHost
	 * @param principal
	 * @return
	 */
	public static List<String> lockFactors(String remoteHost, String principal) {
		return new ArrayList<String>(2) {
			private static final long serialVersionUID = -5976569540781454836L;
			{
				if (!StringUtils.isEmpty(principal)) {
					add(KEY_FAIL_LIMITER_USER_PREFIX + principal);
				}
				if (!StringUtils.isEmpty(remoteHost)) {
					// add(KEY_FAIL_LIMITER_RIP_PREFIX +
					// Hex.encodeHexString(remoteHost.getBytes(Charsets.UTF_8)));
					add(KEY_FAIL_LIMITER_RIP_PREFIX
							+ Hex.encodeHexString(UUID.randomUUID().toString().replaceAll("-", "").getBytes(Charsets.UTF_8)));
				}
			}
		};
	}

}