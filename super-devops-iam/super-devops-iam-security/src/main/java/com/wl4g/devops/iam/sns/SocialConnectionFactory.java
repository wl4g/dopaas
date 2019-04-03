package com.wl4g.devops.iam.sns;

import com.wl4g.devops.common.exception.iam.NoSuchSocialProviderException;
import com.wl4g.devops.iam.sns.support.Oauth2AccessToken;
import com.wl4g.devops.iam.sns.support.Oauth2OpenId;
import com.wl4g.devops.iam.sns.support.Oauth2UserProfile;

/**
 * IAM Social connection factory
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月4日
 * @since
 */
public interface SocialConnectionFactory {

	BindConnection<Oauth2AccessToken, Oauth2OpenId, Oauth2UserProfile> getBindConnection(String provider)
			throws NoSuchSocialProviderException;

}
