package com.wl4g.devops.iam.filter;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.authc.WechatAuthenticationToken;

@IamFilter
public class WechatAuthenticationFilter extends Oauth2SnsAuthenticationFilter<WechatAuthenticationToken> {

	public WechatAuthenticationFilter(IamContextManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return ProviderSupports.WECHAT.getName();
	}

	@Override
	public String getUriMapping() {
		return URI_BASE_MAPPING + ProviderSupports.WECHAT.getName();
	}

	@Override
	protected boolean enabled() {
		return true;
	}

}
