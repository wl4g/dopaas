package com.wl4g.devops.umc.rule.handler;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.bean.umc.model.AlarmRuleInfo;

/**
 * None must implements rule handler.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class MustImpledRuleConfigHandler implements RuleConfigHandler, InitializingBean {

	@Override
	public AlarmRuleInfo getRule(String collectId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		throw new IllegalStateException(String.format("Rule handlers must be implemented '%s'", RuleConfigHandler.class));
	}

	@Override
	public List<AlarmConfig> selectByTemplateId(Integer templateId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<AppInstance> instancelist(AppInstance appInstance) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<AlarmConfig> selectAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<AlarmTemplate> selectAllWithRule() {
		throw new UnsupportedOperationException();
	}

}
