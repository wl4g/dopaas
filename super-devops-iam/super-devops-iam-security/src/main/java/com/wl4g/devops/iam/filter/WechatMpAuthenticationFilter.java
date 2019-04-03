package com.wl4g.devops.iam.filter;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.authc.WechatMpAuthenticationToken;

@IamFilter
public class WechatMpAuthenticationFilter extends Oauth2SnsAuthenticationFilter<WechatMpAuthenticationToken> {

	public WechatMpAuthenticationFilter(IamContextManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return ProviderSupports.WECHATMP.getName();
	}

	@Override
	public String getUriMapping() {
		return URI_BASE_MAPPING + ProviderSupports.WECHATMP.getName();
	}

	@Override
	protected boolean enabled() {
		return true;
	}

}
