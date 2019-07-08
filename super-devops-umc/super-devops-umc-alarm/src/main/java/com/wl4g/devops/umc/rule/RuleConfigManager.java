package com.wl4g.devops.umc.rule;

import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;
import com.wl4g.devops.common.bean.umc.model.TemplateHisInfo;
import com.wl4g.devops.common.bean.umc.model.TemplateHisInfo.Point;
import com.wl4g.devops.common.constants.UMCDevOpsConstants;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.umc.rule.handler.RuleConfigHandler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.*;

/**
 * @author vjay
 * @date 2019-07-04 15:47:00
 */
public class RuleConfigManager {

	@Autowired
	private JedisService jedisService;

	@Autowired
	private RuleConfigHandler ruleConfigHandler;

	/**
	 * get InstandId by Instand,get from redis first ,if not found ,get from db
	 */
	public String getInstandId(String instance) {
		String instandId = jedisService.get(KEY_CACHE_INSTANCE_ID + instance);
		if (StringUtils.isBlank(instandId) || StringUtils.equals(instandId, NOT_FOUND)) {
			AppInstance appInstance = new AppInstance();
			List<AppInstance> instancelist = ruleConfigHandler.instancelist(appInstance);
			for (AppInstance appInstance1 : instancelist) {
				if (StringUtils.equals(instance, appInstance1.getIp() + ":" + appInstance1.getPort())) {
					// found
					instandId = String.valueOf(appInstance1.getId());
					jedisService.set(KEY_CACHE_INSTANCE_ID + appInstance1.getIp() + ":" + appInstance1.getPort(),
							String.valueOf(appInstance1.getId()), 0);
					break;
				}
			}
			jedisService.set(KEY_CACHE_INSTANCE_ID + instance, NOT_FOUND, 30);
		}
		return instandId;
	}

	/**
	 * Get Rule By instandid ,get from redis first ,if not found ,get from db
	 */
	public String getAlarmRuleInfo(String instandId) {

		String json = jedisService.get(KEY_CACHE_ALARM_RULE + instandId);
		if (StringUtils.isBlank(instandId) || StringUtils.equals(instandId, NOT_FOUND)) {
			Map<Integer, AlarmTemplate> alarmTemplateMap = getAllAlarmTemplate();
			List<AlarmConfig> alarmConfigs = ruleConfigHandler.selectAll();
			Set<Integer> templates = new HashSet<>();

			for (AlarmConfig alarmConfig : alarmConfigs) {
				String[] tags = alarmConfig.getTags().split(",");
				Integer templateId = alarmConfig.getTemplateId();
				if (Arrays.asList(tags).contains(instandId)) {
					templates.add(templateId);
				}
			}
			Set<Integer> temList = templates;
			AlarmRuleInfo alarmConfigRedis = new AlarmRuleInfo();
			alarmConfigRedis.setCollectId(Integer.parseInt(instandId));
			alarmConfigRedis.setAlarmTemplateId(temList);
			List<AlarmTemplate> alarmTemplates = new ArrayList<>();
			for (Integer tem : temList) {
				alarmTemplates.add(alarmTemplateMap.get(tem));
			}
			alarmConfigRedis.setAlarmTemplates(alarmTemplates);
			if (alarmTemplates.size() <= 0) {
				jedisService.set(KEY_CACHE_ALARM_RULE + String.valueOf(Integer.parseInt(instandId)), json, 0);
				return json;
			}
			// TODO
			json = JacksonUtils.toJSONString(alarmConfigRedis);
			jedisService.set(KEY_CACHE_ALARM_RULE + instandId, NOT_FOUND, 30);
		}

		return json;
	}

	/**
	 * Get all Template ,and cache in map
	 */
	private Map<Integer, AlarmTemplate> getAllAlarmTemplate() {
		Map<Integer, AlarmTemplate> alarmTemplateMap = new HashMap<>();
		List<AlarmTemplate> alarmTemplates = ruleConfigHandler.selectAllWithRule();
		for (AlarmTemplate alarmTemplate : alarmTemplates) {
			alarmTemplateMap.put(alarmTemplate.getId(), alarmTemplate);
		}
		return alarmTemplateMap;
	}

	/**
	 * Get point history by templateId, and save the newest value into redis
	 */
	public List<TemplateHisInfo.Point> duelTempalteInRedis(Integer templateId, Double value, Long timestamp, long now, int ttl) {
		String json = jedisService.get(UMCDevOpsConstants.KEY_CACHE_TEMPLATE_HIS + templateId);

		TemplateHisInfo templateHisRedis = null;
		if (StringUtils.isNotBlank(json)) {
			templateHisRedis = JacksonUtils.parseJSON(json, TemplateHisInfo.class);
		} else {
			templateHisRedis = new TemplateHisInfo();
		}
		List<Point> points = templateHisRedis.getPoints();
		if (CollectionUtils.isEmpty(points)) {
			points = new ArrayList<>();
		}
		List<Point> needDel = new ArrayList<>();
		for (Point point : points) {
			long t = point.getTimeStamp();
			if (now - t >= ttl) {
				needDel.add(point);
			}
		}
		points.removeAll(needDel);
		points.add(new Point(timestamp, value));
		templateHisRedis.setPoints(points);
		Collections.sort(points, new Comparator<Point>() {
			public int compare(Point arg0, Point arg1) {
				if (arg0.getTimeStamp() > (arg1.getTimeStamp())) {
					return 1;
				} else if (arg0.getTimeStamp() < (arg1.getTimeStamp())) {
					return -1;
				}
				return 0;
			}
		});
		jedisService.set(KEY_CACHE_TEMPLATE_HIS + templateId, JacksonUtils.toJSONString(templateHisRedis), ttl);

		return points;
	}

	/**
	 * Get longest time from rules
	 */
	public Long getLongestRuleKeepTime(List<AlarmRule> rules) {
		long keepTime = 0;
		for (AlarmRule alarmRule : rules) {
			if (alarmRule.getContinuityTime() > keepTime) {
				keepTime = alarmRule.getContinuityTime();
			}
		}
		return keepTime;
	}

}
