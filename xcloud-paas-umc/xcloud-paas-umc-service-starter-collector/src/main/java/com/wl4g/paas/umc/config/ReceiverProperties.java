/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.paas.umc.config;

import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

/**
 * Receiver properties .
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2017年11月16日
 * @since
 */
public class ReceiverProperties {

	final public static String KEY_RECEIVER_PREFIX = "spring.cloud.devops.umc.receiver";

	/**
	 * KAFKA consumer properties
	 */
	private KafkaReceiverProperties kafka = new KafkaReceiverProperties();

	/** HTTP receiver configuration properties */
	private HttpReceiverProperties http = new HttpReceiverProperties();

	public KafkaReceiverProperties getKafka() {
		return kafka;
	}

	public void setKafka(KafkaReceiverProperties kafka) {
		this.kafka = kafka;
	}

	public HttpReceiverProperties getHttp() {
		return http;
	}

	public void setHttp(HttpReceiverProperties http) {
		this.http = http;
	}

	/**
	 * KAFKA consumer configuration properties.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2018年5月15日
	 * @since
	 */
	public static class KafkaReceiverProperties {

		private boolean enabled = false;

		private int pollTimeout = 1000;

		private int concurrency = 3;

		/**
		 * Bulk consumption change buffer queue size.
		 */
		private int queueDepth = 4092;

		private Properties properties = new Properties() {
			private static final long serialVersionUID = 299259605679445927L;
			{
				// Default properties
				put(GROUP_ID_CONFIG, "defaultUmcReceiverClusterId");
				put(BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
				put(SESSION_TIMEOUT_MS_CONFIG, "20000");
				put(FETCH_MIN_BYTES_CONFIG, "1");
				put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.BytesDeserializer");
				put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.BytesDeserializer");
				put(AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
				put(AUTO_OFFSET_RESET_CONFIG, "latest");
				put(ENABLE_AUTO_COMMIT_CONFIG, "false");
				put(MAX_POLL_RECORDS_CONFIG, "1000");
				// put(ENABLE_AUTO_COMMIT_CONFIG, true);
			}
		};

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean startup) {
			this.enabled = startup;
		}

		public int getPollTimeout() {
			return pollTimeout;
		}

		public void setPollTimeout(int pollTimeout) {
			this.pollTimeout = pollTimeout;
		}

		public int getConcurrency() {
			return concurrency;
		}

		public void setConcurrency(int concurrency) {
			this.concurrency = concurrency;
		}

		public int getQueueDepth() {
			return queueDepth;
		}

		public void setQueueDepth(int queueDepth) {
			this.queueDepth = queueDepth;
		}

		public Properties getProperties() {
			return properties;
		}

		public void setProperties(Properties properties) {
			this.properties = properties;
		}

	}

	/**
	 * HTTP receiver configuration properties
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年6月18日
	 * @since
	 */
	public static class HttpReceiverProperties {

		private boolean enabled = false;

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

	}

}