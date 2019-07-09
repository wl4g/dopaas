package com.wl4g.devops.umc.rule;

import com.google.common.net.HostAndPort;
import com.wl4g.devops.common.bean.scm.AppGroup;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;
import com.wl4g.devops.common.bean.umc.model.TemplateHisInfo;
import com.wl4g.devops.common.bean.umc.model.TemplateHisInfo.Point;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.umc.rule.handler.RuleConfigHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.*;

/**
 * @author vjay
 * @date 2019-07-04 15:47:00
 */
public class RuleConfigManager implements ApplicationRunner {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private JedisService jedisService;

	@Autowired
	private RuleConfigHandler ruleConfigHandler;
	/**
	 * get collectId by collect,get from redis first ,if not found ,get from db
	 */
	public Integer convertCollectIp(String collectIpAndPort) {
		//check ip and port
		HostAndPort hostAndPort = HostAndPort.fromString(collectIpAndPort);

		String collectId = jedisService.get(getCacheKeyIpAndPort(collectIpAndPort));
		if (StringUtils.isBlank(collectId) && !StringUtils.equals(collectId, NOT_FOUND)) {
			AppInstance appInstance = new AppInstance();
			appInstance.setIp(hostAndPort.getHostText());
			appInstance.setPort(hostAndPort.getPortOrDefault(0));
			List<AppInstance> instancelist = ruleConfigHandler.instancelist(appInstance);
			if (null != instancelist && instancelist.size() > 0) {//found
				AppInstance appInstance1 = instancelist.get(0);
				collectId = String.valueOf(appInstance1.getId());
				jedisService.set(getCacheKeyIpAndPort(collectIpAndPort),
						String.valueOf(appInstance1.getId()), 0);
			} else {
				jedisService.set(getCacheKeyIpAndPort(collectIpAndPort), NOT_FOUND, 30);
			}
		}
		return Integer.parseInt(collectId);
	}

	public Integer convertGroupId(String groupName) {
		//check ip and port
		String groupId = jedisService.get(getCacheKeyGroup2Id(groupName));
		if (StringUtils.isBlank(groupId) && !StringUtils.equals(groupId, NOT_FOUND)) {
			AppGroup appGroup = ruleConfigHandler.getAppGroupByName(groupName);

			if (null != appGroup) {//found
				groupId = appGroup.getId().toString();
				jedisService.set(getCacheKeyGroup2Id(groupName), groupId, 0);
			} else {
				jedisService.set(getCacheKeyGroup2Id(groupName), NOT_FOUND, 30);
			}
		}
		return Integer.parseInt(groupId);
	}


	/**
	 * cache all collectIp to collectId
	 */
	private void cacheCollectIp2CollectId() {
		AppInstance appInstance = new AppInstance();
		List<AppInstance> instancelist = ruleConfigHandler.instancelist(appInstance);
		for (AppInstance appInstance1 : instancelist) {
			// found
			jedisService.set(getCacheKeyIpAndPort(appInstance1.getIp() + ":" + appInstance1.getPort()),
					String.valueOf(appInstance1.getId()), 0);
		}
	}


	//TODO del all by prefix
	public void clearAll(){
		jedisService.del("umc_alarm_66");
	}

	/**
	 * Get Rule By collectid ,get from redis first ,if not found ,get from db
	 */
	public AlarmRuleInfo getAlarmRuleInfoByCollectId(Integer collectId) {
		AlarmRuleInfo alarmRuleInfo = jedisService.getJsonToObj(getCacheKeyAlarmRuleByCollectId(collectId),AlarmRuleInfo.class);
		if (null==alarmRuleInfo) {
			List<AlarmTemplate> alarmTemplates = ruleConfigHandler.getAlarmTemplateByCollectId(collectId);
			alarmRuleInfo = new AlarmRuleInfo();
			//alarmRuleInfo.setCollectId(collectId);
			alarmRuleInfo.setAlarmTemplates(alarmTemplates);
			if(alarmTemplates.size()<=0){
				jedisService.setObjectToJson(getCacheKeyAlarmRuleByCollectId(collectId),NOT_FOUND,30);
			}else{
				jedisService.setObjectToJson(getCacheKeyAlarmRuleByCollectId(collectId),alarmRuleInfo,0);
			}
		}
		return alarmRuleInfo;
	}


	public AlarmRuleInfo getAlarmRuleInfoByGroupId(Integer groupId) {
		AlarmRuleInfo alarmRuleInfo = jedisService.getJsonToObj(getCacheKeyAlarmRuleByGroupId(groupId),AlarmRuleInfo.class);
		if (null==alarmRuleInfo) {

			List<AlarmTemplate> alarmTemplates = ruleConfigHandler.getAlarmTemplateByGroupId(groupId);
			alarmRuleInfo = new AlarmRuleInfo();
			//alarmRuleInfo.setCollectId(collectId);
			alarmRuleInfo.setAlarmTemplates(alarmTemplates);
			if(alarmTemplates.size()<=0){
				jedisService.setObjectToJson(getCacheKeyAlarmRuleByGroupId(groupId),NOT_FOUND,30);
			}else{
				jedisService.setObjectToJson(getCacheKeyAlarmRuleByGroupId(groupId),alarmRuleInfo,0);
			}
		}
		return alarmRuleInfo;
	}



	//TODO cache all Alarm Rule Info when app start
	/*public void cacheAlarmRuleInfo(){

	}*/

	/**
	 * Get point history by templateId, and save the newest value into redis
	 */
	public List<TemplateHisInfo.Point> duelTempalteInRedis(Integer templateId, Double value, Long timestamp, long now, int ttl) {
		String json = jedisService.get(getCacheKeyTemplateHis(templateId));
		TemplateHisInfo templateHisRedis;
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
			if (now - t >= ttl*1000) {
				needDel.add(point);
			}
		}
		points.removeAll(needDel);
		points.add(new Point(timestamp, value));
		templateHisRedis.setPoints(points);
		points.sort(null);
		jedisService.set(getCacheKeyTemplateHis(templateId), JacksonUtils.toJSONString(templateHisRedis), ttl);

		String s = jedisService.get(getCacheKeyTemplateHis(templateId));
		log.info(s);

		return points;
	}

	/**
	 * Get longest time from rules
	 */
	public Long cacheTime(List<AlarmRule> rules) {
		long cacheTime = 0;
		for (AlarmRule alarmRule : rules) {
			if (alarmRule.getContinuityTime() > cacheTime) {
				cacheTime = alarmRule.getContinuityTime();
			}
		}
		return cacheTime;
	}

	@Override
	public void run(ApplicationArguments applicationArguments){
		//after start
		cacheCollectIp2CollectId();
	}




	private static String getCacheKeyIpAndPort(String collectIpAndPort){
		return  KEY_CACHE_INSTANCE_ID + collectIpAndPort;
	}

	private static String getCacheKeyGroup2Id(String groupName){
		return  KEY_CACHE_GROUP_ID + groupName;
	}

	private static String getCacheKeyAlarmRuleByCollectId(Integer collectId){
		return  KEY_CACHE_ALARM_RULE_COLLECT + collectId;
	}

	private static String getCacheKeyAlarmRuleByGroupId(Integer groupId){
		return  KEY_CACHE_ALARM_RULE_GROUP + groupId;
	}



	private static String getCacheKeyTemplateHis(Integer templateId){
		return KEY_CACHE_TEMPLATE_HIS + templateId;
	}
}
