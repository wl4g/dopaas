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

import static com.wl4g.component.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import com.wl4g.component.common.lang.SystemUtils2;
import com.wl4g.component.common.lang.ThreadUtils2;
import com.wl4g.component.core.utils.context.SpringContextHolder;

/**
 * {@link SpringMqttv3AutoConfiguration}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2021-11-19 v1.0.0
 * @since v1.0.0
 */
@Configuration
public class SpringMqttv3AutoConfiguration implements ApplicationRunner {

    public static final String MQTTV3_INBOUND_CHANNEL = "mqttv3InboundChannel";
    public static final String MQTTV3_OUTBOUND_CHANNEL = "mqttv3OutboundChannel";

    @Bean
    public MqttConnectOptions mqttv3ConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("admin");
        options.setPassword("public".toCharArray());
        options.setServerURIs(new String[] { "tcp://10.0.0.163:1883" });
        options.setAutomaticReconnect(true);
        options.setKeepAliveInterval(3);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(3);
        return options;
    }

    // [Note]: MQTT server must enabled mqtt.v3, otherwise the connect and
    // subscription will fail.

    @Bean
    public MqttPahoClientFactory mqttv3PahoClientFactory(MqttConnectOptions options) {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(options);
        return factory;
    }

    // --- Consumer. ---

    @Bean(name = MQTTV3_INBOUND_CHANNEL)
    public MessageChannel mqttv3InboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer mqttv3Inbound(MqttConnectOptions options, MqttPahoClientFactory factory) {
        String clientId = "mqttv3.consumer." + SystemUtils2.GLOBAL_PROCESS_SERIAL;
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId, factory,
                "mynamespace/topic1/#");
        adapter.setCompletionTimeout(options.getConnectionTimeout());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(0);
        adapter.setCompletionTimeout(10000);
        adapter.setOutputChannel(mqttv3InboundChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = MQTTV3_INBOUND_CHANNEL)
    public MessageHandler mqttv3InputHandler() {
        return msg -> System.out.println(format("Subscribed message: %s", toJSONString(msg)));
    }

    // --- Producer. ---

    @Bean(name = MQTTV3_OUTBOUND_CHANNEL)
    public MessageChannel mqttv3OutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = MQTTV3_OUTBOUND_CHANNEL)
    public MessageHandler mqttv3OutboundHandler(MqttConnectOptions options, MqttPahoClientFactory factory) {
        String clientId = "mqttv3.producer." + SystemUtils2.GLOBAL_PROCESS_SERIAL;
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(clientId, factory);
        handler.setDefaultQos(0);
        handler.setAsync(true);
        handler.setDefaultTopic("mynamespace/topic1/");
        return handler;
    }

    @Component
    @MessagingGateway(defaultRequestChannel = MQTTV3_OUTBOUND_CHANNEL)
    public static interface SimpleSenderGateway {
        void send(String payload);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Senting testdata to mqtt brokers ...");
                    SpringContextHolder.getBean(SimpleSenderGateway.class).send("abcdefgh123112");
                    ThreadUtils2.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

}
