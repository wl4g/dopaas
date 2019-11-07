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
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestMethodNotSupportedException;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.authc.IamAuthenticationToken.RedirectInfo;
import com.wl4g.devops.iam.verification.SecurityVerifier.VerifyType;
import com.wl4g.devops.iam.authc.GeneralAuthenticationToken;

@IamFilter
public class GeneralAuthenticationFilter extends AbstractIamAuthenticationFilter<GeneralAuthenticationToken> {
    final public static String NAME = "general";

    @Override
    protected GeneralAuthenticationToken postCreateToken(String remoteHost, RedirectInfo redirectInfo, HttpServletRequest request,
                                                         HttpServletResponse response) throws Exception {
        if (!POST.name().equalsIgnoreCase(request.getMethod())) {
            response.setStatus(405);
            throw new HttpRequestMethodNotSupportedException(request.getMethod(),
                    String.format("No support '%s' request method", request.getMethod()));
        }

        String principal = getCleanParam(request, config.getParam().getPrincipalName());
        String cipherPassword = getCleanParam(request, config.getParam().getCredentialName());
        String clientRef = getCleanParam(request, config.getParam().getClientRefName());
        String verifiedToken = getCleanParam(request, config.getParam().getVerifiedTokenName());
        return new GeneralAuthenticationToken(remoteHost, redirectInfo, principal, cipherPassword, clientRef, verifiedToken,
                VerifyType.of(request));
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