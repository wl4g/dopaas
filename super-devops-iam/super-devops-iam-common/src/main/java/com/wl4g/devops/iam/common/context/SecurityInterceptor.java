package com.wl4g.devops.iam.common.context;

import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * IAM security processing intercept handler
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月18日
 * @since
 */
public interface SecurityInterceptor {

	/**
	 * Pre-handling before authentication, For example, the implementation of
	 * restricting client IP white-list to prevent violent cracking of large
	 * number of submission login requests.
	 * 
	 * @param filter
	 * @param request
	 * @param response
	 * @return
	 */
	boolean preAuthentication(Filter filter, ServletRequest request, ServletResponse response);

}
