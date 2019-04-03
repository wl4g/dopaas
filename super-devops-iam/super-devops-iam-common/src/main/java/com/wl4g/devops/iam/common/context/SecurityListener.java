package com.wl4g.devops.iam.common.context;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;

public interface SecurityListener {

	/**
	 * Post-handling of login success
	 * 
	 * @param token
	 * @param subject
	 * @param request
	 * @param response
	 */
	void onPostLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response);

	/**
	 * Post-handling of login failure
	 * 
	 * @param token
	 * @param ae
	 * @param request
	 * @param response
	 */
	void onPostLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request,
			ServletResponse response);

	/**
	 * Listener before logout
	 * 
	 * @param forced
	 * @param request
	 * @param response
	 */
	void onPreLogout(boolean forced, ServletRequest request, ServletResponse response);

}
