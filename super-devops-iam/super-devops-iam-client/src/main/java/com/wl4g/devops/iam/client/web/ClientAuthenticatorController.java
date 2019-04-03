package com.wl4g.devops.iam.client.web;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_C_LOGOUT;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.SessionException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wl4g.devops.common.bean.iam.model.LogoutModel;
import com.wl4g.devops.common.utils.Exceptions;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.common.annotation.IamController;
import com.wl4g.devops.iam.common.utils.Sessions;

/**
 * IAM client authenticator controller
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月27日
 * @since
 */
@IamController
public class ClientAuthenticatorController extends BaseController {

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
			log.info("Logout processing... sessionId[{}]", Sessions.getSessionId());
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
