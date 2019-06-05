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
package com.wl4g.devops.iam.filter;

import java.lang.reflect.Constructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import com.wl4g.devops.common.bean.iam.SocialAuthorizeInfo;
import com.wl4g.devops.iam.authc.Oauth2SnsAuthenticationToken;
import com.wl4g.devops.iam.common.cache.EnhancedKey;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_SNSAUTH;
import static com.wl4g.devops.iam.sns.web.AbstractSnsController.KEY_SNS_CALLBACK_PARAMS;
import static com.wl4g.devops.iam.sns.web.AbstractSnsController.PARAM_SNS_CALLBACK_ID;

/**
 * SNS oauth2 authentication abstract filter
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月8日
 * @since
 * @param <T>
 */
public abstract class Oauth2SnsAuthenticationFilter<T extends Oauth2SnsAuthenticationToken>
		extends AbstractIamAuthenticationFilter<T> implements InitializingBean {

	/**
	 * Oauth2 authentication token constructor.
	 */
	private Constructor<T> authenticationTokenConstructor;

	public Oauth2SnsAuthenticationFilter(IamContextManager manager) {
		super(manager);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			this.initial();
			Assert.state(authenticationTokenConstructor != null, "'authenticationTokenConstructor' is null");
		} catch (NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}

		// Add supported SNS provider
		if (this.enabled()) {
			ProviderSupports.addSupport(getName());
		}
	}

	@Override
	protected T postCreateToken(String remoteHost, String fromAppName, String redirectUrl, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String callbackId = WebUtils.getCleanParam(request, PARAM_SNS_CALLBACK_ID);
		Assert.hasText(callbackId, String.format("'%s' must not be empty", PARAM_SNS_CALLBACK_ID));

		// Cache key name
		String key = KEY_SNS_CALLBACK_PARAMS + callbackId;
		try {
			// SNS authorized callback info,
			// See:xx.sns.handler.AbstractSnsHandler#afterCallbackSet()
			SocialAuthorizeInfo info = (SocialAuthorizeInfo) cacheManager.getEnhancedCache(CACHE_SNSAUTH)
					.get(new EnhancedKey(key, SocialAuthorizeInfo.class));

			// Create authentication token instance
			return this.authenticationTokenConstructor.newInstance(fromAppName, redirectUrl, info, remoteHost);
		} finally { // Clean-up cache
			this.cacheManager.getEnhancedCache(CACHE_SNSAUTH).remove(new EnhancedKey(key));
		}
	}

	/**
	 * Initialize the constructor for obtaining authentication tokens
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	@SuppressWarnings("unchecked")
	private void initial() throws NoSuchMethodException, SecurityException {
		ResolvableType resolveType = ResolvableType.forClass(this.getClass());
		Class<T> authenticationTokenClass = (Class<T>) resolveType.getSuperType().getGeneric(0).resolve();
		this.authenticationTokenConstructor = authenticationTokenClass
				.getConstructor(new Class[] { String.class, String.class, SocialAuthorizeInfo.class, String.class });
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