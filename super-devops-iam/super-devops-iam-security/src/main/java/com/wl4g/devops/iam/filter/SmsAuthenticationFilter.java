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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.authc.AbstractIamAuthenticationToken.RedirectInfo;
import com.wl4g.devops.iam.crypto.SecureCryptService.SecureAlgKind;
import com.google.common.annotations.Beta;
import com.wl4g.devops.iam.authc.SmsAuthenticationToken;

/**
 * SMS authentication filter.
 *
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-05-18
 * @since
 */
@IamFilter
@Beta
public class SmsAuthenticationFilter extends AbstractIamAuthenticationFilter<SmsAuthenticationToken> {
	final public static String NAME = "sms";

	@Override
	protected SmsAuthenticationToken doCreateToken(String remoteHost, RedirectInfo redirectInfo, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		final String action = getCleanParam(request, config.getParam().getSmsActionName());
		final String principal = getCleanParam(request, config.getParam().getPrincipalName());
		final String smsCode = getCleanParam(request, config.getParam().getCredentialName());
		final String algKind = getCleanParam(request, config.getParam().getSecretAlgKindName());
		final String clientSecret = getCleanParam(request, config.getParam().getClientSecretKeyName());
		return new SmsAuthenticationToken(SecureAlgKind.of(algKind), clientSecret, remoteHost, action, principal, smsCode);
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