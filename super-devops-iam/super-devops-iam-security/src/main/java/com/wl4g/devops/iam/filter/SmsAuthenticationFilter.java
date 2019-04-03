package com.wl4g.devops.iam.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.authc.SmsAuthenticationToken;

@IamFilter
public class SmsAuthenticationFilter extends AbstractIamAuthenticationFilter<SmsAuthenticationToken> {
	final public static String NAME = "sms";

	public SmsAuthenticationFilter(IamContextManager manager) {
		super(manager);
	}

	@Override
	protected SmsAuthenticationToken createAuthenticationToken(String fromAppName, String redirectUrl, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		return null;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getUriMapping() {
		return URI_BASE_MAPPING + NAME;
	}

}
