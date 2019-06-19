package com.wl4g.devops.umc.receiver;

import com.wl4g.devops.common.bean.umc.model.physical.Total;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.umc.store.PhysicalMetricStore;
import com.wl4g.devops.umc.store.VirtualMetricStore;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.TOPIC_RECEIVE_PATTERN;
import static com.wl4g.devops.umc.config.UmcReceiveAutoConfiguration.BEAN_KAFKA_BATCH_FACTORY;

/**
 * KAFKA collection receiver
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class KafkaCollectReceiver extends AbstractCollectReceiver {

	public KafkaCollectReceiver(PhysicalMetricStore pStore, VirtualMetricStore vStore) {
		super(pStore, vStore);
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
			String value = consumerRecord.value();
			try {
				Total total = JacksonUtils.parseJSON(value, Total.class);
				//Total total = (Total) JSONUtils.parse(value);
				putPhysical(total);
			}catch (Exception e){
				log.error(e.getMessage());
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
