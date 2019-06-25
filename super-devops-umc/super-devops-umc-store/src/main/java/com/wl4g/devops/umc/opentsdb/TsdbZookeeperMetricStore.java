package com.wl4g.devops.umc.opentsdb;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.TAG_ID;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.Assert;

import com.wl4g.devops.common.bean.umc.model.third.ZookeeperStatInfo;
import com.wl4g.devops.umc.opentsdb.client.OpenTSDBClient;
import com.wl4g.devops.umc.opentsdb.client.bean.request.Point;
import com.wl4g.devops.umc.store.ZookeeperMetricStore;

/**
 * @author vjay
 * @date 2019-06-20 16:12:00
 */
public class TsdbZookeeperMetricStore implements ZookeeperMetricStore {

	final protected OpenTSDBClient client;

	public TsdbZookeeperMetricStore(OpenTSDBClient client) {
		this.client = client;
	}

	@Override
	public boolean save(ZookeeperStatInfo zookeeper) {
		Assert.notNull(zookeeper, "zookeeper is null");
		Assert.notEmpty(zookeeper.getProperties(), "redis infos is null");
		Map<String, String> properties = zookeeper.getProperties();
		long timestamp = System.currentTimeMillis() / 1000;// opentsdb用秒做时间戳
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			String key = entry.getKey();
			if (!NumberUtils.isCreatable(entry.getValue())) {
				continue;
			}
			Number value = NumberUtils.createNumber(entry.getValue());
			key = "redis." + key.replaceAll("_", ".");
			Point point = Point.metric(key).tag(TAG_ID, zookeeper.getPhysicalId()).value(timestamp, value).build();
			client.put(point);
		}
		return true;
	}
}
