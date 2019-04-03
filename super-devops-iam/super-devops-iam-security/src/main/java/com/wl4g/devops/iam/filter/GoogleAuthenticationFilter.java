package com.wl4g.devops.iam.filter;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.authc.GoogleAuthenticationToken;

@IamFilter
public class GoogleAuthenticationFilter extends Oauth2SnsAuthenticationFilter<GoogleAuthenticationToken> {

	public GoogleAuthenticationFilter(IamContextManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return ProviderSupports.GOOGLE.getName();
	}

	@Override
	public String getUriMapping() {
		return URI_BASE_MAPPING + ProviderSupports.GOOGLE.getName();
	}

}
