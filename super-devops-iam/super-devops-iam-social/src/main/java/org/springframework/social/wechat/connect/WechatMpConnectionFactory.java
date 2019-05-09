package org.springframework.social.wechat.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;

import org.springframework.social.wechat.UrlConstants;
import org.springframework.social.wechat.api.WechatMp;

/**
 * spring-social-wechat
 * 
 * @author <a href="mailto:larry7696@gmail.com">Larry</a>
 * @version 18.7.31
 */
public class WechatMpConnectionFactory extends OAuth2ConnectionFactory<WechatMp> {

	public WechatMpConnectionFactory(String appId, String appSecret) {
		this(appId, appSecret, new WechatAdapter<WechatMp>());
	}

	public WechatMpConnectionFactory(String appId, String appSecret, ApiAdapter<WechatMp> apiAdapter) {
		super("wechatmp", new WechatServiceProvider<WechatMp>(appId, appSecret, UrlConstants.AUTHORIZE_API_URL), apiAdapter);
	}

	@Override
	protected String extractProviderUserId(AccessGrant accessGrant) {
		return ((WechatAccessGrant) accessGrant).getOpenid();
	}

}
