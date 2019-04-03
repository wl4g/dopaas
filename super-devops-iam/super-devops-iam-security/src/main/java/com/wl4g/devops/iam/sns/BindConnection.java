package com.wl4g.devops.iam.sns;

import java.util.Map;

import com.wl4g.devops.iam.sns.support.Oauth2AccessToken;
import com.wl4g.devops.iam.sns.support.Oauth2OpenId;
import com.wl4g.devops.iam.sns.support.Oauth2UserProfile;

public interface BindConnection<T extends Oauth2AccessToken, O extends Oauth2OpenId, U extends Oauth2UserProfile> {

	String providerId();

	String getAuthorizeCodeUrl(String state, Map<String, String> queryParams);

	T getAccessToken(String code);

	O getUserOpenId(T accessToken);

	U getUserInfo(String accessToken, String openId);

}
