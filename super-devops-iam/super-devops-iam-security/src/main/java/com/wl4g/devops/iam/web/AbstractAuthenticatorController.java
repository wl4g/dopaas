package com.wl4g.devops.iam.web;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MESSAGE_SOURCE;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.iam.common.i18n.DelegateBoundleMessageSource;
import com.wl4g.devops.iam.config.IamProperties;
import com.wl4g.devops.iam.context.ServerSecurityInterceptor;
import com.wl4g.devops.iam.handler.AuthenticationHandler;

/**
 * IAM abstract basic authenticator internal controller
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月22日
 * @since
 */
public abstract class AbstractAuthenticatorController extends BaseController {

	/**
	 * IAM server properties configuration
	 */
	@Autowired
	protected IamProperties config;

	/**
	 * Authentication handler
	 */
	@Autowired
	protected AuthenticationHandler authHandler;

	/**
	 * IAM server security interceptor.
	 */
	@Autowired
	protected ServerSecurityInterceptor interceptor;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MESSAGE_SOURCE)
	protected DelegateBoundleMessageSource delegate;

}
