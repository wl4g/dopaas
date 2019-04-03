package com.zrk.demo.shiro;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.zrk.demo.config.SpringShiroAutoconfig;
import com.zrk.oauthclient.client.QqClient;
import com.zrk.oauthclient.client.SinaWeiboClient;
import com.zrk.oauthclient.client.WeiXinClient;
import com.zrk.oauthclient.shiro.support.ClientFilter;

/**
 * shiro配置
 * @author zrk  
 * @date 2016年5月10日 下午5:49:43
 */
@Configuration
public class ShiroConfig {
	
	//注入自定义shiro、oauthclient配置
	@Bean
	public SpringShiroAutoconfig getShiroConfig(){
		return new SpringShiroAutoconfig();
	}
	
	//第三方登录client配置
	@Bean
	public Clients getClients(){
		SpringShiroAutoconfig config = getShiroConfig();
		QqClient qqClient = new QqClient(config.getQqKey(),config.getQqSecret());
		WeiXinClient weiXinClient = new WeiXinClient(config.getWeixinKey(),config.getWeixinSecret());
		SinaWeiboClient sinaWeiboClient = new SinaWeiboClient(config.getWeiboKey(),config.getWeiboSecret());
		Clients clients = new Clients(config.getOauthCallback(),qqClient,weiXinClient,sinaWeiboClient);
		return clients;
	}
	
	@Bean
	public Config getConfig(){
		Config config =  new Config(getClients());
		return config;
	}
	
	//扩展的Realm
	@Bean(name = "shiroDbAndClientRealm")
	public ShiroDbAndClientRealm getShiroDbAndClientRealm(){
		ShiroDbAndClientRealm realm = new ShiroDbAndClientRealm();
		realm.setClients(getClients());
		return realm;
	}
	
	@Bean(name="lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}
	
	@Bean
	@DependsOn("lifecycleBeanPostProcessor")
	public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
	    daap.setProxyTargetClass(true);
		return daap;
	}
	/**************session缓存redis共享实现****************/
	/* 
	 * 1、需要添加shiro-redis包
	 * <dependency>
			<groupId>org.crazycake</groupId>
			<artifactId>shiro-redis</artifactId>
			<version>2.4.2.1-RELEASE</version>
		</dependency>
	 * 2、application.properties中配置redis
	 * #session cache ; unit: expire -> second ,  timeout -> millisecond  ;7200s = 2 hour
		redis.manager.host=192.168.10.7
		redis.manager.port=6379
		redis.manager.expire=7200
		redis.manager.timeout=10000
	 * 3、替换本类中内存版实现为redis实现
	 * 
	 */
//	@Bean(name="redisManager")
//	@ConfigurationProperties(prefix = "redis.manager")
//	public RedisManager getRedisManager() {
//		RedisManager redisManager = new RedisManager();
//		return redisManager;
//	}
//	@Bean(name="redisCacheManager")
//	public RedisCacheManager getRedisCacheManager() {
//		RedisCacheManager redisCacheManager = new RedisCacheManager();
//		redisCacheManager.setRedisManager(getRedisManager());
//		return redisCacheManager;
//	}
//	@Bean(name="redisSessionDAO")
//	public RedisSessionDAO getRedisSessionDAO() {
//		RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
//		redisSessionDAO.setRedisManager(getRedisManager());
//		return redisSessionDAO;
//	}
	/******************************/
	
	/**************session缓存内存版实现****************/
	@Bean(name="memoryCacheManager")
	public MemoryConstrainedCacheManager getMemoryCacheManager() {
		return new MemoryConstrainedCacheManager();
	}
	@Bean(name="sessionDAO")
	public SessionDAO getSessionDAO() {
		return new MemorySessionDAO();
	}
	/******************************/
	
	@Bean(name="sessionManager")
	public DefaultWebSessionManager getDefaultWebSessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionValidationSchedulerEnabled(true);
//		sessionManager.setSessionDAO(getRedisSessionDAO());
//		sessionManager.setGlobalSessionTimeout(getRedisManager().getTimeout()*1000);
		sessionManager.setSessionDAO(getSessionDAO());
		sessionManager.setGlobalSessionTimeout(30 * 60 * 1000);
		sessionManager.setSessionValidationSchedulerEnabled(true);
		sessionManager.setCacheManager(getMemoryCacheManager());
		
		return sessionManager;
	}
	
	
	
	@Bean(name = "securityManager")
	public DefaultWebSecurityManager defaultWebSecurityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(getShiroDbAndClientRealm());
//		securityManager.setCacheManager(getRedisCacheManager());
		securityManager.setCacheManager(getMemoryCacheManager());
		securityManager.setSessionManager(getDefaultWebSessionManager());
		
		return securityManager;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(defaultWebSecurityManager());
		return advisor;	
	}

	//shiro过滤器
	@Bean(name="shiroFilter")
	public ShiroFilterFactoryBean shiroFilterFactoryBean() {
		SpringShiroAutoconfig config = getShiroConfig();
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager());
		//第三方登陆回调过滤器
		ClientFilter clientFilter =  new ClientFilter();
		clientFilter.setClients(getClients());//
		clientFilter.setRedirectAfterSuccessfulAuthentication(true);
		Map<String, Filter> filterMap = new LinkedHashMap<String,Filter>();
		//定义第三方回调过滤器
		filterMap.put("client", clientFilter);
		Map<String, String> filterChainMap =  new LinkedHashMap<String, String>();
		for (String path : config.getFilter()){
			if(path!=null&&!"".equals(path)){
				String[] kv = path.split("=");
				filterChainMap.put(kv[0].trim(), kv[1].trim());
			}
		}
		shiroFilterFactoryBean.setFilters(filterMap);
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainMap);
		shiroFilterFactoryBean.setLoginUrl(config.getLoginUrl());
		shiroFilterFactoryBean.setSuccessUrl(config.getSuccessUrl());
		shiroFilterFactoryBean.setUnauthorizedUrl(config.getUnauthorizedUrl());
		return shiroFilterFactoryBean;
	}
	
}
