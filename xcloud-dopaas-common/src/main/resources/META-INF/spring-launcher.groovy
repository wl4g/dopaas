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
		configName.append(",common-dopaas")
		configName.append(",uci-web")
		configName.append(",uci-facade")
		configName.append(",uci-analyzer")
		configName.append(",udm-web")
		configName.append(",udm-facade")
		configName.append(",udc-web")
		configName.append(",udc-facade")
		configName.append(",uds-web")
		configName.append(",uds-facade")
		configName.append(",cmdb-web")
		configName.append(",cmdb-facade")
		//configName.append(",esm-web")
		//configName.append(",esm-facade")
		configName.append(",ucm-web")
		configName.append(",umc-collector")
		configName.append(",umc-tracker")
		configName.append(",ucm-facade")
		configName.append(",umc-web")
		configName.append(",umc-facade")
		configName.append(",urm-web")
		configName.append(",urm-facade")
		configName.append(",uos-web")
		configName.append(",uos-facade")

		// Preset spring.config.location
		// for example: spring auto load for 'classpath:/application-web-dev.yml'
		def location = new StringBuffer("classpath:/")
		def archConfigSuffix = ""
		if (isPresent("org.springframework.cloud.openfeign.FeignClient") && isPresent("org.springframework.cloud.openfeign.FeignAutoConfiguration")) {
			location.append(",classpath:/scf/")
			archConfigSuffix = "scf"
		} else if (isPresent("com.wl4g.component.rpc.feign.core.annotation.FeignConsumer")) {
			location.append(",classpath:/sbf/")
			archConfigSuffix = "sbf"
		}
		configName.append(",common-dopaas-" + archConfigSuffix)
		//configName.append(",common-dopaas-web-" + archConfigSuffix)
		//configName.append(",common-dopaas-facade-" + archConfigSuffix)
		configName.append(",uci-web-" + archConfigSuffix)
		configName.append(",uci-facade-" + archConfigSuffix)
		configName.append(",udm-web-" + archConfigSuffix)
		configName.append(",udm-facade-" + archConfigSuffix)
		configName.append(",udc-web-" + archConfigSuffix)
		configName.append(",udc-facade-" + archConfigSuffix)
		configName.append(",uds-web-" + archConfigSuffix)
		configName.append(",uds-facade-" + archConfigSuffix)
		configName.append(",cmdb-web-" + archConfigSuffix)
		configName.append(",cmdb-facade-" + archConfigSuffix)
		//configName.append(",esm-web-" + archConfigSuffix)
		//configName.append(",esm-facade-" + archConfigSuffix)
		configName.append(",ucm-web-" + archConfigSuffix)
		configName.append(",umc-collector-" + archConfigSuffix)
		configName.append(",umc-tracker-" + archConfigSuffix)
		configName.append(",ucm-facade-" + archConfigSuffix)
		configName.append(",umc-web-" + archConfigSuffix)
		configName.append(",umc-facade-" + archConfigSuffix)
		configName.append(",urm-web-" + archConfigSuffix)
		configName.append(",urm-facade-" + archConfigSuffix)
		configName.append(",uos-web-" + archConfigSuffix)
		configName.append(",uos-facade-" + archConfigSuffix)

		defaultProperties.put(CONFIG_NAME_PROPERTY, configName.toString())
		defaultProperties.put(CONFIG_ADDITIONAL_LOCATION_PROPERTY, location.toString())

		return defaultProperties
	}

}