package com.wl4g.devops.umc.rule.handler;

import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;

import java.util.Date;
import java.util.List;

/**
 * Rule handler.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public interface RuleConfigHandler {

	//AlarmRuleInfo getRule(String collectId);

	//List<AlarmConfig> selectByTemplateId(Integer templateId);

	List<AppInstance> instancelist(AppInstance appInstance);

	//List<AlarmConfig> selectAll();

	List<AlarmTemplate> getAlarmTemplateByCollectId(Integer collectId);

	List<AlarmConfig> getAlarmConfigByCollectIdAndTemplateId(Integer templateId, Integer collectId);

	void saveRecord(AlarmTemplate alarmTemplate, List<AlarmConfig> alarmConfigs, Integer collectId, Long gatherTime, Date nowDate, List<AlarmRule> rules);

}
