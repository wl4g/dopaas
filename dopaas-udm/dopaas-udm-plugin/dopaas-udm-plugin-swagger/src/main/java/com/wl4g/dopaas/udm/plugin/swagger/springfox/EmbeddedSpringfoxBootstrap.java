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
package com.wl4g.dopaas.udm.plugin.swagger.springfox;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.wl4g.component.core.boot.BootstrappingAutoConfiguration;
import com.wl4g.component.core.web.error.reactive.ReactiveErrorAutoConfiguration;
import com.wl4g.component.core.web.error.servlet.ServletErrorAutoConfiguration;
import com.wl4g.component.core.web.mapping.annotation.WebFluxSmartHandlerMappingConfigurer;
import com.wl4g.component.core.web.mapping.annotation.WebMvcSmartHandlerMappingConfigurer;
import com.wl4g.component.core.web.versions.annotation.EnableApiVersionManagement;
import com.wl4g.dopaas.udm.plugin.swagger.DocumentionAutoConfigurationRegistrar;
import com.wl4g.dopaas.udm.plugin.swagger.EnableDocumentionAutoConfiguration;

/**
 * {@link EmbeddedSpringfoxBootstrap} </br>
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-10
 * @sine v1.0
 * @see {@link DocumentionAutoConfigurationRegistrar}
 */
@EnableDocumentionAutoConfiguration
@EnableApiVersionManagement // Supported multi version api docs
@SpringBootApplication(scanBasePackages = "none", scanBasePackageClasses = { WebMvcSmartHandlerMappingConfigurer.class,
        WebFluxSmartHandlerMappingConfigurer.class }, exclude = { BootstrappingAutoConfiguration.class,
                ReactiveErrorAutoConfiguration.class, ServletErrorAutoConfiguration.class })
public class EmbeddedSpringfoxBootstrap {

    public static final int EMBEDDED_PORT = RandomUtils.nextInt(55535, 65535);

}
