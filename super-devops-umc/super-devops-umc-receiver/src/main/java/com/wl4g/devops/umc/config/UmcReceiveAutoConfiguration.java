package com.wl4g.devops.umc.config;

import com.wl4g.devops.common.config.AbstractOptionalControllerConfiguration;
import com.wl4g.devops.umc.annotation.EnableHttpReceiver;
import com.wl4g.devops.umc.annotation.EnableKafkaReceiver;
import com.wl4g.devops.umc.receiver.HttpCollectReceiver;
import com.wl4g.devops.umc.receiver.KafkaCollectReceiver;
import com.wl4g.devops.umc.store.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer.AckMode;
import org.springframework.kafka.listener.config.ContainerProperties;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Properties;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.URI_RECEIVER;
import static com.wl4g.devops.umc.config.ReceiverProperties.KEY_RECEIVER_PREFIX;

/**
 * UMC receiver auto configuration
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
@Configuration
public class UmcReceiveAutoConfiguration extends AbstractOptionalControllerConfiguration {

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
	@EnableHttpReceiver
	public HttpCollectReceiver httpCollectReceiver(PhysicalMetricStore pStore, VirtualMetricStore vStore, RedisMetricStore rStore, ZookeeperMetricStore zStore, KafkaMetricStore kStore) {
		return new HttpCollectReceiver(pStore, vStore, rStore, zStore, kStore);
	}

	@Bean
	@EnableHttpReceiver
	public PrefixHandlerMapping httpCollectReceiverPrefixHandlerMapping() {
		return createPrefixHandlerMapping();
	}

	@Override
	protected String getMappingPrefix() {
		return URI_RECEIVER;
	}

	@Override
	protected Class<? extends Annotation> annotationClass() {
		return com.wl4g.devops.umc.annotation.HttpCollectReceiver.class;
	}

	//
	// KAFKA receiver.
	//

	@Bean(BEAN_KAFKA_RECEIVER)
	@EnableKafkaReceiver
	public KafkaCollectReceiver kafkaCollectReceiver(PhysicalMetricStore pStore, VirtualMetricStore vStore, RedisMetricStore rStore, ZookeeperMetricStore zStore, KafkaMetricStore kStore) {
		return new KafkaCollectReceiver(pStore, vStore, rStore, zStore, kStore);
	}

	@Bean(BEAN_KAFKA_BATCH_FACTORY)
	@EnableKafkaReceiver
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

}
