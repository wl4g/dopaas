package com.wl4g.devops.iam.filter;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.authc.SinaAuthenticationToken;

@IamFilter
public class SinaAuthenticationFilter extends Oauth2SnsAuthenticationFilter<SinaAuthenticationToken> {

	public SinaAuthenticationFilter(IamContextManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return ProviderSupports.SINA.getName();
	}

	@Override
	public String getUriMapping() {
		return URI_BASE_MAPPING + ProviderSupports.SINA.getName();
	}

}
