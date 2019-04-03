package com.wl4g.devops.iam.client.context;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;

import com.wl4g.devops.iam.common.context.SecurityListener;

public class AnynothingSecurityListener implements SecurityListener {

	@Override
	public void onPostLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) {
	}

	@Override
	public void onPostLoginFailure(AuthenticationToken token, AuthenticationException ae, ServletRequest request,
			ServletResponse response) {
	}

	@Override
	public void onPreLogout(boolean forced, ServletRequest request, ServletResponse response) {

	}

}
