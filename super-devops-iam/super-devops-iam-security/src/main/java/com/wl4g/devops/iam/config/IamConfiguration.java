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
package com.wl4g.devops.iam.config;

import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_BASE;
import com.wl4g.devops.common.kit.access.IPAccessControl;
import com.wl4g.devops.iam.authc.credential.DefaultCredentialsHashedMatcher;
import com.wl4g.devops.iam.authc.credential.Oauth2AuthorizingBoundMatcher;
import com.wl4g.devops.iam.authc.credential.secure.DefaultCredentialsSecurer;
import com.wl4g.devops.iam.authc.credential.secure.IamCredentialsSecurer;
import com.wl4g.devops.iam.authc.pam.ExceptionModularRealmAuthenticator;
import com.wl4g.devops.iam.common.cache.EnhancedCacheManager;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.common.config.AbstractIamConfiguration;
import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.iam.common.context.SecurityCoprocessor;
import com.wl4g.devops.iam.common.mgt.IamSubjectFactory;
import com.wl4g.devops.iam.common.session.mgt.IamSessionFactory;
import com.wl4g.devops.iam.common.session.mgt.JedisIamSessionDAO;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.configure.DefaultSecurerConfigureAdapter;
import com.wl4g.devops.iam.configure.SecurerConfigureAdapter;
import com.wl4g.devops.iam.context.AnynothingSecurityCoprocessor;
import com.wl4g.devops.iam.context.ServerSecurityCoprocessor;
import com.wl4g.devops.iam.filter.AuthenticatorAuthenticationFilter;
import com.wl4g.devops.iam.filter.DingtalkAuthenticationFilter;
import com.wl4g.devops.iam.filter.FacebookAuthenticationFilter;
import com.wl4g.devops.iam.filter.QrcodeAuthenticationFilter;
import com.wl4g.devops.iam.filter.ROOTAuthenticationFilter;
import com.wl4g.devops.iam.filter.SinaAuthenticationFilter;
import com.wl4g.devops.iam.filter.GeneralAuthenticationFilter;
import com.wl4g.devops.iam.filter.GithubAuthenticationFilter;
import com.wl4g.devops.iam.filter.GoogleAuthenticationFilter;
import com.wl4g.devops.iam.filter.InternalWhiteListServerAuthenticationFilter;
import com.wl4g.devops.iam.filter.LogoutAuthenticationFilter;
import com.wl4g.devops.iam.filter.QQAuthenticationFilter;
import com.wl4g.devops.iam.filter.SmsAuthenticationFilter;
import com.wl4g.devops.iam.filter.TwitterAuthenticationFilter;
import com.wl4g.devops.iam.filter.WechatAuthenticationFilter;
import com.wl4g.devops.iam.filter.WechatMpAuthenticationFilter;
import com.wl4g.devops.iam.handler.GentralAuthenticationHandler;
import com.wl4g.devops.iam.handler.DefaultJdkRandomCaptchaHandler;
import com.wl4g.devops.iam.handler.CaptchaHandler;
import com.wl4g.devops.iam.realm.AbstractIamAuthorizingRealm;
import com.wl4g.devops.iam.realm.DingtalkAuthorizingRealm;
import com.wl4g.devops.iam.realm.FacebookAuthorizingRealm;
import com.wl4g.devops.iam.realm.QrcodeAuthorizingRealm;
import com.wl4g.devops.iam.realm.SinaAuthorizingRealm;
import com.wl4g.devops.iam.realm.GeneralAuthorizingRealm;
import com.wl4g.devops.iam.realm.GithubAuthorizingRealm;
import com.wl4g.devops.iam.realm.GoogleAuthorizingRealm;
import com.wl4g.devops.iam.realm.QQAuthorizingRealm;
import com.wl4g.devops.iam.realm.SmsAuthorizingRealm;
import com.wl4g.devops.iam.realm.TwitterAuthorizingRealm;
import com.wl4g.devops.iam.realm.WechatAuthorizingRealm;
import com.wl4g.devops.iam.realm.WechatMpAuthorizingRealm;
import com.wl4g.devops.iam.session.mgt.IamServerSessionManager;
import com.wl4g.devops.iam.web.CentralAuthenticatorController;

public class IamConfiguration extends AbstractIamConfiguration {

	final private static String BEAN_ROOT_FILTER = "rootAuthenticationFilter";
	final private static String BEAN_AUTH_FILTER = "authenticatorAuthenticationFilter";
	final private static String BEAN_OAUTH2_MATCHER = "oauth2BoundMatcher";

	// ==============================
	// Shiro manager and filter's
	// ==============================

	@Bean
	public DefaultWebSecurityManager securityManager(IamSubjectFactory subjectFactory, IamServerSessionManager sessionManager,
			ModularRealmAuthenticator authenticator) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setSessionManager(sessionManager);
		// Register define realm.
		List<Realm> realms = actx.getBeansOfType(AbstractIamAuthorizingRealm.class).values().stream()
				.collect(Collectors.toList());
		securityManager.setRealms(realms);
		securityManager.setSubjectFactory(subjectFactory);
		// Multiple realm authenticator controller
		securityManager.setAuthenticator(authenticator);
		return securityManager;
	}

	@Bean
	public ExceptionModularRealmAuthenticator modularRealmAuthenticator(AuthenticationStrategy authenticationStrategy) {
		ExceptionModularRealmAuthenticator authenticator = new ExceptionModularRealmAuthenticator();
		authenticator.setAuthenticationStrategy(authenticationStrategy);
		List<Realm> realms = actx.getBeansOfType(AbstractIamAuthorizingRealm.class).values().stream()
				.collect(Collectors.toList());
		authenticator.setRealms(realms);
		return authenticator;
	}

	@Bean
	public FirstSuccessfulStrategy firstSuccessfulStrategy() {
		return new FirstSuccessfulStrategy();
	}

	@Bean
	public IamServerSessionManager iamServerSessionManager(IamSessionFactory sessionFactory, JedisIamSessionDAO sessionDao,
			EnhancedCacheManager cacheManager, SimpleCookie cookie, IamProperties config) {
		IamServerSessionManager sessionManager = new IamServerSessionManager(config);
		sessionManager.setSessionFactory(sessionFactory);
		sessionManager.setSessionDAO(sessionDao);
		sessionManager.setSessionIdCookie(cookie);
		sessionManager.setCacheManager(cacheManager);
		sessionManager.setSessionIdUrlRewritingEnabled(config.getSession().isUrlRewriting());
		sessionManager.setSessionIdCookieEnabled(true);
		sessionManager.setSessionValidationInterval(config.getSession().getSessionValidationInterval());
		sessionManager.setGlobalSessionTimeout(config.getSession().getGlobalSessionTimeout());
		return sessionManager;
	}

	// ==============================
	// Hashing matcher`s.
	// ==============================

	@Bean
	public DefaultCredentialsHashedMatcher defaultCredentialsHashedMatcher() {
		return new DefaultCredentialsHashedMatcher();
	}

	@Bean(BEAN_OAUTH2_MATCHER)
	public Oauth2AuthorizingBoundMatcher oauth2AuthorizingBoundMatcher() {
		return new Oauth2AuthorizingBoundMatcher();
	}

	// ==============================
	// Credentials securer's.
	// ==============================

	@Bean
	@ConditionalOnMissingBean
	public SecurerConfigureAdapter securerConfigureAdapter() {
		return new DefaultSecurerConfigureAdapter();
	}

	@Bean
	@ConditionalOnMissingBean
	public IamCredentialsSecurer iamCredentialsSecurer(SecurerConfigureAdapter adapter, JedisCacheManager cacheManager) {
		return new DefaultCredentialsSecurer(adapter.configure(), cacheManager);
	}

	// ==============================
	// Authentication filter`s.
	// ==============================

	@Bean(BEAN_AUTH_FILTER)
	public AuthenticatorAuthenticationFilter authenticatorAuthenticationFilter(IamContextManager manager) {
		return new AuthenticatorAuthenticationFilter(manager);
	}

	@Bean(BEAN_ROOT_FILTER)
	public ROOTAuthenticationFilter rootAuthenticationFilter(IamContextManager manager) {
		return new ROOTAuthenticationFilter(manager);
	}

	@Bean
	public InternalWhiteListServerAuthenticationFilter internalWhiteListServerAuthenticationFilter(IPAccessControl control,
			AbstractIamProperties<? extends ParamProperties> config) {
		return new InternalWhiteListServerAuthenticationFilter(control, config);
	}

	@Bean
	public QrcodeAuthenticationFilter qrcodeAuthenticationFilter(IamContextManager manager) {
		return new QrcodeAuthenticationFilter(manager);
	}

	@Bean
	public FacebookAuthenticationFilter facebookAuthenticationFilter(IamContextManager manager) {
		return new FacebookAuthenticationFilter(manager);
	}

	@Bean
	public SmsAuthenticationFilter smsAuthenticationFilter(IamContextManager manager) {
		return new SmsAuthenticationFilter(manager);
	}

	@Bean
	public WechatAuthenticationFilter wechatAuthenticationFilter(IamContextManager manager) {
		return new WechatAuthenticationFilter(manager);
	}

	@Bean
	public WechatMpAuthenticationFilter wechatMpAuthenticationFilter(IamContextManager manager) {
		return new WechatMpAuthenticationFilter(manager);
	}

	@Bean
	public GeneralAuthenticationFilter generalAuthenticationFilter(IamContextManager manager) {
		return new GeneralAuthenticationFilter(manager);
	}

	@Bean
	public LogoutAuthenticationFilter logoutAuthenticationFilter(IamContextManager manager) {
		return new LogoutAuthenticationFilter(manager);
	}

	@Bean
	public DingtalkAuthenticationFilter dingtalkAuthenticationFilter(IamContextManager manager) {
		return new DingtalkAuthenticationFilter(manager);
	}

	@Bean
	public GoogleAuthenticationFilter googleAuthenticationFilter(IamContextManager manager) {
		return new GoogleAuthenticationFilter(manager);
	}

	@Bean
	public TwitterAuthenticationFilter twitterAuthenticationFilter(IamContextManager manager) {
		return new TwitterAuthenticationFilter(manager);
	}

	@Bean
	public QQAuthenticationFilter qqAuthenticationFilter(IamContextManager manager) {
		return new QQAuthenticationFilter(manager);
	}

	@Bean
	public GithubAuthenticationFilter githubAuthenticationFilter(IamContextManager manager) {
		return new GithubAuthenticationFilter(manager);
	}

	@Bean
	public SinaAuthenticationFilter sinaAuthenticationFilter(IamContextManager manager) {
		return new SinaAuthenticationFilter(manager);
	}

	// ==============================
	// Authentication filter`s registration
	// Reference See: http://www.hillfly.com/2017/179.html
	// org.apache.catalina.core.ApplicationFilterChain#internalDoFilter
	// ==============================

	@Bean
	public FilterRegistrationBean authenticateFilterRegistrationBean(
			@Qualifier(BEAN_AUTH_FILTER) AuthenticatorAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean rootFilterRegistrationBean(@Qualifier(BEAN_ROOT_FILTER) ROOTAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean internalServerFilterRegistrationBean(InternalWhiteListServerAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean facebookFilterRegistrationBean(FacebookAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean smsFilterRegistrationBean(SmsAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean qrcodeFilterRegistrationBean(QrcodeAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean wechatFilterRegistrationBean(WechatAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean wechatMpFilterRegistrationBean(WechatMpAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean generalFilterRegistrationBean(GeneralAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean logoutFilterRegistrationBean(LogoutAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean dingtalkFilterRegistrationBean(DingtalkAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean googleFilterRegistrationBean(GoogleAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean qqFilterRegistrationBean(QQAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean twitterFilterRegistrationBean(TwitterAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean githubFilterRegistrationBean(GithubAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	@Bean
	public FilterRegistrationBean sinaFilterRegistrationBean(SinaAuthenticationFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean(filter);
		registration.setEnabled(false);
		return registration;
	}

	// ==============================
	// Authorizing realm`s
	// ==============================

	@Bean
	@ConditionalOnMissingBean
	public GeneralAuthorizingRealm generalAuthorizingRealm(DefaultCredentialsHashedMatcher matcher, IamContextManager manager) {
		return new GeneralAuthorizingRealm(matcher, manager);
	}

	@Bean
	@ConditionalOnMissingBean
	public SmsAuthorizingRealm smsAuthorizingRealm(DefaultCredentialsHashedMatcher matcher, IamContextManager manager) {
		return new SmsAuthorizingRealm(matcher, manager);
	}

	@Bean
	@ConditionalOnMissingBean
	public QrcodeAuthorizingRealm qrcodeAuthorizingRealm(DefaultCredentialsHashedMatcher matcher, IamContextManager manager) {
		return new QrcodeAuthorizingRealm(matcher, manager);
	}

	@Bean
	@ConditionalOnMissingBean
	public FacebookAuthorizingRealm facebookAuthorizingRealm(
			@Qualifier(BEAN_OAUTH2_MATCHER) Oauth2AuthorizingBoundMatcher matcher, IamContextManager manager) {
		return new FacebookAuthorizingRealm(matcher, manager);
	}

	@Bean
	@ConditionalOnMissingBean
	public WechatAuthorizingRealm wechatAuthorizingRealm(@Qualifier(BEAN_OAUTH2_MATCHER) Oauth2AuthorizingBoundMatcher matcher,
			IamContextManager manager) {
		return new WechatAuthorizingRealm(matcher, manager);
	}

	@Bean
	@ConditionalOnMissingBean
	public WechatMpAuthorizingRealm wechatMpAuthorizingRealm(
			@Qualifier(BEAN_OAUTH2_MATCHER) Oauth2AuthorizingBoundMatcher matcher, IamContextManager manager) {
		return new WechatMpAuthorizingRealm(matcher, manager);
	}

	@Bean
	@ConditionalOnMissingBean
	public DingtalkAuthorizingRealm dingtalkAuthorizingRealm(
			@Qualifier(BEAN_OAUTH2_MATCHER) Oauth2AuthorizingBoundMatcher matcher, IamContextManager manager) {
		return new DingtalkAuthorizingRealm(matcher, manager);
	}

	@Bean
	@ConditionalOnMissingBean
	public GoogleAuthorizingRealm googleAuthorizingRealm(@Qualifier(BEAN_OAUTH2_MATCHER) Oauth2AuthorizingBoundMatcher matcher,
			IamContextManager manager) {
		return new GoogleAuthorizingRealm(matcher, manager);
	}

	@Bean
	@ConditionalOnMissingBean
	public QQAuthorizingRealm qqAuthorizingRealm(@Qualifier(BEAN_OAUTH2_MATCHER) Oauth2AuthorizingBoundMatcher matcher,
			IamContextManager manager) {
		return new QQAuthorizingRealm(matcher, manager);
	}

	@Bean
	@ConditionalOnMissingBean
	public TwitterAuthorizingRealm twitterAuthorizingRealm(@Qualifier(BEAN_OAUTH2_MATCHER) Oauth2AuthorizingBoundMatcher matcher,
			IamContextManager manager) {
		return new TwitterAuthorizingRealm(matcher, manager);
	}

	@Bean
	@ConditionalOnMissingBean
	public SinaAuthorizingRealm sinaAuthorizingRealm(@Qualifier(BEAN_OAUTH2_MATCHER) Oauth2AuthorizingBoundMatcher matcher,
			IamContextManager manager) {
		return new SinaAuthorizingRealm(matcher, manager);
	}

	@Bean
	@ConditionalOnMissingBean
	public GithubAuthorizingRealm githubAuthorizingRealm(@Qualifier(BEAN_OAUTH2_MATCHER) Oauth2AuthorizingBoundMatcher matcher,
			IamContextManager manager) {
		return new GithubAuthorizingRealm(matcher, manager);
	}

	// ==============================
	// Configuration properties.
	// ==============================

	@Bean
	public IamProperties iamServerProperties() {
		return new IamProperties();
	}

	// ==============================
	// Authentication handler's
	// ==============================

	@Bean
	public GentralAuthenticationHandler gentralAuthenticationHandler(RestTemplate restTemplate, IamContextManager manager) {
		return new GentralAuthenticationHandler(manager.getServerSecurityContext(), restTemplate);
	}

	/**
	 * {@link com.wl4g.devops.iam.captcha.config.KaptchaConfiguration#captchaHandler}
	 * {@link com.wl4g.devops.iam.captcha.handler.KaptchaCaptchaHandler}. <br/>
	 * Notes for using `@ConditionalOnMissingBean': 1, `@Bean'method return
	 * value type must be the type using `@Autowired' annotation; 2, or use
	 * `Conditional OnMissing Bean'(MyInterface. class) in this way.`
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean
	public CaptchaHandler captchaHandler(IamProperties config, JedisCacheManager cacheManager) {
		return new DefaultJdkRandomCaptchaHandler(config, cacheManager);
	}

	// ==============================
	// IAM controller's
	// ==============================

	@Override
	protected String getMappingPrefix() {
		return URI_S_BASE;
	}

	@Bean
	public CentralAuthenticatorController defaultAuthenticatorController() {
		return new CentralAuthenticatorController();
	}

	// ==============================
	// IAM context's
	// ==============================

	@Bean
	@ConditionalOnMissingBean
	public ServerSecurityCoprocessor securityInterceptor() {
		return new AnynothingSecurityCoprocessor();
	}

	@Bean
	@ConditionalOnMissingBean
	public SecurityCoprocessor securityCoprocessor() {
		return new AnynothingSecurityCoprocessor();
	}

}