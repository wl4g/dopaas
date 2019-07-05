package com.wl4g.devops.umc.rule.handler;

import java.util.List;

import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;

/**
 * Rule handler.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public interface RuleConfigHandler {

	/**
	 * Get rule by collectId.(agent collection point)
	 * 
	 * @param collectId
	 * @return
	 */
	AlarmRuleInfo getRule(String collectId);

	List<AlarmConfig> selectByTemplateId(Integer templateId);

	List<AppInstance> instancelist(AppInstance appInstance);

	List<AlarmConfig> selectAll();

	List<AlarmTemplate> selectAllWithRule();

}
