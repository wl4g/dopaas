/*
 * Copyright 2017 ~ 2050 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.iam.web;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.configure.ServerSecurityConfigurer;
import com.wl4g.devops.iam.configure.ServerSecurityCoprocessor;
import com.wl4g.devops.iam.handler.AuthenticationHandler;

/**
 * IAM abstract basic authenticator internal controller
 *
 * @author wangl.sir
 * @version v1.0 2019年1月22日
 * @since
 */
public abstract class AbstractAuthenticatorEndpoint extends BaseController implements InitializingBean {

	/**
	 * IAM server properties configuration
	 */
	@Autowired
	protected IamProperties config;

	/**
	 * Authentication handler
	 */
	@Autowired
	protected AuthenticationHandler authHandler;

	/**
	 * IAM server security coprocessor.
	 */
	@Autowired
	protected ServerSecurityCoprocessor coprocessor;

	/**
	 * IAM server security configurer.
	 */
	@Autowired
	protected ServerSecurityConfigurer configurer;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MSG_SOURCE)
	protected SessionDelegateMessageBundle bundle;

	@Override
	public void afterPropertiesSet() throws Exception {

	}

}