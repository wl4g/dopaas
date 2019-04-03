package com.wl4g.devops.iam.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.authc.QrcodeAuthenticationToken;

@IamFilter
public class QrcodeAuthenticationFilter extends AbstractIamAuthenticationFilter<QrcodeAuthenticationToken> {
	final public static String NAME = "qrcode";

	public QrcodeAuthenticationFilter(IamContextManager manager) {
		super(manager);
	}

	@Override
	protected QrcodeAuthenticationToken createAuthenticationToken(String fromAppName, String redirectUrl,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

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
