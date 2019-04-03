package org.springframework.social.wechat.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;

import org.springframework.social.wechat.api.Wechat;

/**
 * spring-social-wechat
 * 
 * @author <a href="mailto:larry7696@gmail.com">Larry</a>
 * @version 18.6.27
 */
public class WechatConnectionFactory extends OAuth2ConnectionFactory<Wechat> {

	public WechatConnectionFactory(String appId, String appSecret) {
		this(appId, appSecret, new WechatAdapter<Wechat>());
	}

	public WechatConnectionFactory(String appId, String appSecret, ApiAdapter<Wechat> apiAdapter) {
		super("wechat", new WechatServiceProvider<Wechat>(appId, appSecret), apiAdapter);
	}

	@Override
	protected String extractProviderUserId(AccessGrant accessGrant) {
		return ((WechatAccessGrant) accessGrant).getOpenid();
	}

}
