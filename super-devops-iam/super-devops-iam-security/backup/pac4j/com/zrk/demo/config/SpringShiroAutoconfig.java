package com.zrk.demo.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * shiro 配置信息
 * @author zrk  
 * @date 2016年5月10日 下午5:50:38
 */
@ConfigurationProperties(prefix = "spring.shiro")
public class SpringShiroAutoconfig {
	
	List<String> filter = new ArrayList<String>();
	
	
	private String loginUrl;
	private String successUrl;
	private String unauthorizedUrl;
	
	private String qqKey;
	private String qqSecret;
	
	private String WeixinKey;
	private String WeixinSecret;
	
	private String weiboKey;
	private String weiboSecret;
	
	private String oauthCallback;

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	public String getUnauthorizedUrl() {
		return unauthorizedUrl;
	}

	public void setUnauthorizedUrl(String unauthorizedUrl) {
		this.unauthorizedUrl = unauthorizedUrl;
	}

	public String getQqKey() {
		return qqKey;
	}

	public void setQqKey(String qqKey) {
		this.qqKey = qqKey;
	}

	public String getQqSecret() {
		return qqSecret;
	}

	public void setQqSecret(String qqSecret) {
		this.qqSecret = qqSecret;
	}

	public String getWeixinKey() {
		return WeixinKey;
	}

	public void setWeixinKey(String weixinKey) {
		WeixinKey = weixinKey;
	}

	public String getWeixinSecret() {
		return WeixinSecret;
	}

	public void setWeixinSecret(String weixinSecret) {
		WeixinSecret = weixinSecret;
	}

	public String getWeiboKey() {
		return weiboKey;
	}

	public void setWeiboKey(String weiboKey) {
		this.weiboKey = weiboKey;
	}

	public String getWeiboSecret() {
		return weiboSecret;
	}

	public void setWeiboSecret(String weiboSecret) {
		this.weiboSecret = weiboSecret;
	}

	public String getOauthCallback() {
		return oauthCallback;
	}

	public void setOauthCallback(String oauthCallback) {
		this.oauthCallback = oauthCallback;
	}

	public List<String> getFilter() {
		return filter;
	}

	public void setFilter(List<String> filter) {
		this.filter = filter;
	}

	
}
