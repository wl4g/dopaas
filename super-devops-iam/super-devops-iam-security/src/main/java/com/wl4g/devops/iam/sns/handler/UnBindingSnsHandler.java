/*
 * Copyright 2017 ~ 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.iam.sns.handler;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_ERR_SESSION_SAVED;

import com.wl4g.devops.common.bean.iam.SocialConnectInfo;
import com.wl4g.devops.common.utils.Exceptions;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
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
			ServerSecurityContext context) {
		super(config, snsConfig, connectFactory, context);
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