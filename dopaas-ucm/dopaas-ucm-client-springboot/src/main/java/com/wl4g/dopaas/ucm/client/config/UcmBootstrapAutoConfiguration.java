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
package com.wl4g.dopaas.ucm.client.config;

import com.wl4g.dopaas.ucm.client.internal.UcmClient;
import com.wl4g.dopaas.ucm.client.internal.UcmClientBuilder;
import com.wl4g.dopaas.ucm.client.locator.BootstrapUcmPropertySourceLocator;
import com.wl4g.dopaas.ucm.client.refresh.SpringRefreshConfigEventListener;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * UCM bootstrap configuration.</br>
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
public class UcmBootstrapAutoConfiguration {

    @ConfigurationProperties(prefix = "spring.cloud.devops.ucm.client")
    @Bean
    public UcmClientProperties<?> ucmClientProperties() {
        return new UcmClientProperties<>();
    }

    @Bean
    public SpringRefreshConfigEventListener springRefreshConfigEventListener() {
        return new SpringRefreshConfigEventListener();
    }

    @Bean
    public UcmClient defaultUcmClient(UcmClientProperties<?> config, SpringRefreshConfigEventListener listener) {
        UcmClient client = UcmClientBuilder.newBuilder()
                .withConfiguration(config)
                .enableManagementConsole()
                .withListeners(listener)
                .build();
        return client;
    }

    @Bean
    public BootstrapUcmPropertySourceLocator bootstrapUcmPropertySourceLocator(UcmClient client) {
        return new BootstrapUcmPropertySourceLocator(client);
    }

}