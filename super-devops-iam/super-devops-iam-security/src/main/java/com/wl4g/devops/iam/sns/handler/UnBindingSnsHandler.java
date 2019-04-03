
package com.wl4g.devops.iam.sns.handler;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_ERR_SESSION_SAVED;

import com.wl4g.devops.common.bean.iam.SocialConnectInfo;
import com.wl4g.devops.common.utils.Exceptions;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
import com.wl4g.devops.iam.common.context.SecurityInterceptor;
import com.wl4g.devops.iam.common.utils.SessionBindings;
import com.wl4g.devops.iam.config.IamProperties;
import com.wl4g.devops.iam.config.SnsProperties;
import com.wl4g.devops.iam.context.ServerSecurityContext;
import com.wl4g.devops.iam.sns.SocialConnectionFactory;

/**
 * UnBinding SNS handler
 * 
 * @author wangl.sir
 * @version v1.0 2019年2月28日
 * @since
 */
public class UnBindingSnsHandler extends BasedBindSnsHandler {

	public UnBindingSnsHandler(IamProperties config, SnsProperties snsConfig, SocialConnectionFactory connectFactory,
			ServerSecurityContext contextHandler, SecurityInterceptor intercept, JedisCacheManager cacheManager) {
		super(config, snsConfig, connectFactory, contextHandler, intercept, cacheManager);
	}

	@Override
	protected void postBindingProcess(SocialConnectInfo info) {
		try {
			this.context.unbindSocialConnection(info);
		} catch (Throwable e) {
			log.warn("SNS binding processing error", e);
			// Save error to session
			SessionBindings.bind(KEY_ERR_SESSION_SAVED, Exceptions.getRootCauses(e).getMessage());
		}
	}

	@Override
	public Which whichType() {
		return Which.UNBIND;
	}

}
