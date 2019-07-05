package com.wl4g.devops.umc.rule;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.KEY_CACHE_ALARM_RULE;

import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;
import com.wl4g.devops.support.cache.JedisService;

public class ServiceRuleHandler implements RuleHandler {

	@Autowired
	protected JedisService jedisService;

	@Override
	public AlarmRuleInfo getRule(String collectId) {
		return jedisService.getObjectT(getRuleCacheKey(collectId), AlarmRuleInfo.class);
	}

	/**
	 * Get rule cache key by collectId.
	 * 
	 * @param collectId
	 * @return
	 */
	protected String getRuleCacheKey(String collectId) {
		return KEY_CACHE_ALARM_RULE + collectId;
	}

}
