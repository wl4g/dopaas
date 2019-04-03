package com.wl4g.devops.iam.client.context;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class AnynothingSecurityInterceptor implements ClientSecurityInterceptor {

	@Override
	public boolean preAuthentication(Filter filter, ServletRequest request, ServletResponse response) {
		return true;
	}

}
