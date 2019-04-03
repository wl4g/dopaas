package com.wl4g.devops.iam.sns;

import com.wl4g.devops.iam.sns.support.Oauth2AccessToken;
import com.wl4g.devops.iam.sns.support.Oauth2OpenId;
import com.wl4g.devops.iam.sns.support.Oauth2UserProfile;

public interface SocialRepository {

	BindConnection<Oauth2AccessToken, Oauth2OpenId, Oauth2UserProfile> getBindConnection(String providerId);

}
