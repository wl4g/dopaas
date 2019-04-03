package com.wl4g.devops.iam.common.config;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.NameableFilter;
import org.apache.shiro.web.servlet.SimpleCookie;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.wl4g.devops.common.config.AbstractOptionalControllerConfiguration;
import com.wl4g.devops.iam.common.annotation.IamController;
import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.iam.common.core.IamShiroFilterFactoryBean;
import com.wl4g.devops.iam.common.filter.IamAuthenticationFilter;
import com.wl4g.devops.iam.common.mgt.IamSubjectFactory;
import com.wl4g.devops.iam.common.session.mgt.IamSessionFactory;
import com.wl4g.devops.iam.common.session.mgt.JedisIamSessionDAO;
import com.wl4g.devops.iam.common.session.mgt.support.IamUidSessionIdGenerator;

import redis.clients.jedis.JedisCluster;

public abstract class AbstractIamConfiguration extends AbstractOptionalControllerConfiguration {

	// ==============================
	// Shiro manager and filter's
	// ==============================

	@Bean
	public ShiroFilterFactoryBean shiroFilter(AbstractIamProperties<? extends ParamProperties> config,
			DefaultWebSecurityManager securityManager) {
		/*
		 * Note: The purpose of using Iam Shiro FilterFactory Bean is to use Iam
		 * Path Matching Filter Chain Resolver, while Iam Path Matching Filter
		 * Chain Resolver mainly implements the servlet/filter matching
		 * specification of getChain () method for default enhancements (because
		 * Shiro does not implement it, this causes serious problems)
		 */
		IamShiroFilterFactoryBean shiroFilterFB = new IamShiroFilterFactoryBean();
		shiroFilterFB.setSecurityManager(securityManager);

		/*
		 * IAM server login page.(shiro default by "/login.jsp")
		 */
		shiroFilterFB.setLoginUrl(config.getLoginUri());
		// Default login success callback URL.
		shiroFilterFB.setSuccessUrl(config.getSuccessUri());
		// IAM server 403 page URL
		shiroFilterFB.setUnauthorizedUrl(config.getUnauthorizedUri());

		// Register define filters.
		Map<String, Filter> filters = new HashMap<>();
		// Register define filter mapping.
		Map<String, String> filterChain = new HashMap<>();
		this.actx.getBeansWithAnnotation(IamFilter.class).values().stream().forEach(filter -> {
			String filterName = null, uriPertten = null;
			if (filter instanceof NameableFilter) {
				filterName = (String) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(filter.getClass(), "getName"),
						filter);
			}
			if (filter instanceof IamAuthenticationFilter) {
				uriPertten = ((IamAuthenticationFilter) filter).getUriMapping();
			}
			Assert.notNull(filterName, "'filterName' must not be null");
			Assert.notNull(uriPertten, "'uriPertten' must not be null");

			if (filters.putIfAbsent(filterName, (Filter) filter) != null) {
				throw new IllegalStateException(String.format("Already filter. [%s]", filterName));
			}
			if (filterChain.putIfAbsent(uriPertten, filterName) != null) {
				throw new IllegalStateException(String.format("Already filter mapping. [%s] = %s", uriPertten, filterName));
			}
		});
		// Filter chain definition register
		shiroFilterFB.setFilters(filters);

		// Add external filter chain configuration
		config.getFilterChain().forEach((uriPertten, filterName) -> {
			if (filterChain.putIfAbsent(uriPertten, filterName) != null) {
				throw new IllegalStateException(String.format("Already filter mapping. [%s] = %s", uriPertten, filterName));
			}
		});

		// Filter chain mappings register
		shiroFilterFB.setFilterChainDefinitionMap(filterChain);

		return shiroFilterFB;
	}

	@Bean
	public IamSubjectFactory iamSubjectFactory() {
		return new IamSubjectFactory();
	}

	@Bean
	public JedisCacheManager jedisCacheManager(AbstractIamProperties<? extends ParamProperties> config,
			JedisCluster jedisCluster) {
		return new JedisCacheManager(config.getCache().getPrefix(), jedisCluster);
	}

	@Bean
	public IamUidSessionIdGenerator iamUidSessionIdGenerator() {
		return new IamUidSessionIdGenerator();
	}

	@Bean
	public JedisIamSessionDAO jedisIamSessionDAO(AbstractIamProperties<? extends ParamProperties> config,
			JedisCacheManager cacheManager, IamUidSessionIdGenerator sessionIdGenerator) {
		JedisIamSessionDAO sessionDAO = new JedisIamSessionDAO(config, cacheManager);
		sessionDAO.setSessionIdGenerator(sessionIdGenerator);
		return sessionDAO;
	}

	@Bean
	public IamSessionFactory iamSessionFactory() {
		return new IamSessionFactory();
	}

	@Bean
	public SimpleCookie simpleCookie(AbstractIamProperties<? extends ParamProperties> config) {
		return new SimpleCookie(config.getCookie());
	}

	/**
	 * Ensuring the execution of beans that implement lifecycle functions within
	 * Shiro
	 * 
	 * @return
	 */
	@Bean("lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	@DependsOn("lifecycleBeanPostProcessor")
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator advisorCreator = new DefaultAdvisorAutoProxyCreator();
		advisorCreator.setProxyTargetClass(true);
		return advisorCreator;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authzAdvisor = new AuthorizationAttributeSourceAdvisor();
		authzAdvisor.setSecurityManager(securityManager);
		return authzAdvisor;
	}

	// ==============================
	// Authentication filter`s.
	// ==============================

	// ==============================
	// Authentication filter`s registration
	// Reference See: http://www.hillfly.com/2017/179.html
	// org.apache.catalina.core.ApplicationFilterChain#internalDoFilter
	// ==============================

	// ==============================
	// Authorizing realm`s
	// ==============================

	// ==============================
	// Configuration properties.
	// ==============================

	// ==============================
	// Authentication handler's
	// ==============================

	// ==============================
	// IAM controller's
	// ==============================

	@Bean
	public PrefixHandlerMapping iamControllerPrefixHandlerMapping() {
		return super.createPrefixHandlerMapping();
	}

	@Override
	protected Class<? extends Annotation> annotationClass() {
		return IamController.class;
	}

	// ==============================
	// IAM Other's
	// ==============================

}
