package com.wl4g.devops.umc.alarm;

import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;
import com.wl4g.devops.common.bean.umc.model.TemplateHisInfo;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.GenericTaskRunner.RunProperties;
import com.wl4g.devops.umc.alarm.MetricAggregateWrapper.MetricWrapper;
import com.wl4g.devops.umc.config.AlarmProperties;
import com.wl4g.devops.umc.rule.*;
import com.wl4g.devops.umc.rule.handler.RuleConfigHandler;
import com.wl4g.devops.umc.rule.inspect.AbstractRuleInspector;
import com.wl4g.devops.umc.rule.inspect.AvgRuleInspector;
import com.wl4g.devops.umc.rule.inspect.LastRuleInspector;
import com.wl4g.devops.umc.rule.inspect.MaxRuleInspector;
import com.wl4g.devops.umc.rule.inspect.MinRuleInspector;
import com.wl4g.devops.umc.rule.inspect.SumRuleInspector;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.wl4g.devops.umc.rule.AggregatorType.*;

/**
 * Default collection metric valve alerter.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class DefaultIndicatorsValveAlerter extends GenericTaskRunner<RunProperties> implements IndicatorsValveAlerter {

	@Autowired
	private RuleConfigManager ruleConfigManager;

	@Autowired
	private RuleConfigHandler ruleConfigHandler;

	public DefaultIndicatorsValveAlerter(AlarmProperties config) {
		super(config);
	}

	@Override
	public void run() {
		// Ignore
		//
	}

	@Override
	public void alarm(MetricAggregateWrapper wrap) {
		// TODO Auto-generated method stub

		getWorker().execute(() -> {
			// TODO alarm send ...
			long now = System.currentTimeMillis();
			List<MetricAggregateWrapper.MetricWrapper> metricsList = wrap.getMetrics();
			// long gatherTime = wrap.getTimeStamp();
			String instance = wrap.getCollectId();
			String collectId = ruleConfigManager.convertCollectId(instance);

			if (StringUtils.isBlank(collectId)) {
				return;
			}
			String json = ruleConfigManager.getAlarmRuleInfo(collectId);
			AlarmRuleInfo alarmConfigRedis = JacksonUtils.parseJSON(json, AlarmRuleInfo.class);

			List<AlarmTemplate> alarmTemplates = alarmConfigRedis.getAlarmTemplates();

			for (MetricWrapper metric : metricsList) {
				Map<String, String> tagsMap = metric.getTags();
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
						Long longestKeepTime = ruleConfigManager.getLongestRuleKeepTime(rules);
						// get history point from redis
						List<TemplateHisInfo.Point> points = ruleConfigManager.duelTempalteInRedis(alarmTemplate.getId(),
								metric.getValue(), wrap.getTimestamp(), now, longestKeepTime.intValue());

						if (checkRuleMatch(points, rules, now)) {
							// TODO send msg
							sendMsg(alarmTemplate, collectId);
						}
					}
				}
			}
		});

	}

	/**
	 * Chekc role is match
	 */
	private boolean checkRuleMatch(List<TemplateHisInfo.Point> points, List<AlarmRule> rules, long now) {
		// or
		for (AlarmRule rule : rules) {
			Double[] valuesByContinuityTime = getValuesByContinuityTime(rule.getContinuityTime(), points, now);
			String aggregator = rule.getAggregator();
			AbstractRuleInspector ruleJudge = getRuleJudge(aggregator);
			boolean match = ruleJudge.judge(valuesByContinuityTime, OperatorType.safeOf(rule.getOperator()), rule.getValue());
			if (match) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get RuleJedge by aggregator
	 */
	private AbstractRuleInspector getRuleJudge(String aggregator) {
		AggregatorType aggregatorEnum = safeOf(aggregator);
		switch (aggregatorEnum){
			case AVG:
				return new AvgRuleInspector();
			case LAST:
				return new LastRuleInspector();
			case MAX:
				return new MaxRuleInspector();
			case MIN:
				return new MinRuleInspector();
			case SUM:
				return new SumRuleInspector();
			default:
				return null;
		}
	}

	/**
	 * Get effective point in range time
	 */
	private Double[] getValuesByContinuityTime(long continuityTime, List<TemplateHisInfo.Point> points, long now) {
		List<Double> values = new ArrayList<>();
		for (TemplateHisInfo.Point point : points) {
			if (now - point.getTimeStamp() < continuityTime) {
				values.add(point.getValue());
			}
		}
		Double[] doubles = new Double[values.size()];
		return values.toArray(doubles);
	}

	/**
	 * Send msg by template , found sent to who by template
	 */
	private void sendMsg(AlarmTemplate alarmTemplate, String collectId) {
		// get all match alarm config
		List<AlarmConfig> alarmConfigs = ruleConfigHandler.selectByTemplateId(alarmTemplate.getId());
		for (AlarmConfig alarmConfig : alarmConfigs) {
			if (StringUtils.isBlank(alarmConfig.getTags()))
				continue;
			String[] tags = alarmConfig.getTags().split(",");
			boolean matchInstant = Arrays.asList(tags).contains(collectId);
			if (matchInstant) {
				if (StringUtils.isBlank(alarmConfig.getAlarmMember()))
					continue;
				// String[] alarmTarget =
				// alarmConfig.getAlarmMember().split(",");
				String msg = alarmConfig.getAlarmContent();
				// TODO send msg
				log.info("send msg:" + msg);
				// new WeChatSender().send(Arrays.asList(alarmTarget),msg);
			}
		}

	}

	/**
	 * Is Tags Match
	 */
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

	/**
	 * tool
	 */
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
