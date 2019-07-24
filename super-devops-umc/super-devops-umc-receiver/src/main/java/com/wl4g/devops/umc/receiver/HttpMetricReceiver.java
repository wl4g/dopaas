package com.wl4g.devops.umc.receiver;

import com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate;
import com.wl4g.devops.umc.store.MetricStore;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.URI_HTTP_RECEIVER_ENDPOINT;

/**
 * HTTP collection receiver
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
@ResponseBody
@com.wl4g.devops.umc.annotation.HttpCollectReceiver
public class HttpMetricReceiver extends AbstractMetricReceiver {

	public HttpMetricReceiver(MetricStore store) {
		super(store);
	}

	/**
	 * Receiving of HTTP metrics message.
	 */
	@RequestMapping(URI_HTTP_RECEIVER_ENDPOINT)
	public void metricReceive(@RequestBody byte[] body) {
		try {
			MetricAggregate aggregate = MetricAggregate.parseFrom(body);
			// Storage metrics.
			putMetrics(aggregate);

			// Metrics alarm.
			alarm(aggregate);
		} catch (Exception e) {
			log.error("Failed to receive metric handling.", e);
		}
	}

}
