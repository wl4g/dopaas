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
package com.wl4g.devops.iam.client.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;

import com.wl4g.devops.iam.client.config.IamClientProperties;
import com.wl4g.devops.iam.client.validation.IamValidator;
import com.wl4g.devops.iam.common.authc.IamAuthenticationInfo;
import com.wl4g.devops.iam.common.authc.model.TicketValidatedAssertModel;
import com.wl4g.devops.iam.common.authc.model.TicketValidateModel;
import com.wl4g.devops.iam.common.realm.AbstractPermittingAuthorizingRealm;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfoWrapper;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_AUTHC_ACCOUNT_INFO;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.*;
import static com.wl4g.devops.iam.common.session.IamSession.RelationAttrKey;

/**
 * Abstract authorizing realm.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2018-11-23
 * @since
 */
public abstract class AbstractClientAuthorizingRealm extends AbstractPermittingAuthorizingRealm {

	/**
	 * Ticket validation properties.
	 */
	final protected IamClientProperties config;

	/**
	 * From the fast-CAS client is used to validate a service ticket on server
	 */
	final protected IamValidator<TicketValidateModel, TicketValidatedAssertModel<IamPrincipalInfo>> ticketValidator;

	public AbstractClientAuthorizingRealm(IamClientProperties config,
			IamValidator<TicketValidateModel, TicketValidatedAssertModel<IamPrincipalInfo>> ticketValidator) {
		this.config = config;
		this.ticketValidator = ticketValidator;
	}

	/**
	 * @see {@link com.wl4g.devops.iam.realm.AbstractIamAuthorizingRealm#doGetAuthenticationInfo(AuthenticationToken)}
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		IamAuthenticationInfo info = doAuthenticationInfo(token);
		bind(new RelationAttrKey(KEY_AUTHC_ACCOUNT_INFO), new IamPrincipalInfoWrapper(info.getPrincipalInfo()));
		return info;
	}

	/**
	 * Get current authenticating principal {@link IamAuthenticationInfo}.</br>
	 * 
	 * @param token
	 * @return
	 * @throws AuthenticationException
	 */
	protected abstract IamAuthenticationInfo doAuthenticationInfo(AuthenticationToken token) throws AuthenticationException;

}