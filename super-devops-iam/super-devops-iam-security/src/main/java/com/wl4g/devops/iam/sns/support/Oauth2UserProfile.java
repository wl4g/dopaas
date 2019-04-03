package com.wl4g.devops.iam.sns.support;

import java.io.Serializable;

public interface Oauth2UserProfile extends Serializable {

	<U extends Oauth2UserProfile> U build(String message);

}
