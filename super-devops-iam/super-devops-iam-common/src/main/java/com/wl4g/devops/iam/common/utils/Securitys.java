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
package com.wl4g.devops.iam.common.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;

import static com.google.common.base.Charsets.*;
import static org.apache.commons.codec.binary.Hex.*;
import static org.apache.commons.lang3.StringUtils.*;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;

/**
 * IAM security utility tools.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月21日
 * @since
 */
public abstract class Securitys {

	/**
	 * Default authentication status.
	 */
	final public static String SESSION_STATUS_AUTHC = "Authenticated";

	/**
	 * Default Unauthenticated status.
	 */
	final public static String SESSION_STATUS_UNAUTHC = "Unauthenticated";

	/**
	 * Safety limiting factor(e.g. Client remote IP and loginId)
	 * 
	 * @param remoteHost
	 * @param uid
	 * @return
	 */
	public static List<String> createLimitFactors(String remoteHost, String uid) {
		return new ArrayList<String>(2) {
			private static final long serialVersionUID = -5976569540781454836L;
			{
				String uidFactor = createUIDLimitFactor(uid);
				if (isNotBlank(uidFactor)) {
					add(uidFactor);
				}

				String hostFactor = createAddrFactor(remoteHost);
				if (isNotBlank(hostFactor)) {
					add(hostFactor);
				}
			}
		};
	}

	/**
	 * Create limit remote host factor.
	 * 
	 * @param remoteHost
	 * @return
	 */
	public static String createAddrFactor(String remoteHost) {
		return isNotBlank(remoteHost) ? (KEY_FAIL_LIMIT_RIP_PREFIX + encodeHexString(remoteHost.getBytes(UTF_8))) : EMPTY;
	}

	/**
	 * Create limit login UID factor.
	 * 
	 * @param uid
	 * @return
	 */
	public static String createUIDLimitFactor(String uid) {
		return isNotBlank(uid) ? (KEY_FAIL_LIMIT_UID_PREFIX + uid) : EMPTY;
	}

	/**
	 * Current session authentication status.
	 * 
	 * @return
	 */
	public static String sessionStatus() {
		return SecurityUtils.getSubject().isAuthenticated() ? SESSION_STATUS_AUTHC : SESSION_STATUS_UNAUTHC;
	}

	/**
	 * Get the URI address of the authenticator interface on the client or
	 * server side.</br>
	 * e.g.
	 * 
	 * <pre>
	 *  http://iam.xx.com/iam-server/xx/list?id=1  =>  http://iam.xx.com/iam-server/authenticator?id=1
	 *  http://iam.xx.com/xx/list?id=1             =>  http://iam.xx.com/xx/authenticator?id=1
	 *  http://iam.xx.com/xx/list/?id=1            =>  http://iam.xx.com/xx/authenticator?id=1
	 *  http://iam.xx.com:8080/xx/list/?id=1       =>  http://iam.xx.com:8080/xx/authenticator?id=1
	 *  /view/index.html					       =>  /view/index.html
	 * </pre>
	 * 
	 * Implementing the IAM-CAS protocol: When successful login, you must
	 * redirect to the back-end server URI of IAM-CAS-Client. (Note: URI of
	 * front-end pages can not be used directly).
	 * 
	 * @see {@link com.wl4g.devops.iam.client.filter.AuthenticatorAuthenticationFilter}
	 * @see {@link com.wl4g.devops.iam.filter.AuthenticatorAuthenticationFilter#determineSuccessUrl()}
	 * @param url
	 * @return
	 */
	public static String correctAuthenticaitorURI(String url) {
		if (isBlank(url)) {
			return EMPTY;
		}
		try {
			URI _uri = new URI(url);
			// e.g. /view/index.html => /view/index.html
			if (isAnyBlank(_uri.getScheme(), _uri.getHost())) {
				return url;
			}

			if (!endsWith(_uri.getPath(), URI_AUTHENTICATOR)) {
				String portPart = (_uri.getPort() == 80 || _uri.getPort() == 443 || _uri.getPort() < 0) ? EMPTY
						: (":" + _uri.getPort());
				String queryPart = isBlank(_uri.getQuery()) ? EMPTY : ("?" + _uri.getQuery());
				String contextPath = _uri.getPath();
				String[] pathPart = split(_uri.getPath(), "/");
				if (pathPart.length > 1) {
					contextPath = "/" + pathPart[0];
				}
				return new StringBuffer(_uri.getScheme()).append("://").append(_uri.getHost()).append(portPart)
						.append(contextPath).append(URI_AUTHENTICATOR).append(queryPart).toString();
			}
			return url;
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Can't to obtain a redirect authenticaitor URL.", e);
		}
	}

}