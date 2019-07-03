package com.wl4g.devops.umc.opentsdb;

import com.wl4g.devops.common.bean.umc.model.proto.MetricModel;
import com.wl4g.devops.umc.opentsdb.client.OpenTSDBClient;
import com.wl4g.devops.umc.opentsdb.client.bean.request.Point;
import com.wl4g.devops.umc.store.MetricStore;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author vjay
 * @date 2019-06-20 16:12:00
 */
public class TsdbMetricStore implements MetricStore {

	final private Logger log = LoggerFactory.getLogger(getClass());

	final protected OpenTSDBClient client;

	public TsdbMetricStore(OpenTSDBClient client) {
		this.client = client;
	}

	@Override
	public boolean save(MetricModel.MetricAggregate aggregate) {
		long timestamp = aggregate.getTimestamp();

		int i = 0;
		for(MetricModel.Metric statMetric : aggregate.getMetricsList()){
			statMetric.getTagsMap();
			if(StringUtils.isBlank(statMetric.getMetric())){
				continue;
			}

			Point.MetricBuilder pointBuilder = Point.metric(statMetric.getMetric()).value(timestamp, statMetric.getValue());
			Map<String, String> tag = statMetric.getTagsMap();
			for (Map.Entry<String, String> entry : tag.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				pointBuilder.tag(key,value);
			}
			pointBuilder.tag("instance",aggregate.getInstance());
			Point point = pointBuilder.build();
			i++;
			client.put(point);
		}
		log.info("Metrics count - "+i);

		/*for(StatMetrics.StatMetric statMetric : statMetrics.getStatMetrics()){
			statMetric.setTimestamp(timestamp);
		}
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(factory);
		String json = JacksonUtils.toJSONString(statMetrics.getStatMetrics());
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
		HttpEntity<String> formEntity = new HttpEntity<String>(json, headers);
		String result = restTemplate.postForEntity("http://10.0.0.57:4242/api/put?details",formEntity, String.class).getBody();*/


		return true;
	}


}
