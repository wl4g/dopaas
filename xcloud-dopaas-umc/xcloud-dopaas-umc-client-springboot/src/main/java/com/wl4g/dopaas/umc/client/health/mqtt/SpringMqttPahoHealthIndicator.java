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
import static com.wl4g.component.common.reflect.ReflectionUtils2.findFieldNullable;
import static com.wl4g.component.common.reflect.ReflectionUtils2.getField;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;

import com.wl4g.component.common.lang.Assert2;
import com.wl4g.component.common.log.SmartLogger;
import com.wl4g.component.core.utils.context.SpringContextHolder;
import com.wl4g.dopaas.umc.client.health.util.HealthUtil;

/**
 * Spring integration with PAHO client health indicator.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-11-19 v1.0.0
 * @since v1.0.0
 */
public class SpringMqttPahoHealthIndicator extends AbstractHealthIndicator {
    private final SmartLogger log = getLogger(getClass());

    @Override
    protected void doHealthCheck(Builder builder) throws Exception {
        try {
            Map<String, MqttPahoMessageHandler> beans = SpringContextHolder.getBeans(MqttPahoMessageHandler.class);
            List<Entry<String, MqttPahoMessageHandler>> unhealthys = safeMap(beans).entrySet().stream().filter(e -> {
                Object mqttClient = getField(MQTTPAHOMESSAGEHANDLER_CLIENTFIELD, e.getValue(), true);
                return !((IMqttAsyncClient) mqttClient).isConnected();
            }).collect(toList());

            if (unhealthys.isEmpty()) {
                HealthUtil.up(builder, "Healthy");
            } else {
                List<String> clientIds = unhealthys.stream().map(e -> e.getValue().getClientId()).collect(toList());
                HealthUtil.down(builder, "UnHealthy, for spring.mqtt.paho.clientIds ".concat(clientIds.toString()));
            }
        } catch (Exception ex) {
            HealthUtil.down(builder, "UnHealthy", ex);
            log.error("Failed to detected spring.mqtt.paho.client", ex);
        }
    }

    @Configuration
    @ConditionalOnClass(MqttPahoMessageHandler.class)
    @ConditionalOnBean(MqttPahoMessageHandler.class)
    public static class SpringMqttPahoHealthIndicatorAutoConfiguration {
        private final SmartLogger log = getLogger(getClass());

        @Bean
        public HealthIndicator springMqttPahoHealthIndicator() {
            log.info("Initializing springMqttPahoHealthIndicator. - {}");
            Assert2.notNull(MQTTPAHOMESSAGEHANDLER_CLIENTFIELD, "Load MqttPahoMessageHandler#client field should not be null");
            return new SpringMqttPahoHealthIndicator();
        }
    }

    private static final Field MQTTPAHOMESSAGEHANDLER_CLIENTFIELD = findFieldNullable(MqttPahoMessageHandler.class, "client",
            IMqttAsyncClient.class);

}