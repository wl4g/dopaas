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
package com.wl4g.devops.iam.common.config;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.NameableFilter;
import org.apache.shiro.web.servlet.SimpleCookie;

import static com.wl4g.devops.iam.common.config.XssProperties.*;
import static com.wl4g.devops.iam.common.config.CorsProperties.*;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.Assert.notNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.validation.constraints.NotBlank;

import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.wl4g.devops.common.config.OptionalPrefixControllerAutoConfiguration;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.iam.common.annotation.IamController;
import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.aop.XssSecurityResolveInterceptor;
import com.wl4g.devops.iam.common.authz.EnhancedModularRealmAuthorizer;
import com.wl4g.devops.iam.common.cache.JedisIamCacheManager;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.iam.common.core.IamFilterChainManager;
import com.wl4g.devops.iam.common.core.IamShiroFilterFactoryBean;
import com.wl4g.devops.iam.common.crypto.AesIamCipherService;
import com.wl4g.devops.iam.common.crypto.BlowfishIamCipherService;
import com.wl4g.devops.iam.common.crypto.IamCipherService;
import com.wl4g.devops.iam.common.crypto.IamCipherService.CipherCryptKind;
import com.wl4g.devops.iam.common.crypto.Des3IamCipherService;
import com.wl4g.devops.iam.common.filter.IamAuthenticationFilter;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.common.mgt.IamSubjectFactory;
import com.wl4g.devops.iam.common.realm.AbstractPermittingAuthorizingRealm;
import com.wl4g.devops.iam.common.security.cipher.CipherRequestSecurityFilter;
import com.wl4g.devops.iam.common.security.cipher.CipherRequestWrapper;
import com.wl4g.devops.iam.common.security.cipher.CipherRequestWrapperFactory;
import com.wl4g.devops.iam.common.security.cors.CorsSecurityFilter;
import com.wl4g.devops.iam.common.security.cors.CorsSecurityFilter.AdvancedCorsProcessor;
import com.wl4g.devops.iam.common.security.xss.XssSecurityResolver;
import com.wl4g.devops.iam.common.session.mgt.IamSessionFactory;
import com.wl4g.devops.iam.common.session.mgt.JedisIamSessionDAO;
import com.wl4g.devops.iam.common.session.mgt.support.IamUidSessionIdGenerator;
import com.wl4g.devops.iam.common.web.IamErrorConfiguring;

import redis.clients.jedis.JedisCluster;

/**
 * Abstract IAM common based configuration.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2018年12月23日
 * @since
 */
public abstract class AbstractIamConfiguration extends OptionalPrefixControllerAutoConfiguration {

	// ==============================
	// Locale i18n configuration.
	// ==============================

	/**
	 * A delegate message resource. Note that this bean can instantiate multiple
	 * different 'base-names', so the name must be unique
	 *
	 * @return
	 */
	@Bean(BEAN_DELEGATE_MSG_SOURCE)
	@ConditionalOnMissingBean
	public SessionDelegateMessageBundle sessionDelegateMessageBundle() {
		return new SessionDelegateMessageBundle(AbstractIamConfiguration.class);
	}

	// ==============================
	// S H I R O _ C O N F I G's.
	// ==============================

	@Bean
	public FilterChainManager filterChainManager() {
		return new IamFilterChainManager();
	}

	@Bean
	public EnhancedModularRealmAuthorizer enhancedModularRealmAuthorizer(List<AbstractPermittingAuthorizingRealm> realms) {
		// Register define realm.
		return new EnhancedModularRealmAuthorizer(realms.stream().collect(toList()));
	}

	@Bean
	public ShiroFilterFactoryBean shiroFilter(AbstractIamProperties<? extends ParamProperties> config,
			DefaultWebSecurityManager securityManager, FilterChainManager chainManager) {
		/*
		 * Note: The purpose of using Iam Shiro FilterFactory Bean is to use Iam
		 * Path Matching Filter Chain Resolver, while Iam Path Matching Filter
		 * Chain Resolver mainly implements the servlet/filter matching
		 * specification of getChain () method for default enhancements (because
		 * Shiro does not implement it, this causes serious problems)
		 */
		IamShiroFilterFactoryBean shiroFilter = new IamShiroFilterFactoryBean(chainManager);
		shiroFilter.setSecurityManager(securityManager);

		/*
		 * IAM server login page.(shiro default by "/login.jsp")
		 */
		shiroFilter.setLoginUrl(config.getLoginUri());
		// Default login success callback URL.
		shiroFilter.setSuccessUrl(config.getSuccessUri());
		// IAM server 403 page URL
		shiroFilter.setUnauthorizedUrl(config.getUnauthorizedUri());

		// Register define filters.
		Map<String, Filter> filters = new LinkedHashMap<>();
		// Register define filter mapping.
		Map<String, String> filterChain = new LinkedHashMap<>();
		actx.getBeansWithAnnotation(IamFilter.class).values().stream().forEach(filter -> {
			String filterName = null, uriPertten = null;
			if (filter instanceof NameableFilter) {
				filterName = (String) ReflectionUtils.invokeMethod(ReflectionUtils.findMethod(filter.getClass(), "getName"),
						filter);
			}
			if (filter instanceof IamAuthenticationFilter) {
				uriPertten = ((IamAuthenticationFilter) filter).getUriMapping();
			}
			notNull(filterName, "'filterName' must not be null");
			notNull(uriPertten, "'uriPertten' must not be null");

			if (filters.putIfAbsent(filterName, (Filter) filter) != null) {
				throw new IllegalStateException(String.format("Already filter. [%s]", filterName));
			}
			if (filterChain.putIfAbsent(uriPertten, filterName) != null) {
				throw new IllegalStateException(String.format("Already filter mapping. [%s] = %s", uriPertten, filterName));
			}
		});
		// Filter chain definition register
		shiroFilter.setFilters(filters);

		// Add external filter chain configuration
		config.getFilterChain().forEach((uriPertten, filterName) -> {
			if (filterChain.putIfAbsent(uriPertten, filterName) != null) {
				throw new IllegalStateException(String.format("Already filter mapping. [%s] = %s", uriPertten, filterName));
			}
		});

		// Filter chain mappings register
		shiroFilter.setFilterChainDefinitionMap(filterChain);

		return shiroFilter;
	}

	@Bean
	@ConditionalOnMissingBean
	public IamSubjectFactory iamSubjectFactory(AbstractIamProperties<? extends ParamProperties> config) {
		return new IamSubjectFactory(config);
	}

	@Bean
	public JedisIamCacheManager jedisIamCacheManager(AbstractIamProperties<? extends ParamProperties> config,
			JedisCluster jedisCluster) {
		return new JedisIamCacheManager(config.getCache().getPrefix(), jedisCluster);
	}

	@Bean
	public IamUidSessionIdGenerator iamUidSessionIdGenerator() {
		return new IamUidSessionIdGenerator();
	}

	@Bean
	public JedisIamSessionDAO jedisIamSessionDAO(AbstractIamProperties<? extends ParamProperties> config,
			JedisIamCacheManager cacheManager, IamUidSessionIdGenerator sessionIdGenerator) {
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
	// A U T H E N T I C A T I O N _ C O N F I G's.
	// ==============================

	// ==============================
	// A U T H E N T I C A T I O N _ R E G I S T R A T I O N _ C O N F I G's.
	// Reference See: http://www.hillfly.com/2017/179.html
	// org.apache.catalina.core.ApplicationFilterChain#internalDoFilter
	// ==============================

	// ==============================
	// A U T H O R I Z I N G _ R E A L M _ C O N F I G's.
	// ==============================

	// ==============================
	// A U T H E N T I C A T I O N _ H A N D L E R _ C O N F I G's.
	// ==============================

	// ==============================
	// I A M _ C O N T R O L L E R _ C O N F I G's.
	// ==============================

	/**
	 * Ne IAM controller prefix request handler mapping.
	 * 
	 * @param mappingPrefix
	 * @return
	 */
	protected PrefixHandlerMapping newIamControllerPrefixHandlerMapping(@NotBlank String mappingPrefix) {
		return super.newPrefixHandlerMapping(mappingPrefix, IamController.class);
	}

	// ==============================
	// IAM security attacks protect's
	// ==============================

	//
	// X S S _ I N T E R C E P T O R _ C O N F I G's.
	//

	@Bean
	@ConditionalOnProperty(name = KEY_XSS_PREFIX + ".enabled", matchIfMissing = true)
	@ConfigurationProperties(prefix = KEY_XSS_PREFIX)
	public XssProperties xssProperties() {
		return new XssProperties();
	}

	@Bean
	@ConditionalOnBean(XssProperties.class)
	public XssSecurityResolver xssSecurityResolver() {
		return new XssSecurityResolver() {
		};
	}

	@Bean
	@ConditionalOnBean({ XssSecurityResolver.class })
	public XssSecurityResolveInterceptor xssSecurityResolveInterceptor(XssProperties config, XssSecurityResolver resolver) {
		return new XssSecurityResolveInterceptor(config, resolver);
	}

	@Bean
	@ConditionalOnBean(XssSecurityResolveInterceptor.class)
	public AspectJExpressionPointcutAdvisor xssSecurityResolverAspectJExpressionPointcutAdvisor(XssProperties config,
			XssSecurityResolveInterceptor advice) {
		AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
		advisor.setExpression(config.getExpression());
		advisor.setAdvice(advice);
		return advisor;
	}

	//
	// C O R S _ F I L T E R _ C O N F I G's.
	//

	@Bean
	@ConditionalOnProperty(name = KEY_CORS_PREFIX + ".enabled", matchIfMissing = true)
	@ConfigurationProperties(prefix = KEY_CORS_PREFIX)
	public CorsProperties corsProperties() {
		return new CorsProperties();
	}

	@Bean
	@ConditionalOnBean(CorsProperties.class)
	public AdvancedCorsProcessor advancedCorsProcessor() {
		return new AdvancedCorsProcessor();
	}

	@Bean
	@ConditionalOnBean(CorsProperties.class)
	public CorsSecurityFilter corsSecurityFilter(CorsProperties config, AdvancedCorsProcessor corsProcessor) {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// Merger transformation configuration
		config.getRules().forEach((key, rule) -> source.registerCorsConfiguration(key, rule.toSpringCorsConfiguration()));
		CorsSecurityFilter filter = new CorsSecurityFilter(source);
		filter.setCorsProcessor(corsProcessor);
		return filter;
	}

	/**
	 * The requirement for using the instruction is that the creation of
	 * {@link CorsProperties} object beans must precede this</br>
	 * e.g.
	 *
	 * <pre>
	 * &#64;Bean
	 * public CorsProperties corsProperties() {
	 * 	...
	 * }
	 * </pre>
	 *
	 * <b style="color:red;font-size:40px">&nbsp;↑</b>
	 *
	 * <pre>
	 * &#64;Bean
	 * &#64;ConditionalOnBean(CorsProperties.class)
	 * public FilterRegistrationBean corsResolveSecurityFilterBean(CorsProperties config) {
	 * 	...
	 * }
	 * </pre>
	 */
	@Bean
	@ConditionalOnBean(CorsProperties.class)
	public FilterRegistrationBean corsResolveSecurityFilterBean(CorsSecurityFilter filter) {
		// Register CORS filter
		FilterRegistrationBean filterBean = new FilterRegistrationBean(filter);
		filterBean.setOrder(ORDER_CORS_PRECEDENCE);
		// Cannot use '/*' or it will not be added to the container chain (only
		// '/**')
		filterBean.addUrlPatterns("/*");
		return filterBean;
	}

	//
	// C I P H E R _ A N D _ F I L T E R _ C O N F I G's.
	//

	@Bean
	public AesIamCipherService aesIamCipherService() {
		return new AesIamCipherService();
	}

	@Bean
	public BlowfishIamCipherService blowfishIamCipherService() {
		return new BlowfishIamCipherService();
	}

	@Bean
	public Des3IamCipherService des3IamCipherService() {
		return new Des3IamCipherService();
	}

	@Bean
	public GenericOperatorAdapter<CipherCryptKind, IamCipherService> compositeIamCipherServiceAdapter(
			List<IamCipherService> cipherServices) {
		return new GenericOperatorAdapter<CipherCryptKind, IamCipherService>(cipherServices) {
		};
	}

	/**
	 * Can be used to extend and create a custom {@link CipherRequestWrapper}
	 * instance.
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public CipherRequestWrapperFactory cipherRequestWrapperFactory() {
		return new CipherRequestWrapperFactory() {
		};
	}

	@Bean
	public CipherRequestSecurityFilter cipherRequestSecurityFilter(AbstractIamProperties<? extends ParamProperties> config,
			CipherRequestWrapperFactory factory) {
		return new CipherRequestSecurityFilter(config, factory);
	}

	@Bean
	public FilterRegistrationBean cipherRequestSecurityFilterBean(CipherRequestSecurityFilter filter) {
		// Register cipher filter
		FilterRegistrationBean filterBean = new FilterRegistrationBean(filter);
		filterBean.setOrder(ORDER_CIPHER_PRECEDENCE);
		// Cannot use '/*' or it will not be added to the container chain (only
		// '/**')
		filterBean.addUrlPatterns("/*");
		return filterBean;
	}

	// ==============================
	// IAM _ O T H E R _ C O N F I G's.
	// ==============================

	@Bean
	public IamErrorConfiguring iamErrorConfiguring() {
		return new IamErrorConfiguring();
	}

	final public static int ORDER_CORS_PRECEDENCE = Ordered.HIGHEST_PRECEDENCE + 10;
	final public static int ORDER_CIPHER_PRECEDENCE = Ordered.HIGHEST_PRECEDENCE + 11;

}