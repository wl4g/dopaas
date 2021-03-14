/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.wl4g.component.rpc.feign.core.annotation.EnableFeignConsumers;

import zipkin2.server.internal.EnableZipkinServer;
import zipkin2.server.internal.ZipkinActuatorImporter;
import zipkin2.server.internal.ZipkinModuleImporter;
import zipkin2.server.internal.banner.ZipkinBanner;

/**
 * References to {@link zipkin.server.ZipkinServer}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-03-10
 * @sine v1.0
 * @see
 */
@EnableZipkinServer
@EnableFeignConsumers("com.wl4g.dopaas.umc.service")
@SpringBootApplication
public class UmcTracker {

	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}

	public static void main(String[] args) {
		new SpringApplicationBuilder(UmcTracker.class).banner(new ZipkinBanner())
				.initializers(new ZipkinModuleImporter(), new ZipkinActuatorImporter())
				// Modify from zipkin.server.ZipkinServer
				// .logStartupInfo(false)
				// .properties(EnableAutoConfiguration.ENABLED_OVERRIDE_PROPERTY+"=false","spring.config.name=zipkin-server")
				.logStartupInfo(true).properties("spring.config.name=zipkin-server").run(args);
	}

}