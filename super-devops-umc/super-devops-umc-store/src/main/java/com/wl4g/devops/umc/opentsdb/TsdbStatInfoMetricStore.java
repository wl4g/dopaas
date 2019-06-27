package com.wl4g.devops.umc.opentsdb;

import com.wl4g.devops.common.bean.umc.model.StatInfos;
import com.wl4g.devops.umc.opentsdb.client.OpenTSDBClient;
import com.wl4g.devops.umc.opentsdb.client.bean.request.Point;
import com.wl4g.devops.umc.store.StatInfoMetricStore;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author vjay
 * @date 2019-06-20 16:12:00
 */
public class TsdbStatInfoMetricStore implements StatInfoMetricStore {

	final protected OpenTSDBClient client;

	public TsdbStatInfoMetricStore(OpenTSDBClient client) {
		this.client = client;
	}

	@Override
	public boolean save(StatInfos statInfos) {
		Long timestamp = statInfos.getTimestamp() / 1000;
		for(StatInfos.StatInfo statInfo : statInfos.getStatInfos()){
			if(StringUtils.isBlank(statInfo.getMetric())||statInfo.getValue()==null||statInfos.getStatInfos()==null||statInfos.getStatInfos().length<=0){
				continue;
			}
			Point.MetricBuilder pointBuilder = Point.metric(statInfo.getMetric())
					.value(timestamp, statInfo.getValue());
			Map<String, String> tag = statInfo.getTags();
			for (Map.Entry<String, String> entry : tag.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				pointBuilder.tag(key,value);
			}
			Point point = pointBuilder.build();
			client.put(point);
		}
		return true;
	}
}
