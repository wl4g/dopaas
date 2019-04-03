package com.zrk.oauthclient.client;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.client.BaseOAuth20Client;
import org.pac4j.oauth.profile.JsonHelper;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.ProxyOAuth20ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.zrk.oauthclient.api.SinaWeiboApi20;
import com.zrk.oauthclient.definition.SinaWeiboAttributesDefinition;
import com.zrk.oauthclient.profile.SinaWeiboProfile;

/**
 * 微博登录client
 * 
 * @author zrk
 * @date 2016年4月15日 下午5:42:53
 */
public class SinaWeiboClient extends BaseOAuth20Client<SinaWeiboProfile> {

	private final static SinaWeiboAttributesDefinition SINAWEIBO_ATTRIBUTES = new SinaWeiboAttributesDefinition();

	public final static String DEFAULT_SCOPE = "all";

	protected String scope = DEFAULT_SCOPE;

	public SinaWeiboClient() {
	}

	public SinaWeiboClient(final String key, final String secret) {
		setKey(key);
		setSecret(secret);
	}

	@Override
	protected SinaWeiboClient newClient() {
		final SinaWeiboClient newClient = new SinaWeiboClient();
		newClient.setScope(this.scope);
		return newClient;
	}

	@Override
	protected void internalInit(WebContext context) {
		super.internalInit(context);
		SinaWeiboApi20 api20 = new SinaWeiboApi20();
		if (StringUtils.isNotBlank(this.scope)) {
			this.service = new ProxyOAuth20ServiceImpl(api20,
					new OAuthConfig(this.key, this.secret, this.callbackUrl, SignatureType.Header, this.scope, null),
					this.connectTimeout, this.readTimeout, this.proxyHost, this.proxyPort, true, true);
		} else {
			this.service = new ProxyOAuth20ServiceImpl(api20,
					new OAuthConfig(this.key, this.secret, this.callbackUrl, SignatureType.Header, null, null),
					this.connectTimeout, this.readTimeout, this.proxyHost, this.proxyPort, true, true);
		}
	};

	// 获取用户信息的URL
	@Override
	protected String getProfileUrl(Token accessToken) {
		final JsonNode json = JsonHelper.getFirstNode(accessToken.getRawResponse());
		String uid = (String) JsonHelper.get(json, "uid");
		return "https://api.weibo.com/2/users/show.json?uid=" + uid;
	}

	@Override
	protected SinaWeiboProfile extractUserProfile(String body) {
		final SinaWeiboProfile profile = new SinaWeiboProfile();
		logger.info("========= extractUserProfile Method  body:" + body);
		final JsonNode json = JsonHelper.getFirstNode(body);
		if (null != json) {
			for (final String attribute : SINAWEIBO_ATTRIBUTES.getPrincipalAttributes()) {
				profile.addAttribute(attribute, JsonHelper.get(json, attribute).toString());
			}
		}
		return profile;
	}

	// 认证被用户取消
	@Override
	protected boolean hasBeenCancelled(WebContext context) {
		return false;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

}
