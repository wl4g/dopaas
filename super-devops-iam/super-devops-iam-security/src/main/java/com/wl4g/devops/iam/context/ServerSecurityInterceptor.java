package com.wl4g.devops.iam.context;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.wl4g.devops.iam.common.context.SecurityInterceptor;

public interface ServerSecurityInterceptor extends SecurityInterceptor {

	boolean preApplyCapcha(ServletRequest request, ServletResponse response);

}
