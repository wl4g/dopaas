package org.springframework.social.qq.api.impl;

import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;
import org.springframework.social.qq.api.QQ;
import org.springframework.social.qq.api.UserOperations;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.social.support.URIBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author renq
 */
public class QQTemplate extends AbstractOAuth2ApiBinding implements QQ {

	private final String appId;
	private final String accessToken;
	private final String openId;
	private UserOperations userOperations;
	private static final String URI_ME = "https://graph.qq.com/oauth2.0/me";

	private static Pattern PATTERN_WITH_OPENID = Pattern.compile("\"openid\"\\s*:\\s*\"(\\w+)\"");

	public QQTemplate(String appId, String accessToken) {
		super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
		this.appId = appId;
		this.accessToken = accessToken;
		this.openId = this.getOpenId();
		this.initialize();
	}

	private void initialize() {
		super.setRequestFactory(ClientHttpRequestFactorySelector.bufferRequests(this.getRestTemplate().getRequestFactory()));
		this.initSubApis();
	}

	private void initSubApis() {
		this.userOperations = new UserTemplate(this, getRestTemplate(), isAuthorized(), appId, openId, accessToken);
	}

	private String getOpenId() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.set("access_token", accessToken);
		String openIdJson = this.getRestTemplate().getForObject(URIBuilder.fromUri(URI_ME).queryParams(params).build(),
				String.class);
		Matcher matcher = PATTERN_WITH_OPENID.matcher(openIdJson);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	@Override
	public UserOperations userOperations() {
		return userOperations;
	}
}
