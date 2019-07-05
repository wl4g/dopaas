package com.wl4g.devops.umc.alarm;

import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;
import com.wl4g.devops.common.bean.umc.model.TemplateHisInfo;
import com.wl4g.devops.common.enums.OperatorEnum;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.GenericTaskRunner.RunProperties;
import com.wl4g.devops.umc.config.AlarmProperties;
import com.wl4g.devops.umc.rule.AbstractRuleJudge;
import com.wl4g.devops.umc.rule.AvgRuleJedge;
import com.wl4g.devops.umc.rule.RuleManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.KEY_CACHE_ALARM_RULE;

/**
 * Default collection metric valve alerter.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class DefaultIndicatorsValveAlerter extends GenericTaskRunner<RunProperties> implements IndicatorsValveAlerter {

	@Autowired
	private JedisService jedisService;// TODO
	@Autowired
	private RuleManager ruleManager;

	@Autowired
	AlarmDaoInterface alarmDaoInterface;

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
			List<MetricAggregateWrapper.Metric> metricsList = wrap.getMetrics();
			//long gatherTime = wrap.getTimeStamp();
			String instance = wrap.getCollectId();
			String instandId = ruleManager.getInstandId(instance);

			if(StringUtils.isBlank(instandId)){
				return;
			}
			String json = jedisService.get(KEY_CACHE_ALARM_RULE+instandId);
			AlarmRuleInfo alarmConfigRedis = JacksonUtils.parseJSON(json, AlarmRuleInfo.class);

			List<AlarmTemplate> alarmTemplates = alarmConfigRedis.getAlarmTemplates();

			for(MetricAggregateWrapper.Metric metric : metricsList){
				Map<String, String> tagsMap = metric.getTags();
				String metricName = metric.getMetric();
				for(AlarmTemplate alarmTemplate : alarmTemplates){
					if(StringUtils.equals(metricName,alarmTemplate.getMetric())){
						String tags = alarmTemplate.getTags();
						Map<String, String> map = str2Map(tags);
						//check tags
						if(!isTagsMatch(tagsMap,map)){
							continue;
						}
						//TODO duel rules
						List<AlarmRule> rules = alarmTemplate.getRules();
						Long longestKeepTime = ruleManager.getLongestRuleKeepTime(rules);
						//get history point from redis
						List<TemplateHisInfo.Point> points = ruleManager.duelTempalteInRedis(alarmTemplate.getId(), metric.getValue(), wrap.getTimeStamp(), now, longestKeepTime.intValue());

						if(checkRoleMatch(points,rules,now)){
							//TODO send msg
							sendMsg(alarmTemplate,instandId);
						}
					}
				}
			}
		});

	}

	private boolean checkRoleMatch(List<TemplateHisInfo.Point> points, List<AlarmRule> rules, long now ){
		//or
		for(AlarmRule rule : rules){
			Double[] valuesByContinuityTime = getValuesByContinuityTime(rule.getContinuityTime(), points, now);
			String aggregator = rule.getAggregator();
			AbstractRuleJudge ruleJedge = getRuleJedge(aggregator);
			boolean match = ruleJedge.judge(valuesByContinuityTime, OperatorEnum.safeOf(rule.getOperator()),rule.getValue());
			if(match){
				return true;
			}
		}
		return false;
	}


	private AbstractRuleJudge getRuleJedge(String aggregator){
		//TODO
		if(aggregator.equals("")){
			return new AvgRuleJedge();
		}
		return null;
	}


	private Double[] getValuesByContinuityTime(long continuityTime,List<TemplateHisInfo.Point> points,long now){
		List<Double> values = new ArrayList<>();
		for(TemplateHisInfo.Point point : points){
			if(now-point.getTimeStamp()<continuityTime){
				values.add(point.getValue());
			}
		}
		Double[] doubles = new Double[values.size()];
		return values.toArray(doubles);
	}



	private void sendMsg(AlarmTemplate alarmTemplate, String instandId){
		//get all match alarm config
		List<AlarmConfig> alarmConfigs = alarmDaoInterface.selectByTemplateId(alarmTemplate.getId());
		for(AlarmConfig alarmConfig : alarmConfigs){
			if(StringUtils.isBlank(alarmConfig.getTags())) continue;
			String[] tags = alarmConfig.getTags().split(",");
			boolean matchInstant = Arrays.asList(tags).contains(instandId);
			if(matchInstant){
				if(StringUtils.isBlank(alarmConfig.getAlarmMember())) continue;
				String[] alarmTarget = alarmConfig.getAlarmMember().split(",");
				String msg = alarmConfig.getAlarmContent();
				//TODO send msg
				log.info("send msg:"+msg);
				//new WeChatSender().send(Arrays.asList(alarmTarget),msg);
			}
		}


	}


	private boolean isTagsMatch(Map<String, String> tagsMap,Map<String, String> map){

		boolean isTagMatch = true;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String value = tagsMap.get(entry.getKey());
			if(StringUtils.isBlank(value)){
				isTagMatch = false;
				break;
			}
			if(!StringUtils.equals(value,entry.getValue())){
				isTagMatch = false;
				break;
			}
		}
		return isTagMatch;
	}



	private Map<String,String> str2Map(String str){
		if(StringUtils.isBlank(str)){
			return null;
		}
		String[] strs = str.split(",");
		Map<String,String> map = new HashMap<>();
		for(String string : strs){
			String kv[] = string.split("=");
			if(kv.length!=2){
				continue;
			}
			map.put(kv[0],kv[1]);
		}
		return map;
	}

}
