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

import static org.apache.shiro.web.util.WebUtils.getCleanParam;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken.RedirectInfo;
import com.wl4g.devops.iam.authc.GenericAuthenticationToken;
import static com.wl4g.devops.iam.verification.SecurityVerifier.VerifyType.*;
import static com.wl4g.devops.tool.common.collection.Collections2.isEmptyArray;
import static com.wl4g.devops.tool.common.collection.Collections2.safeMap;
import static com.wl4g.devops.tool.common.web.WebUtils2.rejectRequestMethod;
import static java.util.stream.Collectors.toMap;

@IamFilter
public class GenericAuthenticationFilter extends AbstractIamAuthenticationFilter<GenericAuthenticationToken> {
	final public static String NAME = "generic";

	@Override
	protected GenericAuthenticationToken postCreateToken(String remoteHost, RedirectInfo redirectInfo, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		rejectRequestMethod(true, request, response, "POST");

		// Bsse required parameters.
		String principal = getCleanParam(request, config.getParam().getPrincipalName());
		String cipherPassword = getCleanParam(request, config.getParam().getCredentialName());
		String clientRef = getCleanParam(request, config.getParam().getClientRefName());
		String verifiedToken = getCleanParam(request, config.getParam().getVerifiedTokenName());

		// Additional optional parameters.
		GenericAuthenticationToken token = new GenericAuthenticationToken(remoteHost, redirectInfo, principal, cipherPassword,
				clientRef, verifiedToken, of(request));
		Map<String, String> userProperties = safeMap(request.getParameterMap()).entrySet().stream()
				.collect(toMap(e -> e.getKey(), e -> isEmptyArray(e.getValue()) ? null : e.getValue()[0]));
		token.setUserProperties(userProperties);

		return token;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getUriMapping() {
		return URI_BASE_MAPPING + NAME;
	}

}