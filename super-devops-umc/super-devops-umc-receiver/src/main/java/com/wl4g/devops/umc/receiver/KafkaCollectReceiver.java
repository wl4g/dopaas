package com.wl4g.devops.umc.receiver;

import com.google.protobuf.InvalidProtocolBufferException;
import com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate;
import com.wl4g.devops.umc.store.MetricStore;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.utils.Bytes;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.TOPIC_KAFKA_RECEIVE_PATTERN;
import static com.wl4g.devops.umc.config.UmcReceiveAutoConfiguration.BEAN_KAFKA_BATCH_FACTORY;

/**
 * KAFKA collection receiver
 *
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class KafkaCollectReceiver extends AbstractCollectReceiver {

	public KafkaCollectReceiver(MetricStore store) {
		super(store);
	}

	/**
	 * Receiving consumer messages on multiple topics
	 *
	 * @param records
	 * @param ack
	 */
	@KafkaListener(topicPattern = TOPIC_KAFKA_RECEIVE_PATTERN, containerFactory = BEAN_KAFKA_BATCH_FACTORY)
	public void onMultiReceive(List<ConsumerRecord<byte[], Bytes>> records, Acknowledgment ack) {
		try {
			if (log.isDebugEnabled()) {
				log.debug("Receive metric records - {}", records);
			}
			if (log.isInfoEnabled()) {
				log.info("Receive metric records size - {}", records.size());
			}

			doProcess(records, new MultiAcknowledgmentState(ack));
		} catch (Exception e) {
			log.error(String.format("Failed to receive process for ", records.size()), e);
		}
	}

	/**
	 * UMC agent metric processing.
	 *
	 * @param records
	 * @param state
	 */
	private void doProcess(List<ConsumerRecord<byte[], Bytes>> records, MultiAcknowledgmentState state) {
		for (ConsumerRecord<byte[], Bytes> record : records) {
			try {
				Bytes value = record.value();
				MetricAggregate aggregate = MetricAggregate.parseFrom(value.get());
				if (log.isDebugEnabled()) {
					log.debug("Put metric aggregate for - {}", aggregate);
				}

				putMetrics(aggregate);
			} catch (InvalidProtocolBufferException e) {
				log.error("Failed to parse metric message.", e);
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
