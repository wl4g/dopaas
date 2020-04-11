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
package com.wl4g.devops.iam.client.web;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_C_LOGOUT;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.getSessionId;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.SessionException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.common.annotation.IamController;
import com.wl4g.devops.iam.common.authc.model.LogoutModel;
import com.wl4g.devops.tool.common.lang.Exceptions;

/**
 * IAM client authenticator controller
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月27日
 * @since
 */
@IamController
public class ClientAuthenticatorEndpoint extends BaseController {

	/**
	 * IAM client logout
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping(URI_C_LOGOUT)
	@ResponseBody
	public RespBase<LogoutModel> logout(HttpServletRequest request) {
		if (log.isInfoEnabled()) {
			log.info("Logout processing... sessionId[{}]", getSessionId());
		}

		RespBase<LogoutModel> resp = new RespBase<>();
		/*
		 * Local client session logout
		 */
		try {
			// try/catch added for SHIRO-298:
			SecurityUtils.getSubject().logout();
		} catch (SessionException e) {
			log.warn("Logout exception. This can generally safely be ignored.", e);
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(Exceptions.getRootCauseMessage(e));
		}

		if (log.isInfoEnabled()) {
			log.info("Local logout finished. [{}]", resp);
		}
		return resp;
	}

}