package com.wl4g.devops.umc.alarm;

import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;
import com.wl4g.devops.common.bean.umc.model.TemplateHisInfo.Point;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.GenericTaskRunner.RunProperties;
import com.wl4g.devops.umc.alarm.MetricAggregateWrapper.MetricWrapper;
import com.wl4g.devops.umc.config.AlarmProperties;
import com.wl4g.devops.umc.notification.AlarmNotifier;
import com.wl4g.devops.umc.notification.AlarmType;
import com.wl4g.devops.umc.notification.email.EmailNotifier;
import com.wl4g.devops.umc.notification.sms.SmsNotifier;
import com.wl4g.devops.umc.notification.wechat.WeChatNotifier;
import com.wl4g.devops.umc.rule.AggregatorType;
import com.wl4g.devops.umc.rule.OperatorType;
import com.wl4g.devops.umc.rule.RuleConfigManager;
import com.wl4g.devops.umc.rule.handler.RuleConfigHandler;
import com.wl4g.devops.umc.rule.inspect.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.wl4g.devops.umc.rule.AggregatorType.safeOf;

/**
 * Default collection metric valve alerter.
 *
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class DefaultIndicatorsValveAlerter extends GenericTaskRunner<RunProperties> implements IndicatorsValveAlerter {

	final protected Logger log = LoggerFactory.getLogger(getClass());

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
		// Auto-generated method stub

		getWorker().execute(() -> {
			duel(wrap);
		});
	}


	public void duel(MetricAggregateWrapper wrap){
		long now = System.currentTimeMillis();
		Date nowDate = new Date();
		List<MetricAggregateWrapper.MetricWrapper> metricsList = wrap.getMetrics();
		long gatherTime = wrap.getTimestamp();
		String collectIp = wrap.getCollectId();
		log.info("start match rule,collectIp={}",collectIp);

		Integer collectId = ruleConfigManager.convertCollectIp(collectIp);
		if (null==collectId) {
			return;
		}
		AlarmRuleInfo alarmRuleInfo =  ruleConfigManager.getAlarmRuleInfo(collectId);

		List<AlarmTemplate> alarmTemplates = alarmRuleInfo.getAlarmTemplates();

		for (MetricWrapper metric : metricsList) {
			Map<String, String> tagsMap = metric.getTags();
			String metricName = metric.getMetric();
			for (AlarmTemplate alarmTemplate : alarmTemplates) {
				if (StringUtils.equals(metricName, alarmTemplate.getMetric())) {
					String tags = alarmTemplate.getTags();
					Map<String, String> map = JacksonUtils.parseJSON(tags,Map.class);
					// check tags
					if (!isTagsMatch(tagsMap, map)) {
						continue;
					}
					//duel rules
					List<AlarmRule> rules = alarmTemplate.getRules();
					Long longestKeepTime = ruleConfigManager.cacheTime(rules);
					// get history point from redis
					List<Point> points = ruleConfigManager.duelTempalteInRedis(alarmTemplate.getId(),
							metric.getValue(), wrap.getTimestamp(), now, longestKeepTime.intValue());

					List<AlarmRule>  macthRule = null;
					if (checkRuleMatch(points, rules, now,macthRule)) {
						log.info("match template rule,metricName={}, template_id={},historyData={}",metricName,alarmTemplate.getId(),JacksonUtils.toJSONString(points));
						List<AlarmConfig> alarmConfigs = ruleConfigHandler.getByCollectIdAndTemplateId(alarmTemplate.getId(), collectId);
						//save record
						ruleConfigHandler.saveRecord(alarmTemplate, alarmConfigs, collectId, gatherTime, nowDate, macthRule);
						// send msg
						sendMsg(alarmTemplate, collectId,alarmConfigs);
					}
				}
			}
		}
	}


	/**
	 * Chekc role is match
	 */
	private boolean checkRuleMatch(List<Point> points, List<AlarmRule> rules, long now,List<AlarmRule>  macthRule) {
		// or
		boolean result = false;
		for (AlarmRule rule : rules) {
			Double[] valuesByContinuityTime = effectiveScopeValues(rule.getContinuityTime(), points, now);
			String aggregator = rule.getAggregator();
			AbstractRuleInspector ruleJudge = getRuleJudge(aggregator);
			boolean match = ruleJudge.judge(valuesByContinuityTime, OperatorType.safeOf(rule.getOperator()), rule.getValue());
			if (match) {
				if(null== macthRule){
					macthRule = new ArrayList<>();
				}
				macthRule.add(rule);
				result = true;
			}
		}
		return result;
	}

	/**
	 * Get Rule Judge by aggregator
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
	private Double[] effectiveScopeValues(long continuityTime, List<Point> points, long now) {
		List<Double> values = new ArrayList<>();
		for (Point point : points) {
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
	private void sendMsg(AlarmTemplate alarmTemplate, Integer collectId,List<AlarmConfig> alarmConfigs) {
		// get all match alarm config
		//List<AlarmConfig> alarmConfigs = ruleConfigHandler.getByCollectIdAndTemplateId(alarmTemplate.getId(), collectId);
		for(AlarmConfig alarmConfig : alarmConfigs){
			if (StringUtils.isBlank(alarmConfig.getAlarmMember()))
				continue;
			String[] alarmTarget = alarmConfig.getAlarmMember().split(",");
			String msg = alarmConfig.getAlarmContent();
			// TODO send msg
			log.info("send msg, templateId={},collectId={}, msg={},sendType={},sentTo={}",alarmTemplate.getId(),collectId,msg,alarmConfig.getAlarmType(),alarmConfig.getAlarmMember());
			alarmNotifier(alarmConfig.getAlarmType()).simpleNotify(new ArrayList<>(Arrays.asList(alarmTarget)),alarmConfig.getAlarmContent());
		}
	}



	private AlarmNotifier alarmNotifier(String alarmType){
		AlarmType alarmT = AlarmType.safeOf(Integer.parseInt(alarmType));
		switch (alarmT){
			case EMAIL:
				return new EmailNotifier();
			case SMS:
				return new SmsNotifier();
			case WECHAT:
				return new WeChatNotifier();
			default:
				return null;
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


}
