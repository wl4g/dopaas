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
package com.wl4g.dopaas.umc.client.health.mqtt;

import static com.wl4g.component.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.component.common.log.SmartLoggerFactory.getLogger;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.component.core.utils.context.SpringContextHolder;

/**
 * Eclipse PAHO client(v3) health indicator.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-11-19 v1.0.0
 * @since v1.0.0
 */
public class PahoMqttv3HealthIndicator extends AbstractHealthIndicator {
    private final SmartLogger log = getLogger(getClass());

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        try {
            Map<String, MqttClient> beans = SpringContextHolder.getBeans(MqttClient.class);
            List<Entry<String, MqttClient>> unhealthys = safeMap(beans).entrySet().stream()
                    .filter(e -> !e.getValue().isConnected()).collect(toList());

            if (unhealthys.isEmpty()) {
                builder.up().withDetail("desc", "Healthy");
            } else {
                List<String> clientIds = unhealthys.stream().map(e -> e.getValue().getClientId()).collect(toList());
                builder.down().withDetail("mqttv3.clientIds", clientIds.toString()).withDetail("desc", "UnHealthy");
            }
        } catch (Exception ex) {
            builder.down().withDetail("desc", "UnHealthy");
            log.error("Failed to detected paho.mqttv3.client", ex);
        }
    }

    @Configuration
    @ConditionalOnClass(org.eclipse.paho.client.mqttv3.MqttClient.class)
    @ConditionalOnBean(org.eclipse.paho.client.mqttv3.MqttClient.class)
    public static class PahoMqttv3HealthIndicatorAutoConfiguration {
        private final SmartLogger log = getLogger(getClass());

        @Bean
        public HealthIndicator pahoMqttv3HealthIndicator() {
            log.info("Initializing pahoMqttHealthIndicator. - {}");
            return new PahoMqttv3HealthIndicator();
        }

    }

}