package com.wl4g.devops.iam.filter;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.authc.FacebookAuthenticationToken;

@IamFilter
public class FacebookAuthenticationFilter extends Oauth2SnsAuthenticationFilter<FacebookAuthenticationToken> {

	public FacebookAuthenticationFilter(IamContextManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return ProviderSupports.FACEBOOK.getName();
	}

	@Override
	public String getUriMapping() {
		return URI_BASE_MAPPING + ProviderSupports.FACEBOOK.getName();
	}

}
