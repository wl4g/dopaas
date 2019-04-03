package com.wl4g.devops.iam.client.context;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;

public class AnynothingSecurityContext implements ClientSecurityContext {

	@Override
	public String determineLoginSuccessUrl(String successUrl, AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) {
		return successUrl;
	}

	@Override
	public String determineLoginFailureUrl(String loginUrl, AuthenticationToken token, AuthenticationException ae,
			ServletRequest request, ServletResponse response) {
		return loginUrl;
	}

}
