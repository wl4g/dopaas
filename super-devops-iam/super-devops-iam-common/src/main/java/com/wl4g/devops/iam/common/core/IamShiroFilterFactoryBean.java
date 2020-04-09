/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.iam.common.core;

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.util.Nameable;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.util.Assert;

import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * IAM customize shiro-spring filter factory bean
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月25日
 * @since
 */
public class IamShiroFilterFactoryBean extends ShiroFilterFactoryBean {

	final protected SmartLogger log = getLogger(getClass());

	private FilterChainManager chainManager;

	public IamShiroFilterFactoryBean(FilterChainManager chainManager) {
		Assert.notNull(chainManager, "chainManager is null, please check configure");
		this.chainManager = chainManager;
	}

	@Override
	protected AbstractShiroFilter createInstance() throws Exception {
		log.debug("Creating Shiro Filter instance...");

		SecurityManager securityManager = this.getSecurityManager();
		if (securityManager == null) {
			String msg = "SecurityManager property must be set.";
			throw new BeanInitializationException(msg);
		}
		if (!(securityManager instanceof WebSecurityManager)) {
			String msg = "The security manager does not implement the WebSecurityManager interface.";
			throw new BeanInitializationException(msg);
		}

		FilterChainManager chainManager = this.createFilterChainManager();
		// Expose the constructed FilterChainManager by first wrapping it in a
		// FilterChainResolver implementation. The AbstractShiroFilter
		// implementations
		// do not know about FilterChainManagers - only resolvers:
		IamPathMatchingFilterChainResolver chainResolver = new IamPathMatchingFilterChainResolver();
		chainResolver.setFilterChainManager(chainManager);

		// Now create a concrete ShiroFilter instance and apply the acquired
		// SecurityManager and built
		// FilterChainResolver. It doesn't matter that the instance is an
		// anonymous inner class
		// here - we're just using it because it is a concrete
		// AbstractShiroFilter instance that accepts
		// injection of the SecurityManager and FilterChainResolver:
		return new IamSpringShiroFilter((WebSecurityManager) securityManager, chainResolver);
	}

	@Override
	protected FilterChainManager createFilterChainManager() {
		Map<String, Filter> defaultFilters = chainManager.getFilters();
		// apply global settings if necessary:
		for (Filter filter : defaultFilters.values()) {
			applyGlobalPropertiesIfNecessary(filter);
		}

		// Apply the acquired and/or configured filters:
		Map<String, Filter> filters = getFilters();
		if (!CollectionUtils.isEmpty(filters)) {
			for (Map.Entry<String, Filter> entry : filters.entrySet()) {
				String name = entry.getKey();
				Filter filter = entry.getValue();
				applyGlobalPropertiesIfNecessary(filter);
				if (filter instanceof Nameable) {
					((Nameable) filter).setName(name);
				}
				// 'init' argument is false, since Spring-configured filters
				// should be initialized
				// in Spring (i.e. 'init-method=blah') or implement
				// InitializingBean:
				chainManager.addFilter(name, filter, false);
			}
		}

		// build up the chains:
		Map<String, String> chains = getFilterChainDefinitionMap();
		if (!CollectionUtils.isEmpty(chains)) {
			for (Map.Entry<String, String> entry : chains.entrySet()) {
				String url = entry.getKey();
				String chainDefinition = entry.getValue();
				chainManager.createChain(url, chainDefinition);
			}
		}

		return chainManager;
	}

	/**
	 * See: {@link ShiroFilterFactoryBean#applyLoginUrlIfNecessary}
	 *
	 * @param filter
	 */
	private void applyLoginUrlIfNecessary(Filter filter) {
		String loginUrl = getLoginUrl();
		if (StringUtils.hasText(loginUrl) && (filter instanceof AccessControlFilter)) {
			AccessControlFilter acFilter = (AccessControlFilter) filter;
			// only apply the login url if they haven't explicitly configured
			// one already:
			String existingLoginUrl = acFilter.getLoginUrl();
			if (AccessControlFilter.DEFAULT_LOGIN_URL.equals(existingLoginUrl)) {
				acFilter.setLoginUrl(loginUrl);
			}
		}
	}

	/**
	 * See: {@link ShiroFilterFactoryBean#applySuccessUrlIfNecessary}
	 *
	 * @param filter
	 */
	private void applySuccessUrlIfNecessary(Filter filter) {
		String successUrl = getSuccessUrl();
		if (StringUtils.hasText(successUrl) && (filter instanceof AuthenticationFilter)) {
			AuthenticationFilter authcFilter = (AuthenticationFilter) filter;
			// only apply the successUrl if they haven't explicitly configured
			// one already:
			String existingSuccessUrl = authcFilter.getSuccessUrl();
			if (AuthenticationFilter.DEFAULT_SUCCESS_URL.equals(existingSuccessUrl)) {
				authcFilter.setSuccessUrl(successUrl);
			}
		}
	}

	/**
	 * See: {@link ShiroFilterFactoryBean#applyUnauthorizedUrlIfNecessary}
	 *
	 * @param filter
	 */
	private void applyUnauthorizedUrlIfNecessary(Filter filter) {
		String unauthorizedUrl = getUnauthorizedUrl();
		if (StringUtils.hasText(unauthorizedUrl) && (filter instanceof AuthorizationFilter)) {
			AuthorizationFilter authzFilter = (AuthorizationFilter) filter;
			// only apply the unauthorizedUrl if they haven't explicitly
			// configured one already:
			String existingUnauthorizedUrl = authzFilter.getUnauthorizedUrl();
			if (existingUnauthorizedUrl == null) {
				authzFilter.setUnauthorizedUrl(unauthorizedUrl);
			}
		}
	}

	/**
	 * See: {@link ShiroFilterFactoryBean#applyGlobalPropertiesIfNecessary}
	 *
	 * @param filter
	 */
	private void applyGlobalPropertiesIfNecessary(Filter filter) {
		applyLoginUrlIfNecessary(filter);
		applySuccessUrlIfNecessary(filter);
		applyUnauthorizedUrlIfNecessary(filter);
	}

	/**
	 * <h6>Unaltered replicated:
	 * [{@link org.apache.shiro.spring.web.ShiroFilterFactoryBean.SpringShiroFilter}]
	 * </h6> Ordinarily the {@code AbstractShiroFilter} must be subclassed to
	 * additionally perform configuration and initialization behavior. Because
	 * this {@code FactoryBean} implementation manually builds the
	 * {@link AbstractShiroFilter}'s
	 * {@link AbstractShiroFilter#setSecurityManager(org.apache.shiro.web.mgt.WebSecurityManager)
	 * securityManager} and
	 * {@link AbstractShiroFilter#setFilterChainResolver(org.apache.shiro.web.filter.mgt.FilterChainResolver)
	 * filterChainResolver} properties, the only thing left to do is set those
	 * properties explicitly. We do that in a simple concrete subclass in the
	 * constructor.
	 */
	private static final class IamSpringShiroFilter extends AbstractShiroFilter {

		protected IamSpringShiroFilter(WebSecurityManager webSecurityManager, FilterChainResolver resolver) {
			if (webSecurityManager == null) {
				throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
			}
			setSecurityManager(webSecurityManager);
			if (resolver != null) {
				setFilterChainResolver(resolver);
			}
		}
	}

}