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
package com.wl4g.dopaas.umc.client.health.timeout;

import static com.wl4g.infra.common.log.SmartLoggerFactory.getLogger;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.infra.common.log.SmartLogger;

/**
 * {@link TimeoutMethodAutoConfiguration}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-11-30 v1.0.0
 * @since v1.0.0
 */
@Configuration
@ConditionalOnBean(TimeoutMethodProperties.class)
@AutoConfigureBefore({ TimeoutMethodProperties.class })
public class TimeoutMethodAutoConfiguration {
    private final SmartLogger log = getLogger(getClass());

    @Bean
    public HealthIndicator defaultTimeoutMethodHealthIndicator(TimeoutMethodProperties config) {
        log.info("Initializing timingMethodsHealthIndicator. - {}", config);
        if (config.getSamples() == 0) {
            throw new IllegalArgumentException("Latest measure count is 0.");
        }
        return new TimeoutMethodHealthIndicator(config);
    }

}
