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
package com.wl4g.devops.iam.sns.handler;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.sns.CallbackResult;

/**
 * IAM Social delegate handler factory
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月4日
 * @since
 */
public class DelegateSnsHandler implements SnsHandler {

	/**
	 * SNS handler repository
	 */
	final private Map<Which, SnsHandler> repository = new ConcurrentHashMap<>();

	/**
	 * IAM server properties configuration
	 */
	final protected IamProperties config;

	public DelegateSnsHandler(IamProperties config, List<SnsHandler> handlers) {
		Assert.notNull(config, "'config' must not be null");
		Assert.notEmpty(handlers, "'handlers' must not be empty");
		this.config = config;
		for (SnsHandler handler : handlers) {
			if (repository.putIfAbsent(handler.which(), handler) != null) {
				throw new IllegalStateException(String.format("Already sns handler register", handler.which()));
			}
		}
	}

	@Override
	public String doOAuth2GetAuthorizingUrl(Which which, String provider, String state, Map<String, String> connectParams) {
		state = isBlank(state) ? UUID.randomUUID().toString().replaceAll("-", "") : state;
		return getSnsHandler(which).doOAuth2GetAuthorizingUrl(which, provider, state, connectParams);
	}

	@Override
	public CallbackResult doOAuth2Callback(Which which, String provider, String state, String code, HttpServletRequest request) {
		return getSnsHandler(which).doOAuth2Callback(which, provider, state, code, request);
	}

	@Override
	public Which which() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get target SNS handler
	 *
	 * @param which
	 * @return
	 */
	private SnsHandler getSnsHandler(Which which) {
		Assert.notNull(which, String.format("Illegal parameter %s[%s]", config.getParam().getWhich(), which));
		if (!repository.containsKey(which)) {
			throw new NoSuchBeanDefinitionException(String.format("No such sns handler of which[%s]", which));
		}
		return repository.get(which);
	}

}