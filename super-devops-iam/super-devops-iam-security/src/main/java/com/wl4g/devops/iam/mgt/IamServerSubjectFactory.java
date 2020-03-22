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
package com.wl4g.devops.iam.mgt;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_AUTHC_TOKEN;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_CLIENT_SECRET_TOKEN;
import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static java.lang.String.format;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.common.exception.iam.UnauthenticatedException;
import com.wl4g.devops.iam.authc.ClientSecretIamAuthenticationToken;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken;
import com.wl4g.devops.iam.common.mgt.IamSubjectFactory;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.filter.AbstractIamAuthenticationFilter;

import static org.apache.commons.codec.digest.HmacUtils.*;
import static org.apache.shiro.web.util.WebUtils.*;
import static java.security.MessageDigest.*;
import static java.util.Objects.isNull;

import javax.servlet.http.HttpServletRequest;

/**
 * {@link IamServerSubjectFactory}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月22日 v1.0.0
 * @see
 */
public class IamServerSubjectFactory extends IamSubjectFactory {

	/**
	 * {@link IamProperties}
	 */
	@Autowired
	protected IamProperties config;

	@Override
	public Subject createSubject(SubjectContext context) {
		try {
			assertRequestSignTokenValidity(context);
		} catch (UnauthenticatedException e) {
			context.setAuthenticated(false); // #Forced sets not authenticated
			if (log.isDebugEnabled())
				log.debug(e.getMessage(), e);
			else
				log.warn(e.getMessage());
		}
		return super.createSubject(context);
	}

	/**
	 * Assertion request signature validity.
	 * 
	 * @param context
	 * @throws UnauthenticatedException
	 * @see {@link AbstractIamAuthenticationFilter#makeLoggedResponse}
	 */
	final private void assertRequestSignTokenValidity(SubjectContext context) throws UnauthenticatedException {
		// Additional signature verification will only be performed on those
		// who have logged in successful.
		// e.g: Authentication requests or internal API requests does not
		// require signature verification.
		if (context.isAuthenticated() || isNull(context.getSession())) {
			return;
		}

		WebSubjectContext wsc = (WebSubjectContext) context;
		HttpServletRequest request = toHttp(wsc.resolveServletRequest());
		// HttpServletResponse response = toHttp(wsc.resolveServletResponse());

		String clientSign = getCleanParam(request, config.getParam().getClientSignName());
		String clientNonce = getCleanParam(request, config.getParam().getClientNonceName());
		String clientSecretToken = (String) wsc.getSession().getAttribute(KEY_CLIENT_SECRET_TOKEN);
		IamAuthenticationToken authcToken = (IamAuthenticationToken) wsc.getSession().getAttribute(KEY_AUTHC_TOKEN);
		log.debug("Asserting signature of clientSign:{}, clientNoce:{}, clientSecretToken:{}, authcToken:{}", clientSign,
				clientNonce, clientSecretToken, authcToken);

		// Only the password authentication is verified.
//		if (authcToken instanceof ClientSecretIamAuthenticationToken) {
//			hasText(clientSign, UnauthenticatedException.class, "client sign is required");
//			hasText(clientNonce, UnauthenticatedException.class, "client nonce is required");
//			hasTextOf(clientSecretToken, "clientSecretToken"); // Shouldn't here
//
//			// Calculate signature
//			final byte[] validSign = getHmacSha1(clientSecretToken.getBytes(UTF_8)).doFinal(clientNonce.getBytes(UTF_8));
//			log.info("Asserted signature of clientSign:{}, clientNoce:{}, clientSecretToken:{}, validSign:{}, authcToken:{}",
//					clientSign, clientNonce, clientSecretToken, validSign, authcToken);
//			// Compare signature's
//			if (!isEqual(clientSign.getBytes(UTF_8), validSign)) {
//				throw new UnauthenticatedException(
//						format("Illegal authentication credentials signature. sign: {}, clientSecretToken: {}", clientSign,
//								clientSecretToken));
//			}
//		}

	}

}
