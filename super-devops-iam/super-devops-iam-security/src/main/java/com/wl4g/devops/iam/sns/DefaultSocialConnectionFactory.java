package com.wl4g.devops.iam.sns;

import org.apache.shiro.util.Assert;

import com.wl4g.devops.iam.sns.support.Oauth2AccessToken;
import com.wl4g.devops.iam.sns.support.Oauth2OpenId;
import com.wl4g.devops.iam.sns.support.Oauth2UserProfile;

/**
 * Default IAM Social connection factory
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年1月4日
 * @since
 */
public class DefaultSocialConnectionFactory implements SocialConnectionFactory {

	final private SocialRepository repository;

	public DefaultSocialConnectionFactory(SocialRepository repository) {
		Assert.notNull(repository, "'respository' must not be null");
		this.repository = repository;
	}

	@Override
	public BindConnection<Oauth2AccessToken, Oauth2OpenId, Oauth2UserProfile> getBindConnection(String providerId) {
		return this.repository.getBindConnection(providerId);
	}

}
