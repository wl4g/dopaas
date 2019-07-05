package com.wl4g.devops.umc.rule;

import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;

/**
 * Rule handler.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public interface RuleHandler {

	/**
	 * Get rule by collectId.(agent collection point)
	 * 
	 * @param collectId
	 * @return
	 */
	AlarmRuleInfo getRule(String collectId);

}
