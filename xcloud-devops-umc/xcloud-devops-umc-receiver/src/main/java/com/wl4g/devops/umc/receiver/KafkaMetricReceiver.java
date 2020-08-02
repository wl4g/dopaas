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
package com.wl4g.devops.umc.receiver;

import com.google.protobuf.InvalidProtocolBufferException;
import com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate;
import com.wl4g.devops.umc.alarm.alerting.IndicatorsValveAlerter;
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
public class KafkaMetricReceiver extends AbstractMetricReceiver {

	public KafkaMetricReceiver(IndicatorsValveAlerter alerter, MetricStore store) {
		super(alerter, store);
	}

	/**
	 * Receiving consumer messages on multiple topics
	 *
	 * @param records
	 * @param ack
	 */
	@KafkaListener(topicPattern = TOPIC_KAFKA_RECEIVE_PATTERN, containerFactory = BEAN_KAFKA_BATCH_FACTORY)
	public void onMetricReceive(List<ConsumerRecord<byte[], Bytes>> records, Acknowledgment ack) {
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
				MetricAggregate aggregate = MetricAggregate.parseFrom(record.value().get());
				if (log.isDebugEnabled()) {
					log.debug("Put metric aggregate for - {}", aggregate);
				}

				// Storage metrics.
				putMetrics(aggregate);

				// Metrics alarm.
				alarm(aggregate);
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