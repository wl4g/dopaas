package com.wl4g.devops.iam.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.util.WebUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.authc.GeneralAuthenticationToken;

@IamFilter
public class GeneralAuthenticationFilter extends AbstractIamAuthenticationFilter<GeneralAuthenticationToken> {

	final public static String NAME = "general";

	public GeneralAuthenticationFilter(IamContextManager manager) {
		super(manager);
	}

	@Override
	protected GeneralAuthenticationToken createAuthenticationToken(String fromAppName, String redirectUrl,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!RequestMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(request.getMethod(), RequestMethod.POST.name());
		}

		String username = WebUtils.getCleanParam(request, config.getParam().getPrincipalName());
		/*
		 * The front end IAM JS SDK submits encrypted hexadecimal strings.
		 */
		String password = WebUtils.getCleanParam(request, config.getParam().getPasswordName());
		String clientRef = WebUtils.getCleanParam(request, config.getParam().getClientRefName());
		String captcha = WebUtils.getCleanParam(request, config.getParam().getCaptchaName());

		return new GeneralAuthenticationToken(fromAppName, redirectUrl, username, password, clientRef, captcha);
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
