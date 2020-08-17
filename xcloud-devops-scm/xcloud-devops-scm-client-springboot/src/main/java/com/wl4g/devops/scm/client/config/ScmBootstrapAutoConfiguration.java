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
package com.wl4g.devops.scm.client.config;

import com.wl4g.devops.scm.client.handler.locator.BootstrapScmPropertySourceLocator;
import com.wl4g.devops.scm.client.handler.locator.ScmPropertySourceLocator;
import com.wl4g.devops.scm.client.utils.NodeHolder;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * SCM bootstrap configuration.</br>
 * Note: Spring Cloud loads bootstrap.yml preferentially, which means that other
 * configurationfiles are not # loaded at initialization, so configurations
 * other than bootstrap.yml cannot be used at initialization.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月8日
 * @since {@link de.codecentric.boot.admin.web.PrefixHandlerMapping}
 *        {@link de.codecentric.boot.admin.config.AdminServerWebConfiguration}}
 */
public class ScmBootstrapAutoConfiguration {

	//
	// SCM foundation's
	//

	@Bean
	public ScmClientProperties scmClientProperties() {
		return new ScmClientProperties();
	}

	@Bean
	public NodeHolder instanceHolder(Environment environment) {
		return new NodeHolder(environment, scmClientProperties());
	}

	@Bean
	public ScmPropertySourceLocator scmPropertySourceLocator(ScmClientProperties config, NodeHolder info) {
		return new BootstrapScmPropertySourceLocator(config, info);
	}

}