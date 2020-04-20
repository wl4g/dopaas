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

import java.lang.reflect.Constructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.apache.shiro.web.util.WebUtils.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ResolvableType;

import com.wl4g.devops.common.bean.iam.SocialAuthorizeInfo;
import com.wl4g.devops.iam.authc.Oauth2SnsAuthenticationToken;
import com.wl4g.devops.iam.common.authc.AbstractIamAuthenticationToken.RedirectInfo;
import com.wl4g.devops.iam.common.cache.CacheKey;
import com.wl4g.devops.iam.sns.handler.AbstractSnsHandler;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_SNSAUTH;
import static com.wl4g.devops.iam.sns.web.AbstractSnsController.PARAM_SNS_CALLBACK_ID;
import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.tool.common.lang.Assert2.state;

/**
 * SNS oauth2 authentication abstract filter
 *
 * @param <T>
 * @author wangl.sir
 * @version v1.0 2019年1月8日
 * @since
 */
public abstract class Oauth2SnsAuthenticationFilter<T extends Oauth2SnsAuthenticationToken>
		extends AbstractIamAuthenticationFilter<T> implements InitializingBean {

	/**
	 * Oauth2 authentication token constructor.
	 */
	private Constructor<T> authenticationTokenConstructor;

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			initialize();
			state(authenticationTokenConstructor != null, "'authenticationTokenConstructor' is null");
		} catch (NoSuchMethodException | SecurityException e) {
			throw new IllegalStateException(e);
		}
		// Add supported SNS provider
		if (enabled()) {
			ProviderSupport.addSupport(getName());
		}
	}

	@Override
	protected T doCreateToken(String remoteHost, RedirectInfo redirectInfo, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String callbackId = getCleanParam(request, PARAM_SNS_CALLBACK_ID);
		hasTextOf(callbackId, PARAM_SNS_CALLBACK_ID);

		String callbackKey = AbstractSnsHandler.getOAuth2CallbackKey(callbackId);
		try {
			// Get SNS OAuth2 callback info,
			SocialAuthorizeInfo authorizedInfo = (SocialAuthorizeInfo) cacheManager.getIamCache(CACHE_SNSAUTH)
					.get(new CacheKey(callbackKey, SocialAuthorizeInfo.class));

			// Create authentication token instance
			return authenticationTokenConstructor.newInstance(remoteHost, redirectInfo, authorizedInfo);
		} finally { // Cleanup temporary OAuth2 info.
			cacheManager.getIamCache(CACHE_SNSAUTH).remove(new CacheKey(callbackKey));
		}
	}

	/**
	 * Initialize the constructor for obtaining authentication tokens
	 *
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	@SuppressWarnings("unchecked")
	private void initialize() throws NoSuchMethodException, SecurityException {
		ResolvableType resolveType = ResolvableType.forClass(this.getClass());
		Class<T> authenticationTokenClass = (Class<T>) resolveType.getSuperType().getGeneric(0).resolve();
		this.authenticationTokenConstructor = authenticationTokenClass
				.getConstructor(new Class[] { String.class, RedirectInfo.class, SocialAuthorizeInfo.class });
	}

	/**
	 * Whether social networking authentication provider enabled
	 *
	 * @return
	 */
	protected boolean enabled() {
		return false;
	}

}