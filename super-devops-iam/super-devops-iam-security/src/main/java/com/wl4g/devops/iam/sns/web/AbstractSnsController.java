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
package com.wl4g.devops.iam.sns.web;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;

import javax.annotation.Resource;

import org.springframework.util.Assert;

import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.config.properties.SnsProperties;
import com.wl4g.devops.iam.sns.handler.DelegateSnsHandler;

/**
 * Abstract based social networking services controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月7日
 * @since
 */
public abstract class AbstractSnsController extends BaseController {

	/**
	 * Oauth2 connect parameters 'code'
	 */
	final public static String PARAM_SNS_CODE = "code";

	/**
	 * SNS connect parameters 'provider'
	 */
	final public static String PARAM_SNS_PRIVIDER = "provider";

	/**
	 * ID key name of social network callback information cache
	 */
	final public static String PARAM_SNS_CALLBACK_ID = "callbackId";

	/**
	 * Key name of parameter caching after social network callback
	 * (openId/unionId, etc.)
	 */
	final public static String KEY_SNS_CALLBACK_PARAMS = "callback_params_";

	/**
	 * Callback agent handles intermediate pages html (writing data redirected
	 * after callback to parent dom).
	 */
	final public static String TEMPLATE_CALLBACK_AGENT = "<!DOCTYPE html><html><head><script type=\"text/javascript\">function setParent(parent,data){try{console.debug(\"Binding to parent body...\");var parentBody=parent.document.getElementsByTagName(\"body\")[0];for(var key in data){parentBody.setAttribute(key,data[key])}}catch(err){console.warn(\"The use of parent objects is not supported\")}try{console.debug(\"Post message parent...\");parent.postMessage(JSON.stringify(data),\"*\")}catch(err){console.warn(\"Window post message to parent error\")}}var data=JSON.parse(\"%s\");var doc=document;if(self!=top){console.debug(\"If it's an iframe page\");setParent(window.parent,data)}else if(window.opener){console.debug(\"If it's a subform page\");setParent(window.opener,data);window.close()}else{console.debug(\"Single window home page, no handling\")}</script></head><body>Please wait,handling...</body></html>";

	/**
	 * IAM server properties configuration
	 */
	final protected IamProperties config;

	/**
	 * SNS properties configuration
	 */
	final protected SnsProperties snsConfig;

	/**
	 * Delegate SNS handler
	 */
	final protected DelegateSnsHandler delegate;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MSG_SOURCE)
	protected SessionDelegateMessageBundle bundle;

	public AbstractSnsController(IamProperties config, SnsProperties snsConfig, DelegateSnsHandler delegate) {
		Assert.notNull(delegate, "'delegateSnsHandlerFactory' must not be null");
		Assert.notNull(config, "'config' must not be null");
		Assert.notNull(snsConfig, "'snsConfig' must not be null");
		this.delegate = delegate;
		this.config = config;
		this.snsConfig = snsConfig;
	}

}