/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.bind;
import static com.wl4g.devops.tool.common.lang.Exceptions.getRootCauses;

import com.wl4g.devops.common.bean.iam.SocialConnectInfo;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.Which;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.config.properties.SnsProperties;
import com.wl4g.devops.iam.configure.ServerSecurityConfigurer;
import com.wl4g.devops.iam.sns.OAuth2ApiBindingFactory;

/**
 * UnBinding SNS handler
 *
 * @author wangl.sir
 * @version v1.0 2019年2月28日
 * @since
 */
public class UnBindingSnsHandler extends BasedBindSnsHandler {

	public UnBindingSnsHandler(IamProperties config, SnsProperties snsConfig, OAuth2ApiBindingFactory connectFactory,
			ServerSecurityConfigurer context) {
		super(config, snsConfig, connectFactory, context);
	}

	@Override
	protected void postBindingProcess(SocialConnectInfo info) {
		try {
			configurer.unbindSocialConnection(info);
		} catch (Throwable e) {
			log.warn("Failed to unbinding sns.", e);
			// Save error to session
			bind(KEY_ERR_SESSION_SAVED, getRootCauses(e).getMessage());
		}
	}

	@Override
	public Which which() {
		return Which.UNBIND;
	}

}