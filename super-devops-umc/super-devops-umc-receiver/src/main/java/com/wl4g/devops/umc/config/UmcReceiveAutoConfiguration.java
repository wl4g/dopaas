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
package com.wl4g.devops.umc.config;

import com.wl4g.devops.common.config.OptionalPrefixControllerAutoConfiguration;
import com.wl4g.devops.umc.alarm.alerting.IndicatorsValveAlerter;
import com.wl4g.devops.umc.annotation.EnableHttpCollectReceiver;
import com.wl4g.devops.umc.annotation.EnableKafkaCollectReceiver;
import com.wl4g.devops.umc.console.ReceiveConsole;
import com.wl4g.devops.umc.receiver.HttpMetricReceiver;
import com.wl4g.devops.umc.receiver.KafkaMetricReceiver;
import com.wl4g.devops.umc.store.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer.AckMode;
import org.springframework.kafka.listener.config.ContainerProperties;

import java.util.Map;
import java.util.Properties;

import static com.wl4g.devops.umc.config.UmcAlarmAutoConfiguration.*;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.URI_HTTP_RECEIVER_BASE;
import static com.wl4g.devops.umc.config.ReceiverProperties.KEY_RECEIVER_PREFIX;

/**
 * UMC receiver auto configuration
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
@Configuration
@ImportAutoConfiguration(UmcWatchAutoConfiguration.class)
public class UmcReceiveAutoConfiguration extends OptionalPrefixControllerAutoConfiguration {

	final public static String BEAN_HTTP_RECEIVER = "httpCollectReceiver";
	final public static String BEAN_KAFKA_RECEIVER = "kafkaCollectReceiver";
	final public static String BEAN_KAFKA_BATCH_FACTORY = "kafkaBatchFactory";

	@Bean
	@ConfigurationProperties(prefix = KEY_RECEIVER_PREFIX)
	public ReceiverProperties receiverProperties() {
		return new ReceiverProperties();
	}

	//
	// HTTP receiver.
	//

	@Bean(BEAN_HTTP_RECEIVER)
	@EnableHttpCollectReceiver
	public HttpMetricReceiver httpCollectReceiver(@Qualifier(BEAN_DEFAULT_VALVE_ALERTER) IndicatorsValveAlerter alerter,
			MetricStore store) {
		return new HttpMetricReceiver(alerter, store);
	}

	@Bean
	@EnableHttpCollectReceiver
	public PrefixHandlerMapping httpCollectReceiverPrefixHandlerMapping() {
		return super.newPrefixHandlerMapping(URI_HTTP_RECEIVER_BASE, com.wl4g.devops.umc.annotation.HttpCollectReceiver.class);
	}

	//
	// KAFKA receiver.
	//

	@Bean(BEAN_KAFKA_RECEIVER)
	@EnableKafkaCollectReceiver
	public KafkaMetricReceiver kafkaCollectReceiver(@Qualifier(BEAN_DEFAULT_VALVE_ALERTER) IndicatorsValveAlerter alerter,
			MetricStore store) {
		return new KafkaMetricReceiver(alerter, store);
	}

	@Bean(BEAN_KAFKA_BATCH_FACTORY)
	@EnableKafkaCollectReceiver
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public KafkaListenerContainerFactory<?> batchFactory(ReceiverProperties conf) {
		// Create consumer factory.
		Properties properties = conf.getKafka().getProperties();
		Map<String, Object> config = (Map) properties;
		ConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(config);

		// Create concurrent consumer container factory.
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(cf);
		factory.setConcurrency(conf.getKafka().getConcurrency());
		factory.setBatchListener(true);

		// Spring kafka container properties.
		ContainerProperties containerProps = factory.getContainerProperties();
		containerProps.setPollTimeout(conf.getKafka().getPollTimeout());
		// Bulk consumption change buffer queue size.
		containerProps.setQueueDepth(conf.getKafka().getQueueDepth());
		containerProps.setAckMode(AckMode.MANUAL_IMMEDIATE);
		return factory;
	}

	//
	// Receive console.
	//

	@Bean
	public ReceiveConsole receiveConsole() {
		return new ReceiveConsole();
	}

}