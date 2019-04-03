package com.wl4g.devops.iam.sns.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;

import static com.wl4g.devops.common.web.BaseController.REDIRECT_PREFIX;

import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
import com.wl4g.devops.iam.common.context.SecurityInterceptor;
import com.wl4g.devops.iam.config.IamProperties;
import com.wl4g.devops.iam.config.SnsProperties;
import com.wl4g.devops.iam.context.ServerSecurityContext;
import com.wl4g.devops.iam.sns.SocialConnectionFactory;

/**
 * Login SNS handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年2月24日
 * @since
 */
public class LoginSnsHandler extends AbstractSnsHandler {

	public LoginSnsHandler(IamProperties config, SnsProperties snsConfig, SocialConnectionFactory connectFactory,
			ServerSecurityContext contextHandler, SecurityInterceptor intercept, JedisCacheManager cacheManager) {
		super(config, snsConfig, connectFactory, contextHandler, intercept, cacheManager);
	}

	@Override
	public String connect(Which which, String provider, String state, Map<String, String> connectParams) {
		// Connecting
		String authorizingUrl = super.connect(which, provider, state, connectParams);

		// Save connect parameters
		this.saveOauth2ConnectParameters(provider, state, connectParams);

		return REDIRECT_PREFIX + authorizingUrl;
	}

	@Override
	protected void checkConnectRequireds(String provider, String state, Map<String, String> connectParams) {
		super.checkConnectRequireds(provider, state, connectParams);

		// Check connect parameters
		Assert.notEmpty(connectParams, "Connect parameters must not be empty");

		// PC-side browsers use agent redirection(QQ,sina)
		Assert.hasText(connectParams.get(config.getParam().getAgent()),
				String.format("'%s' must not be empty", config.getParam().getAgent()));
	}

	@Override
	protected void checkConnectCallbacks(String provider, String state, String code, Map<String, String> connectParams) {
		// Check 'state'
		Assert.notEmpty(connectParams, String.format("State '%s' is invalid or expired", state));
		super.checkConnectCallbacks(provider, state, code, connectParams);
	}

	@Override
	protected String buildResponseMessage(String provider, String callbackId, Map<String, String> connectParams,
			HttpServletRequest request) {
		return super.getLoginSubmissionUrl(provider, callbackId, request);
	}

	@Override
	public Which whichType() {
		return Which.LOGIN;
	}

}
