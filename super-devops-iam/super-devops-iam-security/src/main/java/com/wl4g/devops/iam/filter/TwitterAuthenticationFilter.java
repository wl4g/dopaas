package com.wl4g.devops.iam.filter;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.authc.TwitterAuthenticationToken;

@IamFilter
public class TwitterAuthenticationFilter extends Oauth2SnsAuthenticationFilter<TwitterAuthenticationToken> {

	public TwitterAuthenticationFilter(IamContextManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return ProviderSupports.TWITTER.getName();
	}

	@Override
	public String getUriMapping() {
		return URI_BASE_MAPPING + ProviderSupports.TWITTER.getName();
	}

}
