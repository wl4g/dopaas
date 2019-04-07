/*
 * Copyright 2015 the original author or authors.
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

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;

public class IamShiroFilterFactoryBean extends ShiroFilterFactoryBean {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Override
	protected AbstractShiroFilter createInstance() throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Creating Shiro Filter instance...");
		}

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