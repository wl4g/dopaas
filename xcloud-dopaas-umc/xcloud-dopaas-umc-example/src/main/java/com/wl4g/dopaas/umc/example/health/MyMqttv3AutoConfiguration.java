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
package com.wl4g.dopaas.umc.example.health;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.component.common.lang.SystemUtils2;

/**
 * {@link MyMqttv3AutoConfiguration}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-11-19 v1.0.0
 * @since v1.0.0
 */
@Configuration
public class MyMqttv3AutoConfiguration {

    @Bean
    public MqttClient consumerMqttClient() {
        try {
            MqttClient client = new MqttClient("tcp://10.0.0.163:1883", "mqtt.consumer." + SystemUtils2.GLOBAL_PROCESS_SERIAL);
            // If the connect() is not called here, the health check is down.
            client.connect();
            return client;
        } catch (MqttException e) {
            throw new IllegalStateException(e);
        }
    }

}
