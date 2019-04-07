/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.iam.authc.credential;

import javax.annotation.Resource;

import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MESSAGE_SOURCE;
import com.wl4g.devops.iam.authc.credential.secure.IamCredentialsSecurer;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.common.context.SecurityCoprocessor;
import com.wl4g.devops.iam.common.i18n.DelegateBundleMessageSource;
import com.wl4g.devops.iam.config.IamProperties;
import com.wl4g.devops.iam.handler.CaptchaHandler;

/**
 * IAM based matcher
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public abstract class IamBasedMatcher extends SimpleCredentialsMatcher {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Matcher configuration properties
	 */
	@Autowired
	protected IamProperties config;

	/**
	 * Using Distributed Cache to Ensure Concurrency Control under Multi-Node
	 */
	@Autowired
	protected JedisCacheManager cacheManager;

	/**
	 * IAM captcha handler
	 */
	@Autowired
	protected CaptchaHandler captchaHandler;

	/**
	 * IAM credentials securer
	 */
	@Autowired
	protected IamCredentialsSecurer securer;

	/**
	 * IAM security coprocessor
	 */
	@Autowired
	protected SecurityCoprocessor coprocessor;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MESSAGE_SOURCE)
	protected DelegateBundleMessageSource bundle;

}