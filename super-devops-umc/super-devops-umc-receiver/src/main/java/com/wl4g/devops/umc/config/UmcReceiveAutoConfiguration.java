package com.wl4g.devops.umc.config;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.URI_RECEIVER;

import java.lang.annotation.Annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.common.config.AbstractOptionalControllerConfiguration;
import com.wl4g.devops.umc.receiver.HttpCollectReceiver;
import com.wl4g.devops.umc.receiver.KafkaCollectReceiver;
import com.wl4g.devops.umc.store.PhysicalMetricStore;
import com.wl4g.devops.umc.store.VirtualMetricStore;

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

	@Bean(BEAN_HTTP_RECEIVER)
	public HttpCollectReceiver httpCollectReceiver(PhysicalMetricStore pStore, VirtualMetricStore vStore) {
		return new HttpCollectReceiver(pStore, vStore);
	}

	@Bean
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

	@Bean(BEAN_KAFKA_RECEIVER)
	public KafkaCollectReceiver kafkaCollectReceiver(PhysicalMetricStore pStore, VirtualMetricStore vStore) {
		return new KafkaCollectReceiver(pStore, vStore);
	}

}
