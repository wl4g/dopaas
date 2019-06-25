package com.wl4g.devops.common.bean.umc.model.third;

import com.wl4g.devops.common.bean.umc.model.Base;

import java.util.Map;

/**
 * @author vjay
 * @date 2019-06-20 15:52:00
 */
public class RedisStatInfo extends Base {

	private static final long serialVersionUID = 8744833634150314250L;

	private RedisInfo[] redisInfos;

	public RedisInfo[] getRedisInfos() {
		return redisInfos;
	}

	public void setRedisInfos(RedisInfo[] redisInfos) {
		this.redisInfos = redisInfos;
	}

	public static class RedisInfo {
		private int port;
		private Map<String, String> properties;

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public Map<String, String> getProperties() {
			return properties;
		}

		public void setProperties(Map<String, String> properties) {
			this.properties = properties;
		}
	}

}
