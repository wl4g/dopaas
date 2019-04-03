package com.wl4g.devops.iam.client.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.util.WebUtils;

import com.wl4g.devops.common.utils.web.WebUtils2;
import com.wl4g.devops.iam.client.authc.FastCasAuthenticationToken;
import com.wl4g.devops.iam.client.config.IamClientProperties;
import com.wl4g.devops.iam.client.context.ClientSecurityContext;
import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.common.context.SecurityInterceptor;
import com.wl4g.devops.iam.common.context.SecurityListener;

/**
 * This filter validates the CAS service ticket to authenticate the user. It
 * must be configured on the URL recognized by the CAS server. For example, in
 * {@code shiro.ini}:
 * 
 * <pre>
 * [main]
 * casFilter = org.apache.shiro.cas.CasFilter
 * ...
 *
 * [urls]
 * /shiro-cas = casFilter
 * ...
 * </pre>
 * 
 * (example : http://host:port/mycontextpath/shiro-cas)
 *
 * @since 1.2
 */
@IamFilter
public class ROOTAuthenticationFilter extends AbstractAuthenticationFilter<FastCasAuthenticationToken> {
	final public static String NAME = "rootFilter";

	public ROOTAuthenticationFilter(IamClientProperties config, ClientSecurityContext context, SecurityInterceptor interceptor,
			SecurityListener listener, JedisCacheManager cacheManager) {
		super(config, context, interceptor, listener, cacheManager);
	}

	/**
	 * The token created for this authentication is a CasToken containing the
	 * CAS service ticket received on the CAS service url (on which the filter
	 * must be configured).
	 * 
	 * @param request
	 *            the incoming request
	 * @param response
	 *            the outgoing response
	 * @throws Exception
	 *             if there is an error processing the request.
	 */
	@Override
	protected FastCasAuthenticationToken createAuthenticationToken(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String ticket = WebUtils.getCleanParam(request, config.getParam().getGrantTicket());
		return new FastCasAuthenticationToken(ticket);
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		if (log.isDebugEnabled()) {
			String url = WebUtils2.getFullRequestURL(WebUtils.toHttp(request));
			log.debug("Root request: {}", url);
		}

		/*
		 * See:xx.client.filter.AbstractAuthenticationFilter#getRememberUrl()
		 */
		if (WebUtils.toHttp(request).getMethod().equalsIgnoreCase(GET_METHOD)) {
			WebUtils.saveRequest(request);
		}

		return SecurityUtils.getSubject().isAuthenticated();
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getUriMapping() {
		return "/**";
	}

}
