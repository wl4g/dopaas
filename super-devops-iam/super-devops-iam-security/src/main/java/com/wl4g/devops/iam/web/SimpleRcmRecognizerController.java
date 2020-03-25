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
package com.wl4g.devops.iam.web;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.iam.common.utils.AuthenticatingUtils.sessionStatus;
import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static java.util.stream.Collectors.toMap;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static com.wl4g.devops.iam.web.model.SimpleRcmTokenResult.*;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.handler.SimpleRcmRecognizerHandler;
import com.wl4g.devops.iam.web.model.SimpleRcmTokenResult;

/**
 * Simple risk control controller.
 * 
 * Note: it is a simple version of the implementation of risk control
 * inspection. It is recommended to use a more professional external
 * RiskControlService in the production environment.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月25日
 * @since
 */
@com.wl4g.devops.iam.annotation.SimpleRCMController
public class SimpleRcmRecognizerController extends AbstractAuthenticatorController {

	@Autowired
	protected SimpleRcmRecognizerHandler handler;

	/**
	 * Initiate handshake to establish connection, such as client submits UA and
	 * device fingerprint information, and server returns session ID.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = URI_S_RCM_UMIDTOKEN_APPLY, method = { POST })
	@ResponseBody
	public RespBase<?> applyUmidToken(HttpServletRequest request, HttpServletResponse response) {
		RespBase<Object> resp = RespBase.create(sessionStatus());

		// Gets required risk control parameters.
		Map<String, String> requiredParams = config.getParam().getRequiredRiskControlParams().stream().map(name -> {
			String value = request.getParameter(name);
			hasText(value, "Parameter '%s' is required!", name);
			return value;
		}).collect(toMap(n -> n, v -> v));

		// Gets optional risk control parameters.
		Map<String, String> optionalParams = config.getParam().getOptionalRiskControlParams().stream()
				.map(name -> request.getParameter(name)).collect(toMap(n -> n, v -> v));

		// [Simple risk control processing]
		String umidToken = handler.applyUmidToken(requiredParams, optionalParams);
		resp.forMap().put(KEY_RCM_TOKEN_MODEL, new SimpleRcmTokenResult(umidToken));
		return resp;
	}

}
