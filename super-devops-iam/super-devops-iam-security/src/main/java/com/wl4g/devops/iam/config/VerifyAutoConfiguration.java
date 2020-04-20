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
package com.wl4g.devops.iam.config;

import com.wl4g.devops.common.config.OptionalPrefixControllerAutoConfiguration;
import com.wl4g.devops.iam.annotation.VerifyAuthController;
import com.wl4g.devops.iam.web.VerifyAuthenticatorEndpoint;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_VERIFY_BASE;

/**
 * IAM authentication(verify) configuration
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月8日
 * @since
 */
@AutoConfigureAfter({ LoginAutoConfiguration.class })
public class VerifyAutoConfiguration extends OptionalPrefixControllerAutoConfiguration {

	@Bean
	public VerifyAuthenticatorEndpoint verifyAuthenticatorController() {
		return new VerifyAuthenticatorEndpoint();
	}

	@Bean
	public PrefixHandlerMapping verifyAuthenticatorControllerPrefixHandlerMapping() {
		return super.newPrefixHandlerMapping(URI_S_VERIFY_BASE, VerifyAuthController.class);
	}

}