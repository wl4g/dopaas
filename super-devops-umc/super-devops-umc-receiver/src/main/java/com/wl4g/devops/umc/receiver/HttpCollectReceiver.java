package com.wl4g.devops.umc.receiver;

import com.google.protobuf.InvalidProtocolBufferException;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.proto.MetricModel;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.dao.umc.AlarmTemplateDao;
import com.wl4g.devops.umc.alarm.IndicatorsValveAlerter;
import com.wl4g.devops.umc.alarm.MetricAggregateWrapper;
import com.wl4g.devops.umc.store.MetricStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

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
public class HttpCollectReceiver extends AbstractCollectReceiver {

	@Autowired
	private AlarmTemplateDao alarmTemplateDao;


	public HttpCollectReceiver(MetricStore store) {
		super(store);
	}

	/**
	 * metrics
	 */
	@RequestMapping(URI_HTTP_RECEIVER_ENDPOINT)
	public void statInfoReceive(@RequestBody byte[] body) {
		try {
			MetricModel.MetricAggregate aggregate = MetricModel.MetricAggregate.parseFrom(body);
			putMetrics(aggregate);
			alarm(aggregate);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}


	@RequestMapping("test")
	public String test() {
		List<AlarmTemplate> alarmTemplates = alarmTemplateDao.getByCollectId(null);
		return JacksonUtils.toJSONString(alarmTemplates);
	}
}
