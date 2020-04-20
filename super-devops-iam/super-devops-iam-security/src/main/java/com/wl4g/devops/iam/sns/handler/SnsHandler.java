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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
import com.wl4g.devops.iam.sns.CallbackResult;

/**
 * Social networking services handler
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月7日
 * @since
 */
public interface SnsHandler {

	/**
	 * Request connects to a social network (requesting oauth2 authorization)
	 * parameters binding session key name
	 */
	final public static String KEY_SNS_CONNECT_PARAMS = "connect_params_";

	/**
	 * Handling which(action) type
	 *
	 * @return
	 */
	Which which();

	/**
	 * Getting request SNS authorizing URL
	 *
	 * @param which
	 * @param provider
	 * @param state
	 * @param connectParams
	 * @return
	 */
	String doOAuth2GetAuthorizingUrl(Which which, String provider, String state, Map<String, String> connectParams);

	/**
	 * SNS authorizing callback
	 *
	 * @param which
	 * @param provider
	 * @param state
	 * @param code
	 * @param request
	 * @return
	 */
	CallbackResult doOAuth2Callback(Which which, String provider, String state, String code, HttpServletRequest request);

}