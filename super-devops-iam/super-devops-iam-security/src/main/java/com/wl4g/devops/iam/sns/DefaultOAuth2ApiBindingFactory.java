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

import static com.wl4g.devops.tool.common.lang.Assert2.notEmptyOf;
import static java.util.Objects.nonNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wl4g.devops.common.exception.iam.NoSuchSocialProviderException;
import com.wl4g.devops.iam.sns.support.Oauth2AccessToken;
import com.wl4g.devops.iam.sns.support.Oauth2OpenId;
import com.wl4g.devops.iam.sns.support.Oauth2UserProfile;

/**
 * Default IAM Social connection factory
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月4日
 * @since
 */
public class DefaultOAuth2ApiBindingFactory implements OAuth2ApiBindingFactory {

	/**
	 * Binding connection repository
	 */
	final private Map<String, OAuth2ApiBinding<Oauth2AccessToken, Oauth2OpenId, Oauth2UserProfile>> repository = new ConcurrentHashMap<>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DefaultOAuth2ApiBindingFactory(List<OAuth2ApiBinding> apis) {
		notEmptyOf(apis, "OAuth2ApiBindings");
		for (OAuth2ApiBinding<Oauth2AccessToken, Oauth2OpenId, Oauth2UserProfile> api : apis) {
			if (nonNull(repository.putIfAbsent(api.providerId(), api))) {
				throw new IllegalStateException(String.format("Already provider register", api.providerId()));
			}
		}
	}

	@Override
	public OAuth2ApiBinding<Oauth2AccessToken, Oauth2OpenId, Oauth2UserProfile> getApiBinding(String provider) {
		if (!this.repository.containsKey(provider)) {
			throw new NoSuchSocialProviderException(String.format("No such social provider [%s]", provider));
		}
		return this.repository.get(provider);
	}

}