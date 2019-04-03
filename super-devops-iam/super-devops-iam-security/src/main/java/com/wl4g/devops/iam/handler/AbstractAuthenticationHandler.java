package com.wl4g.devops.iam.handler;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MESSAGE_SOURCE;

import javax.annotation.Resource;

import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.common.context.SecurityListener;
import com.wl4g.devops.iam.common.i18n.DelegateBoundleMessageSource;
import com.wl4g.devops.iam.config.IamProperties;
import com.wl4g.devops.iam.context.ServerSecurityContext;

/**
 * Abstract IAM authentication handler.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public abstract class AbstractAuthenticationHandler implements AuthenticationHandler {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Rest template
	 */
	final protected RestTemplate restTemplate;

	/**
	 * IAM security context handler
	 */
	final protected ServerSecurityContext context;

	/**
	 * IAM server configuration properties
	 */
	@Autowired
	protected IamProperties config;

	/**
	 * Key id generator
	 */
	@Autowired
	protected SessionIdGenerator idGenerator;

	/**
	 * Redis's cache manger
	 */
	@Autowired
	protected JedisCacheManager cacheManager;

	/**
	 * IAM security listener
	 */
	@Autowired
	protected SecurityListener listener;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MESSAGE_SOURCE)
	protected DelegateBoundleMessageSource delegate;

	public AbstractAuthenticationHandler(ServerSecurityContext context, RestTemplate restTemplate) {
		Assert.notNull(context, "'context' must not be null");
		Assert.notNull(restTemplate, "'restTemplate' must not be null");
		this.restTemplate = restTemplate;
		this.context = context;
	}

	protected String getRoles(String principal, String application) {
		return this.context.findRoles(principal, application);
	}

	protected String getPermits(String principal, String application) {
		return this.context.findPermissions(principal, application);
	}

}
