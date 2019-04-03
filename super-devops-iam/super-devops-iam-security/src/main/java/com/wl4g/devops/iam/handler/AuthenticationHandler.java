package com.wl4g.devops.iam.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;

import com.wl4g.devops.common.bean.iam.model.LoggedModel;
import com.wl4g.devops.common.bean.iam.model.LogoutModel;
import com.wl4g.devops.common.bean.iam.model.SecondAuthcAssertion;
import com.wl4g.devops.common.bean.iam.model.SessionValidationAssertion;
import com.wl4g.devops.common.bean.iam.model.TicketAssertion;
import com.wl4g.devops.common.bean.iam.model.TicketValidationModel;

/**
 * IAM authentication handler.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public abstract interface AuthenticationHandler {

	/**
	 * Check the validity of the request parameters before executing the login.
	 * (that is, verify that the <b>'source application'</b> and the secure
	 * callback <b>'redirectUrl'</b> are legitimate)
	 * 
	 * @param fromAppName
	 * @param redirectUrl
	 */
	void checkAuthenticateRequests(String fromAppName, String redirectUrl);

	/**
	 * Check whether the current login account has permission to access the
	 * application. (that is, validating the legitimacy of <b>'principal'</b>
	 * and <b>'source application'</b>)
	 * 
	 * @param principal
	 * @param fromAppName
	 *            From source application
	 */
	void checkApplicationAccessAuthorized(String principal, String fromAppName);

	/**
	 * Validate application request ticket
	 * 
	 * @param param
	 *            ticket validation request
	 * @return validation assert result
	 */
	TicketAssertion validate(TicketValidationModel param);

	/**
	 * Shiro authentication success callback process.
	 * 
	 * @param fromAppName
	 *            from source application name
	 * @param subject
	 *            Shiro subject
	 * @return Redirect callback information
	 */
	LoggedModel loggedin(String fromAppName, Subject subject);

	/**
	 * Logout server session, including all external applications logged-in<br/>
	 * <br/>
	 * The Iam server logs out with two entries: <br/>
	 * 1: access http://iam-client/logout <br/>
	 * 2: direct access http://iam-server/logout <br/>
	 * {@link com.wl4g.devops.iam.web.CentralAuthenticatorController#logout()}
	 * {@link com.wl4g.devops.iam.filter.LogoutAuthenticationFilter#preHandle()}
	 * 
	 * @param forced
	 *            logout forced
	 * @param fromAppName
	 *            from source application name
	 * @param request
	 * @param response
	 * @return
	 */
	LogoutModel logout(boolean forced, String fromAppName, HttpServletRequest request, HttpServletResponse response);

	/**
	 * Validation application secondary authentication
	 * 
	 * @param secondAuthCode
	 *            Secondary authentication code
	 * @param fromAppName
	 *            from source application name
	 * @return
	 */
	SecondAuthcAssertion secondValidate(String secondAuthCode, String fromAppName);

	/**
	 * Sessions expired validation
	 * 
	 * @param param
	 * @return
	 */
	SessionValidationAssertion sessionValidate(SessionValidationAssertion param);

}
