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
		return -200
	}

	@Override
	def Properties defaultProperties() {
		def defaultProperties = new Properties()
		// Preset spring.config.name
		// for example: spring auto load for 'application-dev.yml/application-data-dev.yml'
		def configName = new StringBuffer("application")
		// Standalone
		configName.append(",standalone-dopaas")
		configName.append(",common-dopaas")
		// HOME
		configName.append(",home-facade")
		configName.append(",home-manager")
		// UCI
		configName.append(",uci-facade")
		configName.append(",uci-server")
		configName.append(",uci-analyzer")
		// UDM
		configName.append(",udm-facade")
		configName.append(",udm-manager")
		// LCDP
		configName.append(",lcdp-facade")
		configName.append(",lcdp-manager")
		// UDS
		configName.append(",uds-facade")
		configName.append(",uds-manager")
		// CMDB
		configName.append(",cmdb-facade")
		configName.append(",cmdb-manager")
		// ESM
		//configName.append(",esm-facade")
		//configName.append(",esm-manager")
		// UCM
		configName.append(",ucm-facade")
		configName.append(",ucm-server")
		// UMC
		configName.append(",umc-facade")
		configName.append(",umc-manager")
		configName.append(",umc-collector")
		configName.append(",umc-tracker")
		// URM
		configName.append(",urm-facade")
		configName.append(",urm-manager")
		// UOS
		configName.append(",uos-facade")
		configName.append(",uos-manager")

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
		// HOME
		configName.append(",home-facade-" + archConfigSuffix)
		configName.append(",home-manager-" + archConfigSuffix)
		// UCI
		configName.append(",uci-facade-" + archConfigSuffix)
		configName.append(",uci-server-" + archConfigSuffix)
		// UDM
		configName.append(",udm-facade-" + archConfigSuffix)
		configName.append(",udm-manager-" + archConfigSuffix)
		// LCDP
		configName.append(",lcdp-facade-" + archConfigSuffix)
		configName.append(",lcdp-manager-" + archConfigSuffix)
		// UDS
		configName.append(",uds-facade-" + archConfigSuffix)
		configName.append(",uds-manager-" + archConfigSuffix)
		// CMDB
		configName.append(",cmdb-facade-" + archConfigSuffix)
		configName.append(",cmdb-manager-" + archConfigSuffix)
		// ESM
		//configName.append(",esm-facade-" + archConfigSuffix)
		//configName.append(",esm-manager-" + archConfigSuffix)
		// UCM
		configName.append(",ucm-facade-" + archConfigSuffix)
		configName.append(",ucm-server-" + archConfigSuffix)
		// UMC
		configName.append(",umc-facade-" + archConfigSuffix)
		configName.append(",umc-manager-" + archConfigSuffix)
		configName.append(",umc-collector-" + archConfigSuffix)
		configName.append(",umc-tracker-" + archConfigSuffix)
		// URM
		configName.append(",urm-facade-" + archConfigSuffix)
		configName.append(",urm-manager-" + archConfigSuffix)
		// UOS
		configName.append(",uos-facade-" + archConfigSuffix)
		configName.append(",uos-manager-" + archConfigSuffix)

		defaultProperties.put(CONFIG_NAME_PROPERTY, configName.toString())
		defaultProperties.put(CONFIG_ADDITIONAL_LOCATION_PROPERTY, location.toString())

		return defaultProperties
	}

}