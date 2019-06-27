package com.wl4g.devops.umc.receiver;

import com.wl4g.devops.common.bean.umc.model.physical.PhysicalStatInfo;
import com.wl4g.devops.common.bean.umc.model.third.KafkaStatInfo;
import com.wl4g.devops.common.bean.umc.model.third.RedisStatInfo;
import com.wl4g.devops.common.bean.umc.model.third.ZookeeperStatInfo;
import com.wl4g.devops.common.bean.umc.model.virtual.Docker;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.umc.store.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.*;
import static com.wl4g.devops.umc.config.UmcReceiveAutoConfiguration.BEAN_KAFKA_BATCH_FACTORY;

/**
 * KAFKA collection receiver
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class KafkaCollectReceiver extends AbstractCollectReceiver {

	public KafkaCollectReceiver(PhysicalMetricStore pStore, VirtualMetricStore vStore, RedisMetricStore rStore, ZookeeperMetricStore zStore, KafkaMetricStore kStore,StatInfoMetricStore mStroe) {
		super(pStore, vStore, rStore,zStore,kStore,mStroe);
	}

	/**
	 * Receiving consumer messages on multiple topics
	 * 
	 * @param records
	 * @param ack
	 */
	@KafkaListener(topicPattern = TOPIC_RECEIVE_PATTERN, containerFactory = BEAN_KAFKA_BATCH_FACTORY)
	public void onMultiReceive(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
		try {
			if (log.isInfoEnabled()) {
				log.info("Consumer records for - {}", records);
			}

			// Process
			doProcess(records, new MultiAcknowledgmentState(ack));
		} catch (Exception e) {
			log.error("", e);
		} finally {
			// Echo
			// ack.acknowledge();
		}
	}

	/**
	 * UMC agent metric processing.
	 * 
	 * @param records
	 * @param state
	 */
	private void doProcess(List<ConsumerRecord<String, String>> records, MultiAcknowledgmentState state) {
		//
		// TODO
		//
		for(ConsumerRecord<String, String> consumerRecord : records){
			log.info("listen kafka message"+consumerRecord.value());
			String key = consumerRecord.key();
			String value = consumerRecord.value();

			switch (key){
				case URI_PHYSICAL:
					PhysicalStatInfo physical = JacksonUtils.parseJSON(value, PhysicalStatInfo.class);
					putPhysical(physical);
					break;
				case URI_VIRTUAL_DOCKER:
					Docker docker = JacksonUtils.parseJSON(value, Docker.class);
					putVirtualDocker(docker);
					break;
				case URI_REDIS:
					RedisStatInfo redis = JacksonUtils.parseJSON(value, RedisStatInfo.class);
					putRedis(redis);
					break;
				case URI_ZOOKEEPER:
					ZookeeperStatInfo zookeeper = JacksonUtils.parseJSON(value, ZookeeperStatInfo.class);
					putZookeeper(zookeeper);
					break;
				case URI_KAFKA:
					KafkaStatInfo kafka = JacksonUtils.parseJSON(value, KafkaStatInfo.class);
					putKafka(kafka);
					break;
				default:
					throw new UnsupportedOperationException("unsupport this type");
			}
		}
		state.completed();
	}

	/**
	 * Multiple ACK completion state
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年6月18日
	 * @since
	 */
	public static class MultiAcknowledgmentState {

		final private Acknowledgment ack;

		public MultiAcknowledgmentState(Acknowledgment ack) {
			super();
			this.ack = ack;
		}

		public void completed() {
			ack.acknowledge();
		}

	}

}
