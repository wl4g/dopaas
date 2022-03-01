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

import static com.wl4g.dopaas.ucm.common.UCMConstants.URI_C_BASE;

import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.wl4g.iam.client.config.IamClientProperties;

/**
 * {@link IamWithUcmClientAutoConfiguration}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月26日
 * @since
 */
@ConditionalOnClass(IamClientProperties.class)
public class IamWithUcmClientAutoConfiguration {

    @Bean
    @Primary
    public IamClientProperties iamWithUcmClientProperties() {
        return new IamWithUcmClientProperties();
    }

    /**
     * {@link IamWithUcmClientProperties}
     *
     * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
     * @version v1.0 2020年5月25日
     * @since
     */
    public static class IamWithUcmClientProperties extends IamClientProperties {

        private static final long serialVersionUID = -2654363585569068709L;

        @Override
        protected void applyRequiresFilterChains(Map<String, String> chains) {
            chains.put(URI_C_BASE + "/**", "anon");
        }

    }

}