package com.wl4g.devops.umc.rule;

import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;

public interface RuleHandler {

	/**
	 * Get rule by collectId.(agent collection point)
	 * 
	 * @param collectId
	 * @return
	 */
	AlarmRuleInfo getRule(String collectId);

}
