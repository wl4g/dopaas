package com.wl4g.devops.umc.rule;

import org.springframework.beans.factory.InitializingBean;

import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;

/**
 * None must implements rule handler.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class NoneMustImpleRuleHandler implements RuleHandler, InitializingBean {

	@Override
	public AlarmRuleInfo getRule(String collectId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		throw new IllegalStateException(String.format("Rule handlers must be implemented '%s'", RuleHandler.class));
	}

}
