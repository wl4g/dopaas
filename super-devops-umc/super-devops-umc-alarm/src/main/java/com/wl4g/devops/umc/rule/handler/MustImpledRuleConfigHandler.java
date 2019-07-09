package com.wl4g.devops.umc.rule.handler;

import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import org.springframework.beans.factory.InitializingBean;

import java.util.Date;
import java.util.List;

/**
 * None must implements rule handler.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public class MustImpledRuleConfigHandler implements RuleConfigHandler, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		throw new IllegalStateException(String.format("Rule handlers must be implemented '%s'", RuleConfigHandler.class));
	}

	@Override
	public List<AppInstance> instancelist(AppInstance appInstance) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<AlarmTemplate> getAlarmTemplateByCollectId(Integer collectId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<AlarmConfig> getAlarmConfigByCollectIdAndTemplateId(Integer templateId, Integer collectId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void saveRecord(AlarmTemplate alarmTemplate, List<AlarmConfig> alarmConfigs, Integer collectId, Long gatherTime, Date nowDate, List<AlarmRule> rules) {
		throw new UnsupportedOperationException();
	}

}
