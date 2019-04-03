package com.wl4g.devops.iam.filter;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.authc.QQAuthenticationToken;

@IamFilter
public class QQAuthenticationFilter extends Oauth2SnsAuthenticationFilter<QQAuthenticationToken> {

	public QQAuthenticationFilter(IamContextManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return ProviderSupports.QQ.getName();
	}

	@Override
	public String getUriMapping() {
		return URI_BASE_MAPPING + ProviderSupports.QQ.getName();
	}

	@Override
	public boolean enabled() {
		return true;
	}

}
