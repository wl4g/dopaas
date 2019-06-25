package com.wl4g.devops.common.bean.umc.model.third;

import java.util.Map;

/**
 * @author vjay
 * @date 2019-06-20 15:52:00
 */
public class KafkaStatInfo {

	private KafkaInfo[] kafkaInfos;

	public KafkaInfo[] getKafkaInfos() {
		return kafkaInfos;
	}

	public void setKafkaInfos(KafkaInfo[] kafkaInfos) {
		this.kafkaInfos = kafkaInfos;
	}

	public static class KafkaInfo {
		private int port;
		private Map<String, Object> info;

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public Map<String, Object> getInfo() {
			return info;
		}

		public void setInfo(Map<String, Object> info) {
			this.info = info;
		}
	}

}
