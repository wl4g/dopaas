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
package com.wl4g.devops.iam.authc;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

import org.apache.shiro.authc.RememberMeAuthenticationToken;

import com.wl4g.devops.iam.common.authc.AbstractIamAuthenticationToken;
import com.wl4g.devops.iam.common.authc.ClientRef;
import com.wl4g.devops.iam.verification.SecurityVerifier.VerifyType;

/**
 * General (Username/Password) authentication token
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月19日
 * @since
 */
public class GeneralAuthenticationToken extends AbstractIamAuthenticationToken
        implements RememberMeAuthenticationToken, VerifyAuthenticationToken {
    private static final long serialVersionUID = 8587329689973009598L;

    /**
     * The username principal
     */
    private String principal;

    /**
     * The password credentials
     */
    private String credentials;

    /**
     * Whether or not 'rememberMe' should be enabled for the corresponding login
     * attempt; default is <code>false</code>
     */
    private boolean rememberMe = false;

    /**
     * User client type.
     */
    final private ClientRef clientRef;

    /**
     * Verification code verifiedToken.
     */
    final private String verifiedToken;

    /**
     * Verifier type.
     */
    final private VerifyType verifyType;

    public GeneralAuthenticationToken(final String remoteHost, final RedirectInfo redirectInfo, final String principal,
                                      final String credentials, String clientRef, final String verifiedToken, final VerifyType verifyType) {
        this(remoteHost, redirectInfo, principal, credentials, clientRef, verifiedToken, verifyType, false);
    }

    public GeneralAuthenticationToken(final String remoteHost, final RedirectInfo redirectInfo, final String principal,
                                      final String credentials, String clientRef, final String verifiedToken, final VerifyType verifyType,
                                      final boolean rememberMe) {
        super(remoteHost, redirectInfo);
        hasText(principal, "Username principal must not be empty.");
        hasText(credentials, "Credentials must not be empty.");
        hasText(clientRef, "ClientRef must not be empty.");
        // hasText(verifiedToken, "Verified token must not be empty.");
        notNull(verifyType, "Verify type must not be null.");
        this.principal = principal;
        this.credentials = credentials;
        this.clientRef = ClientRef.of(clientRef);
        this.verifiedToken = verifiedToken;
        this.verifyType = verifyType;
        this.rememberMe = rememberMe;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public boolean isRememberMe() {
        return rememberMe;
    }

    public ClientRef getClientRef() {
        return clientRef;
    }

    @Override
    public String getVerifiedToken() {
        return verifiedToken;
    }

    @Override
    public VerifyType getVerifyType() {
        return verifyType;
    }

}