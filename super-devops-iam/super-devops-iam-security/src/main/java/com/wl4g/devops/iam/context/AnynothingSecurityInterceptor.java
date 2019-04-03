package com.wl4g.devops.iam.context;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * IAM default empty processing intercept handler
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月18日
 * @since
 */
public class AnynothingSecurityInterceptor implements ServerSecurityInterceptor {

	@Override
	public boolean preApplyCapcha(ServletRequest request, ServletResponse response) {
		return true; // Any allowed
	}

	@Override
	public boolean preAuthentication(Filter filter, ServletRequest request, ServletResponse response) {
		return true; // Any allowed
	}

}
