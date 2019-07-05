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
import com.wl4g.devops.umc.alarm.AlarmDaoInterface;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.*;

/**
 * @author vjay
 * @date 2019-07-04 15:47:00
 */
@Component
public class RuleManager {

    @Autowired
    private JedisService jedisService;


    @Autowired
    private AlarmDaoInterface alarmDaoInterface;

    private Map<Integer, Set<Integer>> tagToTemplate = new HashMap<>();


    //after system start run
    public void putInstandIPToIdInRedis(){
        AppInstance appInstance = new AppInstance();
        List<AppInstance> instancelist = alarmDaoInterface.instancelist(appInstance);
        for(AppInstance appInstance1 : instancelist){
            jedisService.set(KEY_CACHE_INSTANCE_ID+appInstance1.getIp()+":"+appInstance1.getPort(),String.valueOf(appInstance1.getId()),0);
        }
    }

    //after system start run
    public void putAllAlarmConfigIntoRedis(){
        Map<Integer,AlarmTemplate> alarmTemplateMap = getAllAlarmTemplate();
        List<AlarmConfig> alarmConfigs = alarmDaoInterface.selectAll();
        for(AlarmConfig alarmConfig : alarmConfigs){
            String tagsStr = alarmConfig.getTags();
            Integer templateId = alarmConfig.getTemplateId();
            String[] tags = tagsStr.split(",");
            for(String tag : tags){
                int instandId = Integer.parseInt(tag);
                Set<Integer> set = tagToTemplate.get(instandId);
                if(null==set){
                    set = new HashSet<>();
                }
                set.add(templateId);
            }
        }
        for (Map.Entry<Integer, Set<Integer>> entry : tagToTemplate.entrySet()) {
            int instandId = entry.getKey();
            Set<Integer> temList = entry.getValue();
            AlarmRuleInfo alarmConfigRedis = new AlarmRuleInfo();
            alarmConfigRedis.setCollectId(instandId);
            alarmConfigRedis.setAlarmTemplateId(temList);
            List<AlarmTemplate> alarmTemplates = new ArrayList<>();
            for(Integer tem : temList){
                alarmTemplates.add(alarmTemplateMap.get(tem));
            }
            alarmConfigRedis.setAlarmTemplates(alarmTemplates);
            //TODO
            String json = JacksonUtils.toJSONString(alarmConfigRedis);
            jedisService.set(KEY_CACHE_ALARM_RULE+String.valueOf(instandId),json,0);

        }
    }


    private Map<Integer,AlarmTemplate> getAllAlarmTemplate(){
        Map<Integer,AlarmTemplate> alarmTemplateMap = new HashMap<>();
        List<AlarmTemplate> alarmTemplates = alarmDaoInterface.selectAllWithRule();
        for(AlarmTemplate alarmTemplate : alarmTemplates){
            alarmTemplateMap.put(alarmTemplate.getId(),alarmTemplate);
        }
        return alarmTemplateMap;
    }


    public List<TemplateHisInfo.Point> duelTempalteInRedis(Integer templateId, Double value, Long timestamp, long now, int ttl){
        String json = jedisService.get(UMCDevOpsConstants.KEY_CACHE_TEMPLATE_HIS+templateId);

        TemplateHisInfo templateHisRedis = null;
        if(StringUtils.isNotBlank(json)){
            templateHisRedis = JacksonUtils.parseJSON(json, TemplateHisInfo.class);
        }else{
            templateHisRedis = new TemplateHisInfo();
        }
        List<Point> points = templateHisRedis.getPoints();
        if(CollectionUtils.isEmpty(points)){
            points = new ArrayList<>();
        }
        List<Point> needDel = new ArrayList<>();
        for(Point point : points){
            long t = point.getTimeStamp();
            if(now-t>=ttl){
                needDel.add(point);
            }
        }
        points.removeAll(needDel);
        points.add(new Point(timestamp,value));
        templateHisRedis.setPoints(points);
        Collections.sort(points, new Comparator<Point>() {
            public int compare(Point arg0, Point arg1) {
                if(arg0.getTimeStamp()>(arg1.getTimeStamp())){
                    return 1;
                }else if(arg0.getTimeStamp()<(arg1.getTimeStamp())){
                    return -1;
                }
                return 0;
            }
        });

        jedisService.set(KEY_CACHE_TEMPLATE_HIS+templateId,JacksonUtils.toJSONString(templateHisRedis),ttl);

        return points;
    }

    public Long getLongestRuleKeepTime(List<AlarmRule> rules){
        long keepTime = 0;
        for(AlarmRule alarmRule : rules){
            if(alarmRule.getContinuityTime()>keepTime){
                keepTime = alarmRule.getContinuityTime();
            }
        }
        return keepTime;
    }














}
