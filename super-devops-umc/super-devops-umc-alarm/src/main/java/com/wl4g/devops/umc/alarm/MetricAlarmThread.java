package com.wl4g.devops.umc.alarm;

import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;
import com.wl4g.devops.common.bean.umc.model.proto.MetricModel.Metric;
import com.wl4g.devops.common.bean.umc.model.proto.MetricModel.MetricAggregate;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.support.task.GenericTaskRunner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.KEY_CACHE_ALARM_RULE;
import static com.wl4g.devops.common.constants.UMCDevOpsConstants.KEY_CACHE_INSTANCE_ID;

/**
 * @author vjay
 * @date 2019-07-04 11:23:00
 */
public class MetricAlarmThread extends GenericTaskRunner {

	@Autowired
	private JedisService jedisService;// TODO

	private MetricAggregate aggregate;

	public MetricAlarmThread(RunProperties config, MetricAggregate aggregate) {
		super(config);
		this.aggregate = aggregate;
	}

	@Override
	public void run() {
		List<Metric> metricsList = aggregate.getMetricsList();
		long gatherTime = aggregate.getTimestamp();
		String instance = aggregate.getInstance();
		String instandId = jedisService.get(KEY_CACHE_INSTANCE_ID + instance);
		if (StringUtils.isBlank(instandId)) {
			return;
		}
		String json = jedisService.get(KEY_CACHE_ALARM_RULE + String.valueOf(instandId));
		AlarmRuleInfo ruleInfo = JacksonUtils.parseJSON(json, AlarmRuleInfo.class);

		List<AlarmTemplate> alarmTemplates = ruleInfo.getAlarmTemplates();

		for (Metric metric : metricsList) {
			Map<String, String> tagsMap = metric.getTagsMap();
			String metricName = metric.getMetric();
			for (AlarmTemplate alarmTemplate : alarmTemplates) {
				if (StringUtils.equals(metricName, alarmTemplate.getMetric())) {
					String tags = alarmTemplate.getTags();
					Map<String, String> map = str2Map(tags);
					// check tags
					if (!isTagsMatch(tagsMap, map)) {
						continue;
					}
					// TODO duel rules
					List<AlarmRule> rules = alarmTemplate.getRules();
					for (AlarmRule rule : rules) {

					}
					double value = metric.getValue();

				}
			}
		}
	}

	private boolean isTagsMatch(Map<String, String> tagsMap, Map<String, String> map) {

		boolean isTagMatch = true;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String value = tagsMap.get(entry.getKey());
			if (StringUtils.isBlank(value)) {
				isTagMatch = false;
				break;
			}
			if (!StringUtils.equals(value, entry.getValue())) {
				isTagMatch = false;
				break;
			}
		}
		return isTagMatch;
	}

	private Map<String, String> str2Map(String str) {
		if (StringUtils.isBlank(str)) {
			return null;
		}
		String[] strs = str.split(",");
		Map<String, String> map = new HashMap<>();
		for (String string : strs) {
			String kv[] = string.split("=");
			if (kv.length != 2) {
				continue;
			}
			map.put(kv[0], kv[1]);
		}
		return map;
	}

}
