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
package com.wl4g.devops.iam.handler;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;

import javax.annotation.Resource;

import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.iam.common.cache.EnhancedCacheManager;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.config.IamProperties;
import com.wl4g.devops.iam.configure.ServerSecurityConfigurer;
import com.wl4g.devops.iam.configure.ServerSecurityCoprocessor;

/**
 * Abstract IAM authentication handler.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public abstract class AbstractAuthenticationHandler implements AuthenticationHandler {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Rest template
	 */
	final protected RestTemplate restTemplate;

	/**
	 * IAM security context handler
	 */
	final protected ServerSecurityConfigurer context;

	/**
	 * IAM server configuration properties
	 */
	@Autowired
	protected IamProperties config;

	/**
	 * Key id generator
	 */
	@Autowired
	protected SessionIdGenerator idGenerator;

	/**
	 * Enhanced cache manager.
	 */
	@Autowired
	protected EnhancedCacheManager cacheManager;

	/**
	 * IAM server security processor
	 */
	@Autowired
	protected ServerSecurityCoprocessor coprocessor;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MSG_SOURCE)
	protected SessionDelegateMessageBundle bundle;

	public AbstractAuthenticationHandler(ServerSecurityConfigurer context, RestTemplate restTemplate) {
		Assert.notNull(context, "'context' must not be null");
		Assert.notNull(restTemplate, "'restTemplate' must not be null");
		this.restTemplate = restTemplate;
		this.context = context;
	}

	protected String getRoles(String principal, String application) {
		return this.context.findRoles(principal, application);
	}

	protected String getPermits(String principal, String application) {
		return this.context.findPermissions(principal, application);
	}

}