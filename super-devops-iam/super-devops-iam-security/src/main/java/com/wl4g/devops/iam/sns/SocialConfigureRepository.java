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
package com.wl4g.devops.iam.sns;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.util.Assert;

import com.wl4g.devops.common.exception.iam.NoSuchSocialProviderException;
import com.wl4g.devops.iam.sns.support.Oauth2AccessToken;
import com.wl4g.devops.iam.sns.support.Oauth2OpenId;
import com.wl4g.devops.iam.sns.support.Oauth2UserProfile;

/**
 * Social configure repository
 * 
 * @author wangl.sir
 * @version v1.0 2019年2月25日
 * @since
 */
public class SocialConfigureRepository implements SocialRepository {

	/**
	 * Binding connection repository
	 */
	final private Map<String, BindConnection<Oauth2AccessToken, Oauth2OpenId, Oauth2UserProfile>> repository = new ConcurrentHashMap<>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SocialConfigureRepository(List<BindConnection> binds) {
		Assert.notEmpty(binds, "'binds' must not be empty");
		for (BindConnection<Oauth2AccessToken, Oauth2OpenId, Oauth2UserProfile> bind : binds) {
			if (this.repository.putIfAbsent(bind.providerId(), bind) != null) {
				throw new IllegalStateException(String.format("Already provider register", bind.providerId()));
			}
		}
	}

	@Override
	public BindConnection<Oauth2AccessToken, Oauth2OpenId, Oauth2UserProfile> getBindConnection(String provider) {
		if (!this.repository.containsKey(provider)) {
			throw new NoSuchSocialProviderException(String.format("No such social provider [%s]", provider));
		}
		return this.repository.get(provider);
	}

}