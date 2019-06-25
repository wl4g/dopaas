package com.wl4g.devops.umc.opentsdb;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.TAG_DISK_NET_PORT;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.TAG_ID;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.Assert;

import com.wl4g.devops.common.bean.umc.model.third.RedisStatInfo;
import com.wl4g.devops.common.bean.umc.model.third.RedisStatInfo.RedisInfo;
import com.wl4g.devops.umc.opentsdb.client.OpenTSDBClient;
import com.wl4g.devops.umc.opentsdb.client.bean.request.Point;
import com.wl4g.devops.umc.store.RedisMetricStore;

/**
 * @author vjay
 * @date 2019-06-20 16:12:00
 */
public class TsdbRedisMetricStore implements RedisMetricStore {

	final protected OpenTSDBClient client;

	public TsdbRedisMetricStore(OpenTSDBClient client) {
		this.client = client;
	}

	@Override
	public boolean save(RedisStatInfo redis) {

		Assert.notNull(redis, "reids is null");
		Assert.notEmpty(redis.getRedisInfos(), "redis infos is null");
		RedisInfo[] redisInfos = redis.getRedisInfos();

		long timestamp = System.currentTimeMillis() / 1000;// opentsdb用秒做时间戳

		for (RedisInfo redisInfo : redisInfos) {
			int port = redisInfo.getPort();
			Map<String, String> infos = redisInfo.getProperties();
			for (Map.Entry<String, String> entry : infos.entrySet()) {
				String key = entry.getKey();
				if (!NumberUtils.isCreatable(entry.getValue())) {
					continue;
				}
				Number value = NumberUtils.createNumber(entry.getValue());
				key = "redis." + key.replaceAll("_", ".");
				Point point = Point.metric(key).tag(TAG_ID, redis.getPhysicalId()).tag(TAG_DISK_NET_PORT, String.valueOf(port))
						.value(timestamp, value).build();
				client.put(point);
			}
		}
		return true;
	}
}
