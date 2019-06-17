package com.wl4g.devops.umc.config;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.URI_RECEIVER;

import java.lang.annotation.Annotation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.common.config.AbstractOptionalControllerConfiguration;
import com.wl4g.devops.umc.receiver.RESTFulCollectReceiver;
import com.wl4g.devops.umc.receiver.KafkaCollectReceiver;
import com.wl4g.devops.umc.store.adapter.PhysicalMetricStoreAdapter;
import com.wl4g.devops.umc.store.adapter.VirtualMetricStoreAdapter;

/**
 * UMC receiver auto configuration
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
@Configuration
public class UmcReceiveAutoConfiguration extends AbstractOptionalControllerConfiguration {

	final public static String BEAN_HTTP_RECEIVER = "restfulCollectReceiver";
	final public static String BEAN_KAFKA_RECEIVER = "kafkaCollectReceiver";

	@Bean(BEAN_HTTP_RECEIVER)
	public RESTFulCollectReceiver restfulCollectReceiverCollectReceiver(PhysicalMetricStoreAdapter pStore,
			VirtualMetricStoreAdapter vStore) {
		return new RESTFulCollectReceiver(pStore, vStore);
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
	public KafkaCollectReceiver kafkaCollectReceiver(PhysicalMetricStoreAdapter pStore, VirtualMetricStoreAdapter vStore) {
		return new KafkaCollectReceiver(pStore, vStore);
	}

}
