package com.wl4g.devops.iam.filter;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.authc.GithubAuthenticationToken;

@IamFilter
public class GithubAuthenticationFilter extends Oauth2SnsAuthenticationFilter<GithubAuthenticationToken> {

	public GithubAuthenticationFilter(IamContextManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return ProviderSupports.GITHUB.getName();
	}

	@Override
	public String getUriMapping() {
		return URI_BASE_MAPPING + ProviderSupports.GITHUB.getName();
	}

}
