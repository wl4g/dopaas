package com.wl4g.devops.iam.sns.support;

import java.io.Serializable;

public interface Oauth2AccessToken extends Serializable {

	String accessToken();

	<T extends Oauth2AccessToken> T build(String message);

}
