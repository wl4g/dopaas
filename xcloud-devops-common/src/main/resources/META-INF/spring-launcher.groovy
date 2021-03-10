/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
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
 *
 * Reference to website: http://wl4g.com
 */

import static com.wl4g.component.common.lang.ClassUtils2.isPresent
import static org.springframework.boot.context.config.ConfigFileApplicationListener.*
import com.wl4g.component.core.boot.listener.ISpringLauncherConfigurer

/**
 * IAM web implementation of {@link ISpringLauncherConfigurer}
 */
class DevOpsSpringLauncherConfigurer implements ISpringLauncherConfigurer {

	@Override
	def int getOrder() {
		return -100
	}

	@Override
	def Properties defaultProperties() {
		def defaultProperties = new Properties()
		// Preset spring.config.name
		// for example: spring auto load for 'application-dev.yml/application-data-dev.yml'
		def configName = new StringBuffer("application")
		configName.append(",default")
		configName.append(",ci-web")
		configName.append(",ci-facade")
		configName.append(",ci-analyzer")
		configName.append(",doc-web")
		configName.append(",doc-facade")
		configName.append(",dts-web")
		configName.append(",dts-facade")
		configName.append(",erm-web")
		configName.append(",erm-facade")
		configName.append(",esm-web")
		configName.append(",esm-facade")
		configName.append(",scm-web")
		configName.append(",scm-facade")
		configName.append(",umc-web")
		configName.append(",umc-facade")
		configName.append(",umc-receiver")
		configName.append(",vcs-web")
		configName.append(",vcs-facade")

		// Preset spring.config.location
		// for example: spring auto load for 'classpath:/application-web-dev.yml'
		def location = new StringBuffer("classpath:/")
		def archConfigSuffix = ""
		if (isPresent("org.springframework.cloud.openfeign.FeignClient") && isPresent("org.springframework.cloud.openfeign.FeignAutoConfiguration")) {
			location.append(",classpath:/scf/")
			archConfigSuffix = "scf"
		} else if (isPresent("com.wl4g.component.rpc.springboot.feign.annotation.SpringBootFeignClient")) {
			location.append(",classpath:/sbf/")
			archConfigSuffix = "sbf"
		}
		configName.append(",default-devops-" + archConfigSuffix)
		//configName.append(",default-devops-web-" + archConfigSuffix)
		//configName.append(",default-devops-facade-" + archConfigSuffix)
		configName.append(",ci-web-" + archConfigSuffix)
		configName.append(",ci-facade-" + archConfigSuffix)
		configName.append(",doc-web-" + archConfigSuffix)
		configName.append(",doc-facade-" + archConfigSuffix)
		configName.append(",dts-web-" + archConfigSuffix)
		configName.append(",dts-facade-" + archConfigSuffix)
		configName.append(",erm-web-" + archConfigSuffix)
		configName.append(",erm-facade-" + archConfigSuffix)
		configName.append(",esm-web-" + archConfigSuffix)
		configName.append(",esm-facade-" + archConfigSuffix)
		configName.append(",scm-web-" + archConfigSuffix)
		configName.append(",scm-facade-" + archConfigSuffix)
		configName.append(",umc-web-" + archConfigSuffix)
		configName.append(",umc-facade-" + archConfigSuffix)
		configName.append(",umc-receiver-" + archConfigSuffix)
		configName.append(",vcs-web-" + archConfigSuffix)
		configName.append(",vcs-facade-" + archConfigSuffix)

		defaultProperties.put(CONFIG_NAME_PROPERTY, configName.toString())
		defaultProperties.put(CONFIG_ADDITIONAL_LOCATION_PROPERTY, location.toString())

		return defaultProperties
	}

}